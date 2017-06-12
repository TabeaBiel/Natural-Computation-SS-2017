package gpoker.modes;

import gpoker.Dealer;
import gpoker.Director;
import gpoker.Player;
import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import org.jdom2.Element;

/** The game mode defines the mode of poker play, e.g. a tournament. A {@code round} is a concept separating the game
 * into sub-units which are repeated. In a tournament a round is a complete tournament, but there could be played another one
 * (if rounds = 2). In Doyle's Game a round is a single hand.
 *
 * @author Helmut A. Mayer
 * @since 25/03/16
 */
public abstract class GameMode {

	/** The dealer for cooperation. */
	protected Dealer dealer;

	/** The number of repetitions. */
	protected int rounds;

	/** The chips per player at start of a round.*/
	protected int chipsPerPlayer;

	/** The big blind. */
	private int bigBlind;

	/** The current round of the game. */
	protected int currentRound;


	/** Constructs a basic game. */
	public GameMode() {

		rounds = 1;
		chipsPerPlayer = 1000;
		bigBlind = 20;
		dealer = Director.getInstance().getDealer();
	}


	/** Constructs a game from XML.
	 *
	 * @param element	an XML tag
	 */
	public GameMode(Element element) {

		rounds = Xml.getProperty(element, "rounds", 1);
		chipsPerPlayer = Xml.getProperty(element, "chipsPerPlayer", 1000);
		bigBlind = Xml.getProperty(element, "bigBlind", 20);
		dealer = Director.getInstance().getDealer();
	}


	/** Creates a game mode by its class name found in XML.
	 *
	 * @param element	the XML element with game mode information
	 *
	 * @return          the game mode, null if problems
	 */
	public static GameMode create(Element element) {

		String className = Xml.getProperty(element, "class", "SitAndGo");
		className = "gpoker.modes." + className;
		String[] constructorClassNames = {"org.jdom2.Element"};
		Object[] constructorParameters = {element};

		return (GameMode)Utilities.instantiate(className, constructorClassNames, constructorParameters);
	}


	/** Returns the number of rounds.
	 *
	 * @return	the rounds of the game
	 */
	public int getRounds() {

		return rounds;
	}


	/** Returns the big blind.
	 *
	 * @return	the chips needed for big blind
	 */
	public int getBigBlind() {

		return bigBlind;
	}


	/** Sets the current round to 0, resets wins of players, resets the round and reports pre game information. */
	public void reset() {

		currentRound = 0;
		for (Player player : dealer.getPlayers())
			player.setWins(0);

		resetRound();
		Director.getInstance().getReporter().reportPreGame(this);
	}


	/** Returns common game mode information.
	 *
	 * @return	a string
	 */
	public String toString() {

		return " (" + rounds + " rounds, " + chipsPerPlayer + " chips)";
	}


	/** Prepares for the next round. */
	public abstract void resetRound();


	/** Checks if a a game round  is over.
	 *
	 * @return true, if a round is over
	 */
	public abstract boolean isRoundOver();


	/** Does some housekeeping after a round has been completed. */
	public abstract void registerRound();


	/** Checks if all rounds have been played.
	 *
	 * @return	true, if no more rounds
	 */
	public abstract boolean isGameOver();

	/** Does some housekeeping after a game has been completed. */
	public abstract void registerGame();


	/**
	 * Returns the game winner..
	 *
	 * @return 	the winner
	 */
	public abstract Player getWinner();

}
