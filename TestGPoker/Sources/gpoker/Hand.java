package gpoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The cards of a player also called hand.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 *
 */
public class Hand {

	/** Indicates an unevaluated hand. Its value is lower than any other hand value. */
	public final static int NOT_EVALUATED = 0;

	/** All the hand values. */
	public final static int HIGH_CARD = 1;

	public final static int ONE_PAIR = 2;

	public final static int TWO_PAIR = 3;

	public final static int THREE_OF_A_KIND = 4;

	public final static int STRAIGHT = 5;

	public final static int FLUSH = 6;

	public final static int FULL_HOUSE = 7;

	public final static int FOUR_OF_A_KIND = 8;

	public final static int STRAIGHT_FLUSH = 9;

	/** The cards in one hand */
	public List<Card> cards = new ArrayList<>(7);

	/** The value of the hand. */
	private int value;

	/** True if cards array is sorted false otherwise */
	private boolean sorted;


	/**
	 * Returns the value of the hand.
	 *
	 * @return value.
	 */
	public int getValue() {

		return value;
	}


	/**
	 * Sets the value of the hand.
	 *
	 * @param value value to set.
	 */
	public void setValue(int value) {

		this.value = value;
	}


	/**
	 * Returns the size of the hand.
	 *
	 * @return Number of cards.
	 */
	public int size() {

		return cards.size();
	}


	/* Resets the hand. */
	public void reset() {

		cards.clear();
		value = NOT_EVALUATED;
		sorted = false;
	}


	/**
	 * Returns if the hand is sorted.
	 *
	 * @return sorted.
	 */
	public boolean isSorted() {

		return sorted;
	}


	/**
	 * Returns all cards (card[]) of the hand.
	 *
	 * @return The cards.
	 */
	public List<Card> getCards() {

		return cards;
	}


	/**
	 * Adds a card to the cards array.
	 *
	 * @param card The card to be added.
	 */
	public void addCard(Card card) {

		cards.add(card);
		sorted = false;
	}


	/**
	 * Sorts the cards array of the hand (high to low).
	 */
	public void sort() {

		Collections.sort(cards, Collections.reverseOrder());				// descending sort
		sorted = true;
	}


	/**
	 * Adds a list of cards to the hand.
	 *
	 * @param cards the cards to add.
	 */
	public void addCards(List<Card> cards) {

		if (cards.size() > 0) {
			this.cards.addAll(cards);
			sorted = false;
		}
	}


	public String toString() {

		String hand = "";
		for (int i = 0, n = cards.size(); i < n; i++) {
			hand += cards.get(i);
			if (i < n - 1)
				hand += " ";
		}
		return hand;
	}

}
