package gpoker.gp.fitness;

import gpoker.Director;
import gpoker.Player;
import gpoker.players.GPlayer;
import org.jdom2.Element;

/**
 * The OneSeat evaluator simply places the evolved player at the table and lets it play the game against the other
 * players at the table. The fitness is then the wins of the player.
 *
 * @author Helmut A. Mayer
 * @since May 10, 2016
 */
public class OneSeat extends FitnessEvaluator {

	/**
	 * Creates the evaluator via XML.
	 *
	 * @param element an XML player element
	 */
	public OneSeat(Element element) {

	}


	/**
	 * Evaluates the fitness, by placing the player at a seat at the table. Before the old GPlayer at the table
	 * has to be replaced by this next GPlayer, which has to be evaluated. The fitness is determined by the wins of the
	 * player.
	 *
	 * @param player	an evolved player
	 *
	 */
	public void evaluate(GPlayer player) {

		Player old = Director.getInstance().getPlayer("GPlayer");
		Director.getInstance().getTournamentMode().replacePlayer(old, player);
		Director.getInstance().getTournamentMode().start();
		player.setFitness(player.getWins());
	}


	/**
	 * The string representation of the evaluator.
	 *
	 * @return a string describing the evaluator
	 */
	public String toString() {

		return "OneSeat";
	}

}
