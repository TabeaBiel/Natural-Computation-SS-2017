package gpoker;

import java.util.List;

/**
 * The Evaluator analyzes poker hands and determines the hand value.
 * Also, the evaluator determines the better of two hands having the same value. This is the basic evaluator common to
 * all poker forms. The derived classes should be implemented for a specific poker form making specific assumptions
 * on the number of cards to be evaluated.
 *
 * @author Helmut A. Mayer
 * @since March 10, 2016
 *
 */
public abstract class Evaluator {

	/** A hand to be evaluated. */
	protected Hand hand1;

	/** Another hand to be evaluated. */
	protected Hand hand2;

	/** The suited cards counter. */
	protected int[] suits;


	protected Evaluator() {

		hand1 = new Hand();
		hand2 = new Hand();
		suits = new int[4];
	}


	/**
	 * Evaluates the given hand and sets its value.
	 *
	 * @param hand		the hand to be evaluated
	 * @param cards		additional cards to be used by the hand
	 */
	public abstract void evaluate(Hand hand, List<Card> cards);


	/** Evaluates the given cards.
	 *
	 * @param cards		some cards, may not be null
	 *
	 * @return          the value of the cards
	 *
	 */
	public abstract int evaluate(List<Card> cards);


	/**
	 * Determines the better of two equal hands, i.e. resolves a tie between hands.
	 * E.g. with a straight the higher top rank of a straight determines the winner.
	 *
	 *
	 * @param hand1        first Hand
	 * @param hand2        second hand
	 * @param cards			additional cards to be used by the hands
	 *
	 * @return 				1 if hand1 is better, 2 if hand2 is, 0 if equal
	 */
	public abstract int resolve(Hand hand1, Hand hand2, List<Card> cards);


	/**
	 * Returns the pivot card of an evaluated hand. The pivot card is the card determining the quality of a specific
	 * hand value. E.g., for a pair it is a card of the pair, for a flush the highest card of the flush.
	 *
	 * @param hand		a hand
	 * @param cards		additional cards, may be null
	 *
	 * @return 			the pivot card
	 */
	public abstract Card getPivot(Hand hand, List<Card> cards);

}
