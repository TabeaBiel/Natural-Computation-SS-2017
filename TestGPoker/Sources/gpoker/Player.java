package gpoker;

import gpoker.misc.StatSheet;
import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * A poker player.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public abstract class Player implements Comparable<Player> {

	/** Player name */
	protected String name;

	/** The amount of chips the player has. */
	private int chips;

	/** The card hand of the player. */
	private Hand hand;

	/** The chips set in a hand so far. */
	private int betsPerHand;

	/** The chips set in this stage so far. */
	private int stageBets;

	/** All moves done by the player in one hand. */
	private List<Move> moves;

	/** Indicates, if player may make a move. */
	private boolean move;

	/** The wins measure. */
	public int wins;

	/** The success indicator. */
	private long success;

	/** The player statistics. */
	private StatSheet statSheet;

	/** The peep flag indicates visible hole cards in interactive play. */
	private boolean peep;


	/**
	 * Creates the player via XML.
	 *
	 * @param element an XML player element
	 */
	public Player(Element element) {

		this(Xml.getProperty(element, "name", "NoName"), Xml.getProperty(element, "chips", 100));
		peep = Xml.getProperty(element, "peep", false);
	}


	/**
	 * Constructs the player.
	 *
	 * @param name  the player's name
	 * @param chips the amount of chips the player has.
	 */
	public Player(String name, int chips) {

		this.name = name;
		this.chips = chips;
		hand = new Hand();
		moves = new ArrayList<>();
	}


	/**
	 * Creates a player by its class name found in XML.
	 *
	 * @param element the XML element with player information
	 *
	 * @return the player, null if problems
	 */
	public static Player create(Element element) {

		String className = Xml.getProperty(element, "class", "RandomPlayer");
		className = "gpoker.players." + className;
		String[] constructorClassNames = {"org.jdom2.Element"};
		Object[] constructorParameters = {element};

		return (Player)Utilities.instantiate(className, constructorClassNames, constructorParameters);
	}


	/**
	 * Returns a deep clone.
	 *
	 * @return the cloned player
	 */
	protected Object clone() {

		Player clone = null;

		try {
			clone = (Player)super.clone();
			clone.hand = new Hand();
			clone.hand.addCards(hand.getCards());
		} catch (CloneNotSupportedException ignore) {            // should not happen
		}
		return clone;
	}


	/**
	 * Compares the success of this player with another.
	 *
	 * @param other the other player
	 *
	 * @return negative value if this is weaker, zero if equal, positive if this is stronger
	 */
	@SuppressWarnings("NullableProblems")
	public int compareTo(Player other) {

		if (this.success < other.success)
			return -1;
		if (this.success > other.success)
			return 1;
		return 0;
	}


	/**
	 * Returns the name of the player.
	 *
	 * @return the name
	 */
	public String getName() {

		return name;
	}


	/**
	 * Sets the name of the player.
	 *
	 * @param name a name
	 */
	public void setName(String name) {

		this.name = name;
	}


	/**
	 * Returns, if player may make a move.
	 *
	 * @return true, if yes
	 */
	public boolean hasMove() {

		return move;
	}


	/**
	 * Allows the player to make a move.
	 *
	 * @param move true, if allowed
	 */
	public void setMove(boolean move) {

		this.move = move;
	}


	/**
	 * Returns the bet of the player in the current stage.
	 *
	 * @return the bets
	 */
	public int getStageBets() {

		return stageBets;
	}


	/**
	 * Sets the bet of the player in the current stage.
	 *
	 * @param stageBets the bets
	 */
	public void setStageBets(int stageBets) {

		this.stageBets = stageBets;
	}


	/**
	 * Gets the overall bets of the player.
	 *
	 * @return overall bets.
	 */
	public int getBetsPerHand() {

		return betsPerHand;
	}


	/**
	 * Sets the overall bets of the player.
	 *
	 * @param betsPerHand the to be set overall bets.
	 */
	public void setBetsPerHand(int betsPerHand) {

		this.betsPerHand = betsPerHand;
	}


	/**
	 * Adds to the overall bets of the player.
	 *
	 * @param chips amount of chips to be added.
	 */
	public void addBetsPerHand(int chips) {

		betsPerHand += chips;
	}


	/**
	 * Subtracts of the hand bets of the player.
	 *
	 * @param chips number of chips to be subtracted
	 */
	public void subBetsPerHand(int chips) {

		betsPerHand -= chips;
	}


	/**
	 * Returns if the player has bet in the current hand.
	 *
	 * @return true, if player bet
	 */
	public boolean hasBet() {

		return betsPerHand > 0;
	}


	/**
	 * Returns the players cards.
	 *
	 * @return cards.
	 */
	public List<Card> getCards() {

		return hand.getCards();
	}


	/**
	 * Returns the players moves.
	 *
	 * @return moves.
	 */
	public List<Move> getMoves() {

		return moves;
	}


	/**
	 * Returns the chips of the player.
	 *
	 * @return chips.
	 */
	public int getChips() {

		return chips;
	}


	/**
	 * Sets the chip stack.
	 *
	 * @param chips the number of chips
	 */
	public void setChips(int chips) {

		this.chips = chips;
	}


	/**
	 * Removes (subtracts) chips from the players stack.
	 *
	 * @param chips amount of chips to be subtracted.
	 */
	public void removeChips(int chips) {

		this.chips -= chips;
	}


	/**
	 * Adds chips to the player's stack.
	 *
	 * @param chips number of chips to be added
	 */
	public void addChips(int chips) {

		this.chips += chips;
	}


	/**
	 * Returns the hand of the player.
	 *
	 * @return hand.
	 */
	public Hand getHand() {

		return hand;
	}


	/**
	 * Returns the wins. Note that wins may have different meanings according to the game mode (e.g., it might be the
	 * chips won or rounds won).
	 *
	 * @return the wins
	 */
	public int getWins() {

		return wins;
	}


	/**
	 * Sets the wins. Note that wins may have different meanings according to the game mode (e.g., it might be the
	 * chips won or rounds won).
	 *
	 * @param wins the wins
	 */
	public void setWins(int wins) {

		this.wins = wins;
	}


	/**
	 * Adds a win to the wins.
	 *
	 * @param win a win, may be chips or games won depending on the game mode
	 */
	public void addWin(int win) {

		wins += win;
	}


	/**
	 * Returns the success value.
	 *
	 * @return the value
	 */
	public long getSuccess() {

		return success;
	}


	/**
	 * Sets the success value.
	 *
	 * @param success a value indicating the quality of the player
	 */
	public void setSuccess(long success) {

		this.success = success;
	}


	/**
	 * Returns the stat sheet of the player.
	 *
	 * @return the stat sheet
	 */
	public StatSheet getStatSheet() {

		if (statSheet == null)
			statSheet = new StatSheet();

		return statSheet;
	}


	/**
	 * Returns the peep flag.
	 *
	 * @return true, if hole cards are visible in interactive play
	 */
	public boolean isPeep() {

		return peep;
	}


	/**
	 * Sets the peep flag.
	 *
	 * @param peep true, if hole cards are visible in interactive play
	 */
	public void setPeep(boolean peep) {

		this.peep = peep;
	}


	/**
	 * Posts the blind (big or small).
	 *
	 * @param blind the num,ber of chips
	 *
	 * @return the number of chips posted by the player, may be smaller than blind
	 */
	public int postBlind(int blind) {

		int b = blind;

		if (chips < blind)
			b = chips;                    // cannot afford blind
		chips -= b;
		stageBets = b;
		betsPerHand += b;
		Director.getInstance().getReporter().reportBlind(this, blind);

		return blind;
	}


	/**
	 * Adds a move to the moves list.
	 *
	 * @param move the move to be added
	 */
	public void addMove(Move move) {

		moves.add(move);
	}


	/**
	 * Player wins a number of chips.
	 *
	 * @param chips number of chips won
	 */
	public void win(int chips) {

		this.chips += chips;
		Director.getInstance().getReporter().reportWin(chips, this);
	}


	/**
	 * Returns if the player has chips or not
	 *
	 * @return True if the player has chips otherwise false.
	 */
	public boolean hasChips() {

		return chips > 0;
	}


	/**
	 * Resets the players hand so he can play the next hand.
	 */
	public void resetHand() {

		hand.reset();
//		moves.clear();  			// TODO activate when needed
		stageBets = 0;
		betsPerHand = 0;
	}


	/**
	 * Adds the hole cards to the players hand.
	 *
	 * @param cards The to be added cards.
	 */
	public void setHoleCards(List<Card> cards) {

		hand.addCards(cards);
	}


	/**
	 * Returns the string representation of the player.
	 *
	 * @return the player's name
	 */
	public String toString() {

		return name;
	}


	/**
	 * Returns if the player's moves can be trusted. A non-trusted player may generate illegal moves.
	 *
	 * @return true, if trusted, i.e., all moves are considered to be legal
	 */
	public abstract boolean isTrusted();


	/**
	 * Returns the move generated by the player.
	 *
	 * @return the move
	 */
	public abstract Move act();
}
