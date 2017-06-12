package gpoker.players;

import gpoker.Move;
import gpoker.Player;
import org.jdom2.Element;

/**
 * This player always calls, so play is always continued, which is useful for tests.
 *
 * @author Helmut A. Mayer
 */
public class CallPlayer extends Player {


	/**
	 * Creates the player via XML.
	 *
	 * @param element an XML player element
	 */
	public CallPlayer(Element element) {

		super(element);
	}


	/**
	 * Constructs the player.
	 *
	 * @param name  the player's name
	 * @param chips the amount of chips the player has.
	 */
	public CallPlayer(String name, int chips) {

		super(name, chips);
	}


	/**
	 * The moves cannot be trusted, as CALL is not always correct.
	 *
	 * @return false.
	 */
	public boolean isTrusted() {

		return false;
	}


	/**
	 * Returns always CALL.
	 *
	 * @return the CALL move
	 */
	public Move act() {

		return new Move(Move.CALL);
	}

}
