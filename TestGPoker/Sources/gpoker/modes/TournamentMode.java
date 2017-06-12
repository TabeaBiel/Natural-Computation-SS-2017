package gpoker.modes;

import gpoker.Dealer;
import gpoker.Director;
import gpoker.Player;
import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import gpoker.players.CulturePlayer;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/** The tournament mode defines the structure of a tournament. A {@code round} is a concept separating the tournament
 * into sub-units which are repeated. Note that the tournament mode is different from the game mode. The latter defines
 * how the poker game is played, the former defines in what order and structure the games are played to find a winner.
 *
 * @author Helmut A. Mayer
 * @since June 10, 2016
 */
public abstract class TournamentMode {

	/** The dealer for cooperation. */
	protected Dealer dealer;

	/** All the players. */
	protected List<Player> players;

	/** The number of repetitions. */
	protected int rounds;

	/** The current round of the game. */
	protected int currentRound;


	/** Constructs a basic tournament. */
	public TournamentMode(int rounds) {

		this.rounds = rounds;
		dealer = Director.getInstance().getDealer();
	}


	/** Constructs a tournament from XML.
	 *
	 * @param element	an XML tag
	 */
	public TournamentMode(Element element) {

		this(Xml.getProperty(element, "rounds", 1));
		players = new ArrayList<>();
	}


	/** Creates a tournament mode by its class name found in XML.
	 *
	 * @param element	the XML element with tournament mode information
	 *
	 * @return          the tournament mode, null if problems
	 */
	public static TournamentMode create(Element element) {

		String className = Xml.getProperty(element, "class", "Single");
		className = "gpoker.modes." + className;
		String[] constructorClassNames = {"org.jdom2.Element"};
		Object[] constructorParameters = {element};

		return (TournamentMode)Utilities.instantiate(className, constructorClassNames, constructorParameters);
	}


	/** Returns the players in the tournament.
	 *
	 * @return	the players
	 */
	public List<Player> getPlayers() {

		return players;
	}


	/**
	 * Adds a player to the tournament roster.
	 *
	 * @param player a player participating in the tournament
	 */
	public final void addPlayer(Player player) {

		if (player instanceof CulturePlayer) {								// not too pretty but simple
			if (((CulturePlayer)player).isDissect()) {                        // instead of the culture player
				players.addAll(((CulturePlayer)player).getCulture());       // add all masters of the culture
				return;
			}
		}
		players.add(player);
	}


	/**
	 * Adds a number of players to the tournament roster.
	 *
	 * @param players		players participating in the tournament
	 */
	public void addPlayers(List<Player> players) {

		this.players.addAll(players);
	}


	/** Removes the given player.
	 *
	 * @param player	a player
	 */
	public void removePlayer(Player player) {

		players.remove(player);
	}


	/**
	 * Exchanges a player in the tournament.
	 *
	 * @param out     	the player leaving the tournament
	 * @param in        the player joining the tournament
	 *
	 */
	public void replacePlayer(Player out, Player in) {

		for (Player p : players) {
			if (p == out) {
				int i = players.indexOf(p);
				players.set(i, in);
				return;
			}
		}
	}


	/** Returns the number of rounds.
	 *
	 * @return	the rounds of the tournament
	 */
	public int getRounds() {

		return rounds;
	}


	/** Returns the unqualified class name.
	 *
	 * @return	a string
	 */
	public String toString() {

		return Utilities.cutHeadAtLast(this.getClass().getName(), '.');
	}


	/** Starts the tournament. */
	public abstract void start();


	/** Returns the result of the tournament.
	 *
	 * @return	a result string
	 */
	public abstract String getResult();



}