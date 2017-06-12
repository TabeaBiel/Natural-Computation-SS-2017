package gpoker;

/**
 * Representation of all "french" poker cards.
 *
 * @author Edgar Ebensperger
 */
public class Card implements Comparable<Card> {

	/** Number of ranks per deck. */
	public static final int NUMBER_OF_RANKS = 13;

	/** Number of suits per deck. */
	public static final int NUMBER_OF_SUITS = 4;

	/** The card ranks. The strange numbers are for convenience. */
	public static final int ACE = 12;

	public static final int KING = 11;

	public static final int QUEEN = 10;

	public static final int JACK = 9;

	public static final int TEN = 8;

	public static final int NINE = 7;

	public static final int EIGHT = 6;

	public static final int SEVEN = 5;

	public static final int SIX = 4;

	public static final int FIVE = 3;

	public static final int FOUR = 2;

	public static final int THREE = 1;

	public static final int DEUCE = 0;

	/** A blank card. */
	public static final int BLANK = -1;

	// All Suits
	public static final int HEARTS = 3;

	public static final int SPADES = 2;

	public static final int DIAMONDS = 1;

	public static final int CLUBS = 0;

	/** String representation of all ranks. */
	public static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};

	/** String representation of all suits. */
	public static final char[] SUITS = {'c', 'd', 's', 'h'};

	/** Rank of the current card. */
	private final int rank;

	/** Suit of the current card. */
	private final int suit;


	/**
	 * Constructor with a give rank and suit.
	 *
	 * @param rank The given rank
	 * @param suit The given suit
	 */
	public Card(int rank, int suit) {

		this.rank = rank;
		this.suit = suit;
	}


	/**
	 * Returns the suit.
	 *
	 * @return suit.
	 */
	public int getSuit() {

		return suit;
	}


	/**
	 * Returns the rank.
	 *
	 * @return rank.
	 */
	public int getRank() {

		return rank;
	}


	/**
	 * Compares this card rank with the other card rank.  Returns a
	 * negative integer, zero, or a positive integer as this rank is less
	 * than, equal to, or greater than the other card's rank.
	 *
	 * @param other		the other card
	 *
	 * @return 			an integer indicating the rank relation
	 *
	 */
	@SuppressWarnings("NullableProblems")
	public int compareTo(Card other) {

		return rank - other.getRank();
	}


	/**
	 * Returns the string representation of the node.
	 */
	public String toString() {

		return RANKS[rank] + SUITS[suit];
	}

}
