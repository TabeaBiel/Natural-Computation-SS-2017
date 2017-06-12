package gpoker.gp.fitness;

import evSOLve.JEvolution.JEvolution;
import evSOLve.JEvolution.misc.Utilities;
import gpoker.Dealer;
import gpoker.Director;
import gpoker.Player;
import gpoker.misc.Xml;
import gpoker.players.GPlayer;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * The KO tournament evaluator conducts a tournament with all players of a population. The fitness of a player is the
 * sum of chips won or lost in all rounds played. Thus, in general, a player reaching a final stage will have larger
 * fitness, then a player eliminated early on.
 *
 * @author Helmut A. Mayer
 * @since May 12, 2016
 */
public class KOTournament extends FitnessEvaluator {

	/** The number of players in a game. */
	private int playersPerTable;

	/** The number of contestants. */
	private int totalEntries;

	/** All the players. */
	private List<GPlayer> players;


	/**
	 * Creates the evaluator via XML.
	 *
	 * @param element an XML player element
	 */
	public KOTournament(Element element) {

		playersPerTable = Xml.getProperty(element, "PlayersPerTable", 2);
		totalEntries = JEvolution.getInstance().getPopulationSize();
		players = new ArrayList<>(totalEntries);
	}


	/**
	 * Evaluates the fitness by first collecting all players of a population, and then conducting a KO tournament. In
	 * the tournament every player receives a fitness according to the complete chips won or lost in all rounds. As the
	 * tournament winner must win all rounds, it must have a positive fitness. The players eliminated in the first round
	 * must have a negative fitness. It might also happen, that an eliminated player has a large positive fitness, if it
	 * lost moderately but has won largely before. Theoretically, a winner could also have 0 fitness, if it got a free pass
	 * in each round (being very unlikely), hence it never played a game, and/or some of the games ended with a 0 chip tie.
	 *
	 * @param player	an evolved player
	 *
	 */
	public void evaluate(GPlayer player) {

		player.setFitness(0.0);							// reset
		players.add(player);
		if (players.size() == totalEntries)
			playTournament();

	}


	/** Conducts a complete KO tournament. If some random players do not fit into a game, they
	 * receive a free pass into the next round. As a consequence they cannot win or lose chips in this round, so their
	 * fitness remains unchanged. It then may also happen, that one of the winners is actually a free pass, however, it
	 * has reached the final stages, so it must be a good player and has an appropriate fitness.
	 */
	private void playTournament() {

		int gameCount = totalEntries / playersPerTable;

		while (gameCount > 0) {
			System.out.println("New round: " + gameCount + " games, " + players.size() + " players");
			int index = Utilities.nextIntegerInRange(0, players.size() - 1);			// first random player
			for (int i = 0; i < gameCount; i++)
				index = playGame(index);
			gameCount = players.size() / playersPerTable;
		}
		for (Player winner : players) {
			System.out.println(winner);
			System.out.println("with fitness " + ((GPlayer)winner).getFitness());
		}
		players.clear();
	}


	/** Plays a game with a given number of players. The players are drawn randomly starting at index. After the game the won
	 * or lost chips are added to the fitness. If a player has lost it is eliminated from the tournament, but it has
	 * a fitness from all the games played. If winners are tied, the first of the tied players is chosen as a winner.
	 * From that it follows that a player eliminated in the first KO round has a negative fitness (or 0).
	 *
	 * @param index		the first random player index
	 *
	 * @return			the next random player index
	 */
	@SuppressWarnings("SuspiciousMethodCalls")
	private int playGame(int index) {

		Dealer dealer = Director.getInstance().getDealer();
		List<Player> table = dealer.getPlayers();
		table.clear();

		for (int i = 0; i < playersPerTable; i++) {
			dealer.addPlayer(players.get(index));				// pick a random player
			index = ++index % players.size();                   // next is quasi-random neighbor
		}
		Player winner = dealer.start();							// play game

		for (Player p : table) {								// assign fitness
			GPlayer gp = (GPlayer)p;
			gp.setFitness(gp.getFitness() + p.getWins());		// add on to previous wins
//			if (p.getWins() == 0)
//				System.out.println("Total = " + gp.getFitness());
		}
		for (Player p : table) {								// eliminate losers
			if (p != winner)
				players.remove(p);
		}
		return (players.indexOf(winner) + 1) % players.size();
	}


	/**
	 * The string representation of the evaluator.
	 *
	 * @return a string describing the evaluator
	 */
	public String toString() {

		return "KOTournament";
	}

}
