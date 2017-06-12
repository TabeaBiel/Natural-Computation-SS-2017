package gpoker;

import evSOLve.JEvolution.misc.Utilities;

import java.util.List;

/**
 * Representation of deck(s) of "french" cards.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class Deck {

	/** The number of cards in the deck. */
	private static final int NUMBER_OF_CARDS = Card.NUMBER_OF_SUITS * Card.NUMBER_OF_RANKS;

	/** All the cards. */
	private Card[] cards;

	/** The index of the card to be dealt next. */
	private int cardIndex;

	/** Indicates duplicate hand system. */
	private boolean duplicate;

	/** Counts the number of duplicate hands. */
	private int dupCount;


	/** Constructs a shuffled deck. */
	public Deck() {

		cards = new Card[NUMBER_OF_CARDS];
		int index = 0;
		for (int i = 0; i < Card.NUMBER_OF_SUITS; i++)
			for (int j = 0; j < Card.NUMBER_OF_RANKS; j++)
				cards[index++] = new Card(j, i);
		shuffle();
		dupCount = -1;											// a bit tricky, but correct
	}


	/** Sets the duplicate hand system.
	 *
	 * @param duplicate	true, if duplicate hands are to be dealt
	 */
	public void setDuplicate(boolean duplicate) {

		this.duplicate = duplicate;
	}


	/** Performs a deck shuffle implementing the Fisher-Yates algorithm. It also implements the duplicate hand system
	 * by not shuffling the deck until every active player has played the same hand. Note that there are some subtle
	 * problems, if during a duplication period an active player is eliminated (from a tournament), as duplication might
	 * not be exact, i.e., one or more players do not receive the same hand. */
	public void shuffle() {

		cardIndex = 0;

		if (duplicate && ++dupCount < Director.getInstance().getDealer().getActivePlayers().size())
			return;

		for (int i = NUMBER_OF_CARDS - 1; i > 0; i--) {
			int pos = Utilities.nextIntegerInRange(0, i);		// pos in [0, i]
			Card temp = cards[pos];
			cards[pos] = cards[i];
			cards[i] = temp;
		}
		dupCount = 0;
	}


	/**
	 * Draws a number of cards from the deck.
	 *
	 * @param count		the number of cards
	 * @param cards		a card list to put cards into
	 *
	 */
	public void draw(int count, List<Card> cards) {

		for (int i = 0; i < count; i++)
			cards.add(this.cards[cardIndex++]);
	}


	/** Returns a string representation of the deck.
	 *
	 * @return all the cards from first to last
	 */
	public String toString() {

		String cards = "";

		for (Card card : this.cards)
			cards += (String.valueOf(card) + " ");
		return cards;
	}

}