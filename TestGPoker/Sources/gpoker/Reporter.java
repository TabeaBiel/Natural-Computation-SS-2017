package gpoker;

import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import gpoker.modes.GameMode;
import gpoker.modes.TournamentMode;
import org.jdom2.Element;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * The reporter gathers information and optionally reports it.
 *
 * @author Helmut A. Mayer
 * @since March, 2016
 */
public class Reporter {

	/** The report levels. */
	public static final int QUIET = 0;

	public static final int MINIMAL = 1;

	public static final int BRIEF = 2;

	public static final int VERBOSE = 3;

	public static final int INTERACTIVE = 4;

	private int level;


	/** Constructs the reporter from XML.
	 *
	 * @param element	the XML element
	 *
	 */
	public Reporter(Element element) {

		level = Xml.getProperty(element, "level", MINIMAL);
	}


	/**
	 * Sets the report level.
	 *
	 * @param level how much is reported
	 */
	public void setLevel(int level) {

		this.level = level;
	}


	public void reportBlind(Player player, int blind) {

		if (level < VERBOSE)
			return;

		int big = Director.getInstance().getDealer().getGameMode().getBigBlind();
		String b = blind == big ? "big" : "small";
		System.out.println(player + " " + b + " blind: " + player.getStageBets());
	}


	/** Reports information on all active players whose peep flag is set. */
	public void reportPlayerStatus() {

		if (level < VERBOSE)
			return;

		Director director = Director.getInstance();
		for (Player player : director.getDealer().getActivePlayers()) {
			if (player.isPeep()) {
				System.out.println(player + "'s cards: " + player.getHand());
				System.out.println("Chips: " + player.getChips() + ", Pot: " + director.getDealer().getPot() + '\n');
			}
		}
	}


	/**
	 * Report on all-in player.
	 *
	 * @param player the player
	 */
	public void reportAllIn(Player player) {

		if (level >= VERBOSE)
			System.out.println(player + " is all-in");
	}


	/**
	 * Reports stage information.
	 */
	public void reportStage() {

		if (level < VERBOSE)
			return;

		Dealer dealer = Director.getInstance().getDealer();

		if (dealer.getStage() == Dealer.PRE_FLOP) {
			if (level == INTERACTIVE) {
				System.out.println("<Enter> to continue...");
				Scanner sc = new Scanner(System.in);
				sc.nextLine();
			}
			System.out.println("Dealer " + dealer.getButton());
		} else
			System.out.println("Community Cards " + dealer.getCommunity());
	}


	/**
	 * Reports on the move of a player.
	 *
	 * @param player the player
	 * @param move   the move
	 */
	public void reportMove(Player player, Move move) {

		if (level >= VERBOSE) {
			System.out.print(player + " " + move + "s");
			if (move.getType() == Move.RAISE)
				System.out.println(" " + move.getBet() + " chips");
			else
				System.out.println();
		}
	}


	/**
	 * Reports on the hand of players at show down with community cards.
	 *
	 * @param players   the players showing down
	 * @param community the community cards
	 */
	public void reportShowDown(List<Player> players, List<Card> community) {

		if (level < VERBOSE)
			return;

		System.out.println("--- Show down ---");
		System.out.println("Community Cards " + community);

		for (Player player : players) {
			System.out.print(player + "'s cards: ");
			System.out.print(player.getHand());
			String value;

			switch (player.getHand().getValue()) {
				case Hand.HIGH_CARD:
					value = "High Card";
					break;
				case Hand.ONE_PAIR:
					value = "One Pair";
					break;
				case Hand.TWO_PAIR:
					value = "Two Pair";
					break;
				case Hand.THREE_OF_A_KIND:
					value = "Three of a Kind";
					break;
				case Hand.STRAIGHT:
					value = "Straight";
					break;
				case Hand.FLUSH:
					value = "Flush";
					break;
				case Hand.FULL_HOUSE:
					value = "Full House";
					break;
				case Hand.FOUR_OF_A_KIND:
					value = "Four of a Kind";
					break;
				case Hand.STRAIGHT_FLUSH:
					value = "Straight Flush";
					break;
				default:
					value = "Not Evaluated";
					break;
			}
			System.out.println("  " + value);
		}
		System.out.println();
	}


	/**
	 * Report on win of a player.
	 *
	 * @param chips  the number of chips won
	 * @param player the winner
	 */
	public void reportWin(int chips, Player player) {

		if (level > MINIMAL)
			System.out.println(player + " wins " + chips + " chips.");
	}


	/**
	 * Reports game information before the game starts.
	 *
	 * @param mode the game mode
	 */
	public void reportPreGame(GameMode mode) {

		if (level > QUIET) {
			System.out.println("--- " + mode + " ---");
			for (Player player : Director.getInstance().getDealer().getPlayers())
				System.out.println(player + " has " + player.getChips() + " chips.");
			System.out.println();
		}
	}


	/**
	 * Reports on the wins of the players after a game.
	 *
	 * @param meaning what the wins are
	 */
	public void reportPostGame(String meaning) {

		if (level > QUIET) {
			Dealer dealer = Director.getInstance().getDealer();
			System.out.println("--- Game Report ---");

			List<Player> players = dealer.getPlayers();
			for (Player player : players)
				System.out.println(player + " won " + player.getWins() + " "
						+ Utilities.buildPlural(player.getWins(), meaning));
		}
	}


	/**
	 * Reports on the wins of the players after a game.
	 *
	 * @param tm	the tournament mode
	 */
	public void reportTournament(TournamentMode tm) {

		if (level > QUIET) {
			System.out.println("\n--- Tournament Report ---");
			System.out.println("Mode: " + tm);

			List<Player> players = tm.getPlayers();
			for (Player player : players)
				System.out.print(player + " ");
			System.out.println(tm.getResult());
			Collections.sort(players, Collections.reverseOrder());
			System.out.println("Ranking:");
			int rank = 1;
			for (Player p : players) {
				System.out.print(rank++ + ". ");
				System.out.println(p + ": " + p.getSuccess());
			}
		}
	}

}
