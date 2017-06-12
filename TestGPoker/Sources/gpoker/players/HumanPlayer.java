package gpoker.players;

import gpoker.Director;
import gpoker.Move;
import gpoker.Player;
import gpoker.Reporter;
import org.jdom2.Element;

import java.util.Scanner;

/**
 * A human player who gets information over the console and gives information over System.in (console input).
 * 
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 *
 */
public class HumanPlayer extends Player {

	/** The scanner for keyboard input. */
	Scanner scanner = new Scanner(System.in);


	/**
	 * Creates the player via XML.
	 *
	 * @param element an XML player element
	 */
	public HumanPlayer(Element element) {

		super(element);
		Director.getInstance().getReporter().setLevel(Reporter.INTERACTIVE);		// for interactive play
		setPeep(true);
	}


	/**
	 * Constructs the player.
	 *
	 * @param name  the player's name
	 * @param chips the amount of chips the player has.
	 */
	public HumanPlayer(String name, int chips) {

		super(name, chips);
	}


	/**
	 * The human player's moves cannot be trusted.
	 * 
	 * @return false
	 */
	public boolean isTrusted() {
		return false;
	}
	

	/**
	 * Asks the player to return a move.
	 * 
	 * @return the move from the strategy.
	 */
	public Move act() {

		Move move = null;

		System.out.println("Your move? [F(old), (chec)K, C(all), R(aise)]");

		while (move == null) {
			String action = scanner.nextLine();
			if (action.length() == 0)
				continue;
			Character c = action.charAt(0);

			switch (c) {
				case 'f':
					move = new Move(Move.FOLD);
					break;
				case 'k':
					move = new Move(Move.CHECK);
					break;
				case 'c':
					move = new Move(Move.CALL);
					break;
				case 'r':
					move = new Move(Move.RAISE);
					move.setBet(getBet());
					break;
				default:
					System.out.println("Invalid move: try again!");
					break;
			}
		}
		return move;
	}

	
	/**
	 * Asks the player to return the amount of chips it wants to getBet if raise move.
	 * 
	 * @return The amount of chips getBet.
	 */
	private int getBet() {

		int bet;
		System.out.println("Bet size?");

		try {
			bet = scanner.nextInt();
		}
		catch (Exception e) {
			System.out.println("Invalid getBet input: getBet changed to 0!");
			bet = 0;
		}
		return bet;
	}
	
}
