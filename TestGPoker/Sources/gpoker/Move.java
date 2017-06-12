package gpoker;

/**
 * A poker move. Note that there is no BET, which is included in RAISE.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class Move implements Cloneable {

	/** All the moves. */
	public static final int FOLD = 0;

	public static final int CHECK = 1;

	public static final int CALL = 2;

	public static final int RAISE = 3;

	/** The type of move. */
	private int type;

	/** The getBet. */
	private int bet;


	/** A deep clone.
	 *
	 * @return	the clone
	 */
	public final Move clone() {

		Move clone = null;

		try {
			clone = (Move)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();					// should not happen
		}
		return clone;
	}


	/**
	 * Constructs a move.
	 *
	 * @param type the move
	 */
	public Move(int type) {

		this.type = type;
	}


	/** Returns the move.
	 *
	 * @return	the move
	 */
	public int getType() {

		return type;
	}


	/** Sets the move.
	 *
	 * @param type	a move
	 */
	public void setType(int type) {

		this.type = type;
	}


	/** Returns the bet.
	 *
	 * @return	the chips to bet
	 */
	public int getBet() {

		return bet;
	}


	/** Sets the bet. Evidently, {@code getBet()} is only used in case of RAISE.
	 *
	 * @param bet	the chips to bet
	 */
	public void setBet(int bet) {

		this.bet = bet;
	}


	/**
	 * The string representation of the move.
	 *
	 * @return	a string describing the move
	 */
	public String toString() {

		switch (type) {
			case FOLD:
				return "Fold";
			case CHECK:
				return "Check";
			case CALL:
				return "Call";
			case RAISE:
				return "Raise";
			default:
				return "Invalid";
		}
	}

}