package gpoker.players;

import gpoker.Move;
import gpoker.Player;
import org.jdom2.Element;

/**
 * This player makes a random move, but does not generate a FOLD in order to make the play less naive.
 * 
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class RandomPlayer extends Player {

	/**
	 * Creates the player via XML.
	 *
	 * @param element an XML player element
	 */
	public RandomPlayer(Element element) {

		super(element);
	}


	/**
	 * Constructs the player.
	 *
	 * @param name  the player's name
	 * @param chips the amount of chips the player has.
	 */
	public RandomPlayer(String name, int chips) {

		super(name, chips);
	}


	/**
	 * The random player's moves cannot be trusted.
	 * 
	 * @return	false
	 */
	public boolean isTrusted() {
		return false;
	}
	
	
	/**
	 * Returns a random move without a FOLD. This improves the play of Random a little bit, as FOLD after
	 * a RAISE is not a good idea. However, the player still might fold, if a CHECK is made but not possible.
	 * 
	 * @return the  move
	 */
	public Move act() {

		Move move = new Move(Move.FOLD);
		int type = (int)(Math.random() * 3) + 1;				// CHECK, CALL, and RAISE
		move.setType(type);
		if (type == Move.RAISE)
			move.setBet(getBet());

		return move;
	}
	
	
	/**
	 * Returns the amount of chips the RandomPlayer bets when raising. When raising the RandomPlayer randomly makes a min raise up to a all-in.
	 * 
	 * @return The amount of chips when raising.
	 */
	private int getBet() {

		return (int)(getChips() * Math.random());
	}

}
