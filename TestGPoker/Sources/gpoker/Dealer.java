package gpoker;

import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import gpoker.modes.GameMode;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * The dealer handles the play of each hand. It defines the order of play, checks the moves of the players, keeps an
 * eye on the pot and player stacks, determines winner(s) of a hand, and distributes the chips to them.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class Dealer {

	/** The hand stages. */
	public static final int NEW_HAND = -1;
	public static final int PRE_FLOP = 0;
	public static final int FLOP = 1;
	public static final int TURN = 2;
	public static final int RIVER = 3;


	/** Deck of Cards. */
	private final Deck deck;

	/** Community cards. */
	private final List<Card> community;

	/** ALL players on the table. Includes all-in, fold, broke, active players. */
	private final List<Player> players;

	/** All active Players who are currently playing the hand. Includes only All-in and active players. */
	private final List<Player> activePlayers;

	/** The player with the dealer button. */
	private Player button;

	/** The stage of the current hand. */
	private int stage;

	/** The current pot. */
	private int pot;

	/** The current maximal stage bet of a player. */
	private int betLevel;

	/** The evaluator of cards. */
	private Evaluator evaluator;

	/** A utility list. */
	private List<Player> winners;

	/** The game mode. */
	private GameMode gameMode;

	/**
	 * Constructor via XML config-file.
	 *
	 * @param element an XML Dealer element.
	 */
	public Dealer(Element element) {

		this();
		boolean dup = Xml.getProperty(element, "duplicateHands", false);
		deck.setDuplicate(dup);
	}


	/**
	 * Constructor.
	 *
	 */
	public Dealer() {

		players = new ArrayList<>();
		activePlayers = new ArrayList<>();
		winners = new ArrayList<>(1);
		deck = new Deck();
		community = new ArrayList<>(5);
		evaluator = new TexasEvaluator();
	}


	/** Creates a dealer by its class name found in XML. This is intended for future use, as there now is
	 * only one dealer class, however it works also now.
	 *
	 * @param element	the XML element with dealer information
	 *
	 * @return          the dealer, null if problems
	 */
	public static Dealer create(Element element) {

		String className = Xml.getProperty(element, "class", "Dealer");
		className = "gpoker." + className;
		String[] constructorClassNames = {"org.jdom2.Element"};
		Object[] constructorParameters = {element};

		return (Dealer)Utilities.instantiate(className, constructorClassNames, constructorParameters);
	}


	/**
	 * Returns all active players in a List<Player> format.
	 *
	 * @return All active players.
	 */
	public List<Player> getActivePlayers() {

		return activePlayers;
	}


	/**
	 * Returns all players in a List<Player> format.
	 *
	 * @return All players.
	 */
	public List<Player> getPlayers() {

		return players;
	}


	/** Returns the player with the dealer button.
	 *
	 * @return	the player
	 */
	public Player getButton() {

		return button;
	}


	/** Returns the stage of the hand.
	 *
	 * @return	the stage, e.g. PRE_FLOP
	 */
	public int getStage() {

		return stage;
	}


	/**
	 * Returns the community cards of the table.
	 *
	 * @return The community cards.
	 */
	public List<Card> getCommunity() {

		return community;
	}


	/** Returns the current pot. */
	public int getPot() {

		return pot;
	}


	/** Returns the evaluator.
	 *
	 * @return	the evaluator
	 */
	public Evaluator getEvaluator() {

		return evaluator;
	}


	/**
	 * Returns the bet level.
	 *
	 * @return	the level for a call
	 */
	public int getBetLevel() {

		return betLevel;
	}


	/**
	 * Returns the minimal amount of chips necessary to raise.
	 *
	 * @return minimal raise.
	 */
	public int getMinRaise() {

		return gameMode.getBigBlind();
	}


	/** Returns the game mode.
	 *
	 * @return	the game mode
	 */
	public GameMode getGameMode() {

		return gameMode;
	}


	/** Sets the game mode.
	 *
	 * @param gameMode    the mode of play, e.g., tournament
	 */
	public void setGameMode(GameMode gameMode) {

		this.gameMode = gameMode;
	}


	/**
	 * Add a player to the players list.
	 *
	 * @param player a player.
	 */
	public void addPlayer(Player player) {

		players.add(player);
	}


	/** Starts a complete game and returns the winner.
	 *
	 * @return	the winner
	 */
	public Player start() {

		gameMode.reset();

		while (!gameMode.isGameOver()) {
			while (!gameMode.isRoundOver())
				playHand();
			gameMode.registerRound();
			gameMode.resetRound();
		}
		gameMode.registerGame();

		return gameMode.getWinner();
	}


	/** Prepares for a new round. */
	public void reset() {

		button = players.get(0);
	}


	/**
	 * To play one hand (Texas Hold'em rule set).
	 */
	private void playHand() {

		prepareHand();
		dealHoleCards(2);											// two cards for players

		Player player = button;
		if (activePlayers.size() > 2)								// in heads-up the dealer is small blind
			player = getNextPlayer(player);
		pot += player.postBlind(gameMode.getBigBlind() / 2);		// small blind
		player = getNextPlayer(player);
		pot += player.postBlind(gameMode.getBigBlind());			// big blind
		betLevel = gameMode.getBigBlind();
		Director.getInstance().getReporter().reportPlayerStatus();

		for (int i = 0; i < 4; i++) {								// maximally four rounds
			if (!doBettingRound(player))
				break;                                      		// hand is finished
			for (Player p : activePlayers)
				p.setStageBets(0);
			betLevel = 0;
			++stage;
			if (i == 0)
				dealCommunityCards(3);							// flop
			else if (i < 3)
				dealCommunityCards(1);							// turn, river
			Director.getInstance().getReporter().reportPlayerStatus();
		}
		showDown();
	}



	/** Performs a complete round of betting. Basically, it gives every active player the right for a move. Then,
	 * all the active players make their moves. If only one active player is left during the round, the round is prematurely
	 * finished.
	 *
	 * @param player	the active player
	 *
	 * @return 			true if round has normally finished, false, if only one active player is left
	 */
	private boolean doBettingRound(Player player) {

		if (stage > PRE_FLOP)
			player = button;									// post-flop
		player = getNextPlayer(player);
		allowMoves();											// all active may make a move

		while (player.hasMove()) {
			if (player.hasChips()) {
				Move move = player.act();

				if (!player.isTrusted() || move.getType() == Move.RAISE)
					checkMove(player, move);						// is it allowed?
				executeMove(player, move);
//				activePlayer.addMove(move);							// TODO activate when needed, change resetHand()!!
				if (activePlayers.size() == 1)
					return false;                                    // end of hand
			}
			else
				Director.getInstance().getReporter().reportAllIn(player);
			player.setMove(false);									// has had its right to move
			player = getNextPlayer(player);
		}
		return true;												// hand continues
	}


	/** Allows a move for all active players. */
	private void allowMoves() {

		for (Player p : activePlayers)
			p.setMove(true);
	}


	/**
	 * Checks if a move of a player is legal. If it is not, the move is changed accordingly.
	 *
	 *
	 * @param player    the player
	 * @param move     	the move announced by the player
	 *
	 */
	private void checkMove(Player player, Move move) {

		if (move.getType() == Move.CHECK) {
			if (player.getStageBets() != betLevel)            // someone raised before?
				move.setType(Move.FOLD);
		} else if (move.getType() == Move.CALL) {
			if (player.getStageBets() == betLevel)            // no raise to call?
				move.setType(Move.CHECK);
		} else if (move.getType() == Move.RAISE) {
			int raise = move.getBet();
			int minRaise = gameMode.getBigBlind();
			if (raise < betLevel + minRaise)   							// handles blinds and re-raise
				raise = betLevel + minRaise;   							// effective min raise
			int maxRaise = player.getStageBets() + player.getChips();
			if (raise > maxRaise )
				raise = maxRaise;										// player may only raise his stack
			move.setBet(raise);                               			// now it is correct
		}
	}
		

	/**
	 * Executes a move.
	 *
	 * @param player	the player
	 * @param move		the move
	 */
	private void executeMove(Player player, Move move) {

		switch (move.getType()) {
			case Move.CHECK:
				break;
			case Move.CALL:
				int toCall = betLevel - player.getStageBets();			// chips needed for call
				if (player.getChips() < toCall)
					toCall = player.getChips();								// player is all-in
				player.removeChips(toCall);
				player.setStageBets(player.getStageBets() + toCall);
				player.addBetsPerHand(toCall);
				pot += toCall;
				break;
			case Move.RAISE:
				int chips = move.getBet() - player.getStageBets();
				if (move.getBet() > betLevel)								// player is not all-in
					betLevel = move.getBet();
				player.removeChips(chips);
				pot += chips;
				player.addBetsPerHand(chips);
				player.setStageBets(move.getBet());
				allowMoves();										// all active players may react to the raise
				break;
			case Move.FOLD:
				activePlayers.remove(player);
				break;
		}
		Director.getInstance().getReporter().reportMove(player, move);
	}


	/** Handles evaluation of winner(s), tie breaks, and distribution of pot to winner(s). */
	private void showDown() {

		if (activePlayers.size() == 1) {					// the only active player wins the pot
			activePlayers.get(0).win(pot);
			pot = 0;										// all paid out
			return;
		}
		for (Player player : activePlayers)    				// evaluate hands
			evaluate(player.getHand());
		Director.getInstance().getReporter().reportShowDown(activePlayers, community);	// show down hands

		while (pot > 0) {                                   // still some chips to distribute

			if (activePlayers.size() == 0) {				// all winners got their money, but there is some extra left
				for (Player p : players) {                  // give it to the first who is still playing
					if (p.hasChips()) {                     // may lead to chips for a player who did not play this round
						p.win(pot);                         // TODO improve if needed
						return;
					}
				}
			}
			int bestHand = Hand.NOT_EVALUATED;
			winners.clear();

			for (Player player : activePlayers) {				// evaluate winner(s)
				int nextHand = player.getHand().getValue();
				if (nextHand < bestHand)
					continue;
				if (nextHand > bestHand) {
					winners.clear();
					bestHand = nextHand;
				}
				winners.add(player);							// if next hand is better or equal
			}

			/* Resolve multiple winners using tie breaks. */
			for (int i = winners.size() - 1; i > 0; i--) {
				int winner = tieBreak(winners.get(i).getHand(), winners.get(i - 1).getHand());
				if (winner == 0)
					continue;													// keep them
				if (winner == 1)
					winners.remove(i - 1);                                   	// remove loser
				else {
					for (int j = winners.size() - 1; j > i - 1 ; j--)          	// remove loser and its potential ties
						winners.remove(j);
				}
			}

			/* This handles normal pots, split pots and side pots in a general way. */
			Player minWinner = winners.get(0);
			int minBet = minWinner.getBetsPerHand();							// the minimal bet of the winners

			for (Player p : winners) {                                          // handles potential side pots
				if (p.getBetsPerHand() < minBet)
					minBet = p.getBetsPerHand();
					minWinner = p;                                              // min winner has contributed the least
			}
			int wins = 0;
			for (Player p : players) {											// calculate wins
				if (p.hasBet()) {                                               // has bet in round?
					int win = Math.min(p.getBetsPerHand(), minBet);				// players contribute max the min bet
					p.subBetsPerHand(win);
					wins += win;
				}
			}
			int winShare = getWinShare(wins, winners.size());					// handles potential split pots
			for (Player p : winners) {
				pot -= winShare;                                                // distribute the share among winners
				p.win(winShare);
			}
			activePlayers.remove(minWinner);									// this one is not in next side pot
		}
		if (pot != 0)
			System.err.println("Panic! The empty pot has " + pot + " chips.");  // should not happen
	}





	/** Returns the win share of a number of players.
	 *
	 * @param chips		the chips to be shared
	 * @param count     the number of players
	 *
	 * @return          the share rounded in small blinds
	 */
	private int getWinShare(int chips, int count) {

		if (count == 1)
			return chips;								// nothing to share
		int blind = gameMode.getBigBlind() / 2;
		chips -= chips % blind;							// cut off excess
		int blinds = chips / blind;                    // number of small blinds

		return (blinds / count) * blind;               // the share in chips
	}


	/**
	 * Evaluates a hand.
	 *
	 * @param hand		the hand to be evaluated
	 *
	 * @return 			the hand value
	 */
	public int evaluate(Hand hand) {

		evaluator.evaluate(hand, community);
		return hand.getValue();
	}


	/** Returns the tie break value of two hands, which already have been evaluated.
	 *
	 * @param hand1		first hand
	 * @param hand2     second hand
	 *
	 * @return          0, if equal, 1 if first hand, 2 if second hand wins
	 */
	private int tieBreak(Hand hand1, Hand hand2) {

		return evaluator.resolve(hand1, hand2, community);
	}


	/** Prepares for a new hand. */
	private void prepareHand() {

		community.clear();
		betLevel = 0;
		pot = 0;
		stage = PRE_FLOP;
		activePlayers.clear();
		for (Player player : players) {
			player.resetHand();							// keep it here!
			if (player.hasChips())
				activePlayers.add(player);
		}
		button = getNextPlayer(button);                	// next active is now dealer
		deck.shuffle();
		Director.getInstance().getReporter().reportStage();
	}


	/**
	 * Rotate to the next active player.
	 *
	 * @param player	the current active player
	 *
	 * @return 			the next active player
	 *
	 */
	private Player getNextPlayer(Player player) {

		int pos = players.indexOf(player);				// may be -1, but it still works

		do {
			pos = ++pos % players.size();
			player = players.get(pos);
		}
		while (!activePlayers.contains(player));

		return player;
	}


	/** Returns the position of an active player. The dealer position is 0, small blind is 1, big blind is 2, etc.
	 * Hence the position number indicates the distance to the dealer wrt the order of play.
	 *
	 * @param player	the player we want the position of
	 *
	 * @return          the position, -1 if given player is not active
	 */
	public int getPosition(Player player) {		// TODO improve by having activePlayers start with button

		if (player == button)
			return 0;
		int pos = 1;
		Player p = getNextPlayer(button);

		while (p != player) {
			p = getNextPlayer(p);
			++pos;
			if (pos > activePlayers.size())
				return -1;										// not in active players
		}
		return pos;
	}


	/** Returns the current bet the player has to make for a call.
	 *
	 * @param player	an active player
	 *
	 * @return          the bet for a call
	 *
	 */
	public int getCallBet(Player player) {

		return betLevel - player.getStageBets();
	}


	/** Returns the total chip count of all players.
	 *
	 * @return	sum of all the chips on the table
	 */
	public int getTotalChips() {

		int sum = 0;

		for (Player player : players)
			sum += player.getChips();

		return sum + pot;
	}


	/**
	 * Deals hole cards in the correct order being necessary for the duplicate hand system to work. If activated the
	 * deck is only shuffled after each active player has played the same hand(s).
	 */
	private void dealHoleCards(int numberOfCards) {

		Player player = button;
		do {
			player = getNextPlayer(player);
			deck.draw(numberOfCards, player.getCards());
		} while (player != button);
	}


	/**
	 * Deals community cards.
	 *
	 * @param numberOfCards Number of dealt cards.
	 */
	private void dealCommunityCards(int numberOfCards) {

		deck.draw(numberOfCards, community);
		Director.getInstance().getReporter().reportStage();
	}



}
