package gpoker;

import java.util.Arrays;
import java.util.List;

/**
 * The Texas Hold'em evaluator.
 *
 * @author Helmut A. Mayer
 * @since March 2016
 *
 */
public class TexasEvaluator extends Evaluator {


	/** Constructs the evaluator. */
	public TexasEvaluator() {

		super();
	}


	/**
	 * Evaluates the given player hand using the additional cards.
	 *
	 * @param playerHand	the hand of a player
	 * @param cards			additional cards, may be null
	 */
	public void evaluate(Hand playerHand, List<Card> cards) {

		hand1.reset();
		hand1.addCards(playerHand.getCards());
		if (cards != null)
			hand1.addCards(cards);
		evaluate(hand1);
		playerHand.setValue(hand1.getValue());
	}


	/** Evaluates the given cards.
	 *
	 * @param cards		some cards, may not be null
	 *
	 * @return          the value of the cards
	 *
	 */
	public int evaluate(List<Card> cards) {

		hand1.reset();
		hand1.addCards(cards);
		evaluate(hand1);
		return hand1.getValue();
	}


	/** Evaluates a hand.
	 *
	 * @param hand	the hand
	 */
	private void evaluate(Hand hand) {

		hand.sort();

		boolean flush = getFlushSuit(hand.getCards()) != -1;
		boolean straight = checkStraight(hand);

		if (straight) {
			if (flush) {
				if (getStraightFlushPivot(hand.getCards()) != null)
					hand.setValue(Hand.STRAIGHT_FLUSH);
				else
					hand.setValue(Hand.FLUSH);
			} else
				hand.setValue(Hand.STRAIGHT);                            // a straight excludes any higher multiples
		} else {
			checkMultiples(hand);                                    	// sets hand value
			if (flush) {
				if (hand.getValue() < Hand.FLUSH)
					hand.setValue(Hand.FLUSH);
			}
			// all other hand values are already set correctly
		}
	}


	/**
	 * Checks for multiple cards of same rank and sets the according hand value.
	 *
	 * @param hand the hand
	 */
	private void checkMultiples(Hand hand) {

		List<Card> cards = hand.getCards();
		int n = cards.size();
		int multiples = 1;
		int pairs = 0;
		int trips = 0;

		for (int i = 0; i < n - 1; i++) {
			boolean same = cards.get(i).getRank() == cards.get(i + 1).getRank();
			if (same)
				++multiples;
			if (!same || i == n - 2) {                			// a bit strange, but needed
				switch (multiples) {
					case 1:                                 	// no multiples
						break;
					case 2:
						++pairs;
						multiples = 1;                     		// reset
						break;
					case 3:
						++trips;
						multiples = 1;                         	// reset
						break;
					case 4:
						hand.setValue(Hand.FOUR_OF_A_KIND);
						return;                              	// can't get any better
				}
			}
		}
		if (trips > 0) {
			if (pairs > 0)
				hand.setValue(Hand.FULL_HOUSE);
			else
				hand.setValue(Hand.THREE_OF_A_KIND);
		} else if (pairs == 0)
			hand.setValue(Hand.HIGH_CARD);
		else if (pairs == 1)
			hand.setValue(Hand.ONE_PAIR);
		else if (pairs > 1)
			hand.setValue(Hand.TWO_PAIR);
	}


	/**
	 * Returns the suit of a potential flush.
	 *
	 * @param cards    the hand
	 *
	 * @return		suit, if flush, -1 if none
	 */
	private int getFlushSuit(List<Card> cards) {

		if (cards.size() < 5)
			return -1;								// too few cards

		Arrays.fill(suits, 0);                    	// resetRound array

		for (Card card : cards)
			++suits[card.getSuit()];
		for (int i = 0; i < 4; i++) {
			if (suits[i] >= 5)
				return i;             				// suit of flush
		}
		return -1;
	}


	/**
	 * Checks hand for a straight. This method works in principal for an arbitrary number of cards,
	 * but returns when the first straight is found.
	 *
	 * @param hand the hand
	 *
	 * @return true, if straight
	 */
	private boolean checkStraight(Hand hand) {

		if (hand.size() < 5)
			return false;							// too few cards

		List<Card> cards = hand.getCards();
		int n = cards.size();

		int count = 1;
		if (cards.get(0).getRank() == Card.ACE && cards.get(n - 1).getRank() == Card.DEUCE)
			count = 2;                                              // ace may be used as 1

		for (int i = n - 1; i > 0; i--) {                			// from low to high cards
			int rank0 = cards.get(i).getRank();
			int rank1 = cards.get(i - 1).getRank();
			if (rank0 == rank1)
				continue;                                            // skip over multiples
			if (rank0 + 1 == rank1) {
				if (++count == 5)                                // one more card in straight
					return true;                                    // we have a straight
			} else
				count = 1;								// reset, no straight here
		}
		return false;
	}


	/**
	 * Determines the better of two equal hands. E.g. with a straight the higher top rank of a straight determines
	 * the winner. Important: the two hands must have correct hand values!
	 *
	 * @param h1        first Hand
	 * @param h2        second hand
	 * @param cards		additional cards, may be null
	 * @return 			1 if hand1 is better, 2 if hand2 , 0 if equal
	 */
	public int resolve(Hand h1, Hand h2, List<Card> cards) {

		hand1.reset();
		hand1.addCards(h1.getCards());
		if (cards != null)
			hand1.addCards(cards);
		hand1.sort();
		hand2.reset();
		hand2.addCards(h2.getCards());
		if (cards != null)
			hand2.addCards(cards);
		hand2.sort();

		List<Card> cards1 = hand1.getCards();
		List<Card> cards2 = hand2.getCards();

		switch (h1.getValue()) {
			case Hand.HIGH_CARD:
				return compareHighCards(cards1, cards2);
			case Hand.ONE_PAIR:
				return comparePairs(cards1, cards2);
			case Hand.TWO_PAIR:
				return compareTwoPairs(cards1, cards2);
			case Hand.THREE_OF_A_KIND:
				return compareTrips(cards1, cards2);
			case Hand.STRAIGHT:
				return compareStraights(cards1, cards2);
			case Hand.FLUSH:
				return compareFlushes(cards1, cards2);
			case Hand.FULL_HOUSE:
				return compareFullHouses(cards1, cards2);
			case Hand.FOUR_OF_A_KIND:
				return compareQuads(cards1, cards2);
			case Hand.STRAIGHT_FLUSH:
				return compareStraightFlushes(cards1, cards2);
			default:
				return 0;
		}
	}


	/**
	 * Determines the better of two high card hands.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return 1 if hand1, 2 if hand2 has the higher kicker, 0 if equal
	 */
	private int compareHighCards(List<Card> cards1, List<Card> cards2) {

		for (int i = 0; i < 5; i++) {
			Card kicker1 = cards1.get(i);
			Card kicker2 = cards2.get(i);
			int comp = kicker1.compareTo(kicker2);

			if (comp > 0)
				return 1;
			if (comp < 0)
				return 2;
		}
		return 0;
	}


	/**
	 * Determines the better of two one pair hands. If the pairs have the same rank, the three kickers are
	 * checked. We assume that the hand has at least five cards.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return 1 if hand1, 2 if hand2 has the higher pair or kicker, 0 if equal
	 */
	private int comparePairs(List<Card> cards1, List<Card> cards2) {

		int index1 = getPair(cards1, 0);
		int index2 = getPair(cards2, 0);
		int comp = cards1.get(index1).getRank() - cards2.get(index2).getRank();
		if (comp > 0)
			return 1;
		if (comp < 0)
			return 2;

		index1 = index2 = -1;                                                        // equal pairs
		for (int i = 0; i < 3; i++) {
			index1 = getKicker(cards1, index1 + 1);
			index2 = getKicker(cards2, index2 + 1);
			comp = cards1.get(index1).getRank() - cards2.get(index2).getRank();
			if (comp > 0)
				return 1;
			if (comp < 0)
				return 2;
		}
		return 0;                                                                   // equal kickers
	}


	/**
	 * Determines the better of two two pair hands. If the pairs have the same rank, the remaining kicker is checked.
	 * Thus we assume that the hand has at least five cards.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return 1 if hand1, 2 if hand2 has the higher pair or kicker, 0 if equal
	 */
	private int compareTwoPairs(List<Card> cards1, List<Card> cards2) {

		int index1 = -1;
		int index2 = -1;

		for (int i = 0; i < 2; i++) {
			index1 = getPair(cards1, index1 + 1);
			index2 = getPair(cards2, index2 + 1);
			int comp = cards1.get(index1).getRank() - cards2.get(index2).getRank();
			if (comp > 0)
				return 1;
			if (comp < 0)
				return 2;
		}
		index1 = getPair(cards1, 0);                                                // equal pairs
		index2 = getPair(cards1, index1 + 1);                                        // for the ranks
		int rank1 = cards1.get(index1).getRank();                                   // higher pair
		int rank2 = cards1.get(index2).getRank();                                   // lower pair
		index1 = -1;
		do {
			index1 = getKicker(cards1, ++index1, rank1);
		} while (cards1.get(index1).getRank() == rank2);                            // kicker1 not from pairs
		index2 = -1;
		do {
			index2 = getKicker(cards2, ++index2, rank1);
		} while (cards2.get(index2).getRank() == rank2);                            // kicker2 not from pairs

		int comp = cards1.get(index1).getRank() - cards2.get(index2).getRank();
		if (comp > 0)
			return 1;
		if (comp < 0)
			return 2;
		return 0;                                                                   // equal kickers
	}


	/**
	 * Determines the better of two trips hands. We assume that the hands have at least five cards.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return hand1 wins is 1, hand2 wins is 2, equal is 0
	 */
	private int compareTrips(List<Card> cards1, List<Card> cards2) {

		int index1 = getPair(cards1, 0);                                            // the pair must be trips
		int index2 = getPair(cards2, 0);
		int comp = cards1.get(index1).getRank() - cards2.get(index2).getRank();
		if (comp > 0)
			return 1;
		if (comp < 0)
			return 2;

		int rank = cards1.get(index1).getRank();                                    // equal trips
		index1 = index2 = -1;
		for (int i = 0; i < 2; i++) {                                               // max two kickers
			index1 = getKicker(cards1, ++index1, rank);
			index2 = getKicker(cards2, ++index2, rank);
			comp = cards1.get(index1).getRank() - cards2.get(index2).getRank();
			if (comp > 0)
				return 1;
			if (comp < 0)
				return 2;
		}
		return 0;                                                                   // equal kickers
	}


	/**
	 * The straight tie breaker. It simply compares the top ranks of the two straights.
	 *
	 * @param cards1 first straight hand
	 * @param cards2 second straight hand
	 *
	 * @return 1 if first hand wins, 2 if second hand wins, 0 if still tied
	 */
	private int compareStraights(List<Card> cards1, List<Card> cards2) {

		int rank1 = getStraightPivot(cards1).getRank();
		int rank2 = getStraightPivot(cards2).getRank();
		if (rank1 > rank2)
			return 1;
		if (rank2 > rank1)
			return 2;
		return 0;
	}


	/**
	 * Determines the better of two flush hands. We assume that there are at least five cards and less than
	 * ten cards in the hand. With ten cards two different flush suits would be possible.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return 1 if first hand wins, 2 if second, 0 if equal
	 */
	private int compareFlushes(List<Card> cards1, List<Card> cards2) {

		int suit = Card.HEARTS;
		Arrays.fill(suits, 0);                                        // resetRound array

		for (Card card : cards1)
			++suits[card.getSuit()];
		for (int i = 0; i < suits.length; i++) {                    // only one flush suit possible
			if (suits[i] >= 5) {
				suit = i;
				break;
			}
		}
		int index1 = -1;
		int index2 = -1;
		for (int i = 0; i < 5; i++) {                               // max five suited kickers
			index1 = getKicker(suit, cards1, ++index1);
			index2 = getKicker(suit, cards2, ++index2);
			int comp = cards1.get(index1).getRank() - cards2.get(index2).getRank();
			if (comp > 0)
				return 1;
			if (comp < 0)
				return 2;
		}
		return 0;                                                    // equal flushes
	}


	/**
	 * Determines the better of two full house hands. We assume that there is only one set (trips) in the hand.
	 * If so, the number of cards is arbitrary.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return 1 if first hand wins, 2 if second, 0 if equal
	 */
	private int compareFullHouses(List<Card> cards1, List<Card> cards2) {

		int index = getMultiple(cards1, 3, 0);
		int tripsRank1 = cards1.get(index).getRank();
		index = getMultiple(cards2, 3, 0);
		int tripsRank2 = cards2.get(index).getRank();

		if (tripsRank1 > tripsRank2)                                        // higher trips?
			return 1;
		if (tripsRank1 < tripsRank2)
			return 2;

		int pairRank1 = Card.DEUCE;
		index = -1;

		while ((index = getPair(cards1, ++index)) != -1) {                    // check first hand
			pairRank1 = cards1.get(index).getRank();
			if (pairRank1 != tripsRank1)
				break;
		}

		int pairRank2 = Card.DEUCE;
		index = -1;

		while ((index = getPair(cards2, ++index)) != -1) {                    // check second hand
			pairRank2 = cards2.get(index).getRank();
			if (pairRank2 != tripsRank2)
				break;
		}

		if (pairRank1 > pairRank2)                                            // higher pair?
			return 1;
		if (pairRank1 < pairRank2)
			return 2;

		return 0;                                                            // equal houses
	}


	/**
	 * Determines the better of two quads hands. The number of cards is assumed to be five at least.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return 1 if first hand wins, 2 if second, 0 if equal
	 */
	private int compareQuads(List<Card> cards1, List<Card> cards2) {

		int quadsRank1 = Card.BLANK;
		int rank = Card.BLANK;
		int index = -1;

		while (quadsRank1 == Card.BLANK) {
			index = getPair(cards1, ++index);
			if (cards1.get(index).getRank() == rank)                    // same rank as last pair?
				quadsRank1 = rank;
			else
				rank = cards1.get(index).getRank();
		}
		int quadsRank2 = Card.BLANK;
		rank = Card.BLANK;
		index = -1;

		while (quadsRank2 == Card.BLANK) {
			index = getPair(cards2, ++index);
			if (cards2.get(index).getRank() == rank)                    // same rank as last pair?
				quadsRank2 = rank;
			else
				rank = cards2.get(index).getRank();
		}
		if (quadsRank1 > quadsRank2)
			return 1;
		if (quadsRank1 < quadsRank2)
			return 2;

		index = getKicker(cards1, 0, quadsRank1);                        // equal quads
		int kicker1 = cards1.get(index).getRank();                        // highest non-quad card
		index = getKicker(cards2, 0, quadsRank2);
		int kicker2 = cards2.get(index).getRank();                        // highest non-quad card

		if (kicker1 > kicker2)
			return 1;
		if (kicker1 < kicker2)
			return 2;

		return 0;                                                       // equal kickers
	}


	/**
	 * Determines the better of two straight flush hands.
	 *
	 * @param cards1 first hand
	 * @param cards2 second hand
	 *
	 * @return 1 if first hand wins, 2 if second, 0 if equal
	 */
	private int compareStraightFlushes(List<Card> cards1, List<Card> cards2) {

		int rank1 = getStraightFlushPivot(cards1).getRank();
		int rank2 = getStraightFlushPivot(cards2).getRank();

		if (rank1 > rank2)
			return 1;
		if (rank1 < rank2)
			return 2;

		return 0;
	}


	/**
	 * Returns the higher index of the first pair found in cards. Note that we do not check, if the pair
	 * is part of a higher hand, e.g., trips. This method works for an arbitrary number of cards.
	 *
	 * @param cards the hand
	 * @param start the start index for the search
	 *
	 * @return the higher index of the first pair found
	 */
	private int getPair(List<Card> cards, int start) {

		int last = cards.size() - 1;
		if (start > last)
			return -1;                                        // no more cards

		for (int i = start; i < last; i++) {
			Card card1 = cards.get(i);
			Card card2 = cards.get(i + 1);
			if (card1.getRank() == card2.getRank())
				return i + 1;                                // pair found
		}
		return -1;                                            // no pair found
	}


	/**
	 * Returns the higher index of the first multiple found in cards. Note that we do not check, if the multiple
	 * is part of a higher multiple hand. This method works for an arbitrary number of cards.
	 *
	 * @param cards				the hand
	 * @param multiple			the number of cards of equal rank to be searched for
	 * @param start 			the start index for the search
	 *
	 *  @return 				the highest index of the multiple found
	 */
	private int getMultiple(List<Card> cards, int multiple, int start) {

		int last = cards.size() - 1;
		if (start > last)
			return -1;                                        // no more cards

		int count = 1;

		for (int i = start; i < last; i++) {
			Card card1 = cards.get(i);
			Card card2 = cards.get(i + 1);
			if (card1.getRank() == card2.getRank())
				++count;	                                // pair found
			else
				count = 1;
			if (count == multiple)
				return i + 1;
		}
		return -1;                                            // no multiple found
	}


	/**
	 * Returns the index of the highest card not being a multiple starting the search at the start index.
	 * If cards are multiple they are marked
	 * as such. The next different card after multiples may be a kicker, but only is really one, if the card after the
	 * candidate is different from the rank of the candidate. Otherwise, the candidate itself is a multiple. E.g.,
	 * 4 pairs in eight cards do not have a kicker. so -1 would be returned.
	 * This method works for an arbitrary number of sorted cards.
	 *
	 * @param cards the hand
	 * @param start the start index for the search
	 *
	 * @return the index of the kicker, -1 if none found
	 */
	private int getKicker(List<Card> cards, int start) {

		int last = cards.size() - 1;
		if (start > last)
			return -1;                                        // no more kicker
		boolean multiples = false;

		for (int i = start; i < last; i++) {
			Card card1 = cards.get(i);
			Card card2 = cards.get(i + 1);
			if (card1.getRank() == card2.getRank())
				multiples = true;                            // cannot be a kicker
			else {
				if (multiples)                              // different card after multiples, may also be multiple
					multiples = false;                      // signal different card
				else
					return i;                                // again different after different, so this is it
			}
		}
		if (!multiples)
			return last;                                     // last was not multiple
		return -1;                                            // no more kicker
	}


	/**
	 * Returns the index of the highest card not having the excluded rank.
	 * If no more kicker can be found, -1 is returned. This method works for an arbitrary number of sorted cards.
	 *
	 * @param cards   the hand
	 * @param start   the start index for the search
	 * @param exclude the rank to be excluded from search
	 *
	 * @return the index of the kicker, -1 if none found
	 */
	private int getKicker(List<Card> cards, int start, int exclude) {

		int last = cards.size();
		if (start > last)
			return -1;                                        // no more kicker

		for (int i = start; i < last; i++) {
			Card card = cards.get(i);
			if (card.getRank() != exclude)
				return i;                                    // kicker
		}
		return -1;                                            // no more kicker
	}


	/**
	 * Returns the index of the highest card of given suit.
	 * If no more kicker can be found, -1 is returned. This method works for an arbitrary number of sorted cards.
	 *
	 * @param suit  the suit to be searched for
	 * @param cards the hand
	 * @param start the start index for the search
	 *
	 * @return the index of the kicker, -1 if none found
	 */
	private int getKicker(int suit, List<Card> cards, int start) {

		int last = cards.size();
		if (start > last)
			return -1;                                        // no more kicker

		for (int i = start; i < last; i++) {
			Card card = cards.get(i);
			if (card.getSuit() == suit)
				return i;                                    // suited kicker
		}
		return -1;                                            // no more kicker
	}


	/**
	 * Gets the top card of a straight hand assuming only one straight in the hand. As with eleven cards there could
	 * be two straights, this method works until ten cards, but always assuming that there is a straight in the hand.
	 *
	 *
	 * @param cards 	a straight hand
	 *
	 * @return			the top card of the straight
	 */
	private Card getStraightPivot(List<Card> cards) {

		Card pivot = cards.get(0);									// assume it
		int rank = pivot.getRank();
		int count = 1;

		for (Card card : cards) {				                	// from high to low cards
			if (card.getRank() == rank)
				continue;                                          	// skip over multiples
			if (card.getRank() == rank - 1) {
				if (++count == 5)                                   // one more card in straight
					return pivot;
			} else {
				pivot = card;										// reset, no straight here
				count = 1;
			}
			rank = card.getRank();
		}
		return pivot;				                                // must be a ...2-A straight
	}


	/**
	 * Returns the pivot of a potential straight flush hand. This method works in principal for an arbitrary number of cards,
	 * but returns when the first straight flush is found. The pivot is the highest card of the straight flush.
	 *
	 *
	 * @param cards		the hand
	 *
	 * @return 			pivot, if straight flush, otherwise null
	 */
	private Card getStraightFlushPivot(List<Card> cards) {

		int suit = getFlushSuit(cards);
		Card pivot = null;
		int rank = Card.BLANK;
		int count = 0;

		for (Card card : cards) {
			if (card.getSuit() != suit)
				continue;                 								// also jumps over multiples
			if (card.getRank() == rank - 1) {
				if (++count == 5)
					return pivot;
			}
			else {
				count = 1;
				pivot = card;
			}
			rank = card.getRank();
		}
		if (count == 4 && rank == Card.DEUCE) {
			if (cards.get(0).getRank() == Card.ACE && cards.get(0).getSuit() == suit)
				return pivot;                                                          	// ...2-A straight flush
		}
		return null;
	}



	/**
	 * Returns the pivot card of an evaluated hand. The pivot card is the card determining the quality of a specific
	 * hand value. E.g., for a pair it is a card of the pair, for a flush the highest card of the flush.
	 *
	 * @param hand		a hand
	 * @param cards		additional cards, may be null
	 *
	 * @return 			the pivot card
	 */
	public Card getPivot(Hand hand, List<Card> cards) {

		hand1.reset();
		hand1.addCards(hand.getCards());
		if (cards != null)
			hand1.addCards(cards);
		hand1.sort();

		List<Card> cards1 = hand1.getCards();

		switch (hand.getValue()) {
			case Hand.HIGH_CARD:
				return cards1.get(0);
			case Hand.ONE_PAIR:
				return cards1.get(getPair(cards1, 0));
			case Hand.TWO_PAIR:
				return cards1.get(getPair(cards1, 0));
			case Hand.THREE_OF_A_KIND:
				return cards1.get(getPair(cards1, 0));
			case Hand.STRAIGHT:
				return getStraightPivot(cards1);
			case Hand.FLUSH:
				return cards1.get(getKicker(getFlushSuit(cards1), cards1, 0));
			case Hand.FULL_HOUSE:
				return cards1.get(getMultiple(cards1, 3, 0));
			case Hand.FOUR_OF_A_KIND:
				return cards1.get(getMultiple(cards1, 4, 0));
			case Hand.STRAIGHT_FLUSH:
				return getStraightFlushPivot(cards1);
			default:
				return new Card(Card.BLANK, Card.BLANK);							// should not happen
		}
	}

}
