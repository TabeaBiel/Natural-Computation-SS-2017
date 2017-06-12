package gpoker.modes;

import gpoker.Dealer;
import gpoker.Director;
import gpoker.Player;
import gpoker.misc.Xml;
import org.jdom2.Element;

import java.util.List;

/**
 * The round robin tournament.
 *
 * @author Helmut A. Mayer
 * @since June 13, 2016
 */
public class RoundRobin extends TournamentMode {

	/** The number of players in a game. */
	private int playersPerTable;

	/** The win matrix. */
	private int[][] wins;


	/**
	 * Creates the evaluator via XML.
	 *
	 * @param element an XML player element
	 */
	public RoundRobin(Element element) {

		super(element);
		playersPerTable = Xml.getProperty(element, "PlayersPerTable", 2);
	}


	/** Conducts a round robin tournament. Each player plays against each other.
	 */
	public void start() {

		int size = players.size();
		wins = new int[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++)
				playGame(i, j);
		}
		Director.getInstance().getReporter().reportTournament(this);
	}


	/** Plays a game between two players and records the outcome.
	 *
	 * @param a        the first player index
	 * @param b        the second player index
	 *
	 */
	private void playGame(int a, int b) {

		Dealer dealer = Director.getInstance().getDealer();
		List<Player> table = dealer.getPlayers();
		table.clear();
		Player pa = players.get(a); 					// first player
		Player pb = players.get(b);	 					// second player

		dealer.addPlayer(pa);
		dealer.addPlayer(pb);
		dealer.start();									// play game

		wins[a][b] = pa.getWins();
		pa.setSuccess(pa.getSuccess() + pa.getWins());
		wins[b][a] = pb.getWins();
		pb.setSuccess(pb.getSuccess() + pb.getWins());
	}


	/**
	 * Returns the win matrix of the tournament.
	 *
	 * @return a result string
	 */
	public String getResult() {

		String s = "";

		for (int i = 0; i < wins.length; i++) {
			s += '\n';
			for (int j = 0; j < wins.length; j++)
				s += wins[i][j] + " ";
		}
		s += '\n';

		return s;
	}

}
