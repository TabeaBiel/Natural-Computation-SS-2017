package gpoker.modes;

import gpoker.Director;
import gpoker.Player;
import org.jdom2.Element;

/**
 * @author Helmut A. Mayer
 * @since 24/03/16
 */
public class SitAndGo extends GameMode {


	/**
	 * Constructs a tournament from XML.
	 *
	 * @param element an XML tag
	 */
	public SitAndGo(Element element) {

		super(element);
	}


	/** Prepares for the next round. */
	public void resetRound() {

		for (Player player : dealer.getPlayers())
			player.setChips(chipsPerPlayer);
		dealer.reset();
	}


	/** Checks if a tournament round  is over.
	 *
	 * @return true, if a single tournament is over
	 */
	public boolean isRoundOver() {

		int count = 0;

		for (Player player : dealer.getPlayers()) {
			if (player.hasChips())
				++count;              			// count players with chips left
		}
		return count == 1;						// one player has all the chips?
	}


	/** Adds a win to the winner of the tournament. */
	public void registerRound() {

		for (Player player : dealer.getPlayers()) {
			if (player.hasChips()) {
				player.addWin(1);					// won the tournament
				return;
			}
		}
	}


	/** Checks if all rounds have been played.
	 *
	 * @return	true, if no more rounds
	 */
	public boolean isGameOver() {

		return currentRound++ == rounds;
	}


	/** Reports on the success of the players in the game. */
	public void registerGame() {

		Director.getInstance().getReporter().reportPostGame("SitNGo");
	}


	/**
	 * Returns the tournament winner.
	 *
	 * @return 	the winner
	 */
	public Player getWinner() {

		for (Player player : dealer.getPlayers()) {
			if (player.hasChips())    							// the last one with chips
				return player;
		}
		return null;                           					// should not happen
	}


	/** Returns game mode information.
	 *
	 * @return a string
	 */
	public String toString() {

		return "SitAndGo" + super.toString();
	}

}
