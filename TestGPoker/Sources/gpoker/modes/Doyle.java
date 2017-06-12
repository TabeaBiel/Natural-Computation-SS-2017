package gpoker.modes;

import gpoker.Director;
import gpoker.Player;
import org.jdom2.Element;

/**
 * @author Helmut A. Mayer
 * @since March 2016
 */
public class Doyle extends GameMode {

	/** Indicates if a hand has been played. */
	private boolean handPlayed;


	/**
	 * Constructs Doyle's Game from XML.
	 *
	 * @param element an XML tag
	 */
	public Doyle(Element element) {

		super(element);
		handPlayed = true;				// prepare for toggle
	}


	/** Prepares for the next round. */
	public void resetRound() {

		for (Player player : dealer.getPlayers())
			player.setChips(chipsPerPlayer);
	}


	/** Checks if a hand has been played.
	 *
	 * @return	true, if hand played
	 */
	public boolean isRoundOver() {

		handPlayed = !handPlayed;			// toggle
		return handPlayed;
	}


	/** Adds the chips won or lost to the total wins of the players. */
	public void registerRound() {

		for (Player player : dealer.getPlayers()) {
			int win = player.getChips() - chipsPerPlayer;
			player.addWin(win);
		}

	}


	/** Checks if all rounds (hands) have been played.
	 *
	 * @return	true, if no more rounds
	 */
	public boolean isGameOver() {

		return currentRound++ == rounds;
	}


	/** Does some housekeeping after a game has been completed. */
	public void registerGame() {

		Director.getInstance().getReporter().reportPostGame("chip");
	}


	/**
	 * Returns the game winner, which is the player who has won the most chips. In case of a (rare) tie, this method
	 * returns only one of the winners, but you may ask the players about their wins later.
	 *
	 * @return 	the winner
	 */
	public Player getWinner() {

		Player winner = dealer.getPlayers().get(0);
		int maxWins = winner.getWins();

		for (Player player : dealer.getPlayers()) {
			if (player.getWins() > maxWins) {
				maxWins = player.getWins();
				winner = player;
			}
		}
		return winner;
	}


	/** Returns game mode information.
	 *
	 * @return a string
	 */
	public String toString() {

		return "Doyle's Game" + super.toString();
	}

}
