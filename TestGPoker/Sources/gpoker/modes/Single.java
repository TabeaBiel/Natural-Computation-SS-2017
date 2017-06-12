package gpoker.modes;

import org.jdom2.Element;

/** This mode is the default mode, which just lets the dealer play the game. The only parameter considered here is the
 * number of rounds, i.e., the game is played that many rounds.
 *
 * @author Helmut A. Mayer
 * @since June 10, 2016
 */
public class Single extends TournamentMode {


	/** Constructs a basic tournament.
	 *
	 * @param rounds	the number of rounds
	 */
	public Single(int rounds) {

		super(rounds);
	}


	/**
	 * Constructs a single plain tournament from XML.
	 *
	 * @param element an XML tag
	 */
	public Single(Element element) {

		super(element);
	}


	/** Starts the tournament by simply letting the dealer do its work under the set game mode. However, the tournament
	 * rounds are considered, so the dealer plays n rounds of a game, which may also consist of a number of rounds.
	 *
	 */
	public void start() {

		dealer.getPlayers().clear();
		dealer.getPlayers().addAll(players);

		for (int i = 0; i < rounds; i++)
			dealer.start();

	}


	/**
	 * Returns the result of the tournament.
	 *
	 * @return a result string
	 */
	public String getResult() {

		return "Implement, if needed.";
	}

}
