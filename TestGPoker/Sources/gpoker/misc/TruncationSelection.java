package gpoker.misc;

import evSOLve.JEvolution.Individual;
import evSOLve.JEvolution.SubPopulation;
import evSOLve.JEvolution.selection.Selection;

/** Truncation Selection is selecting the best individuals from the given percentage range. These best individuals are
 * selected in order starting with the best until the offspring is equal to the number of parents.
 *
 * @author Helmut A. Mayer
 * @version 0.9.8
 * @since August 22, 2001
 *
 * $Id: TruncationSelection.java 665 2017-04-26 10:56:22Z helmut $
 */
public class TruncationSelection extends Selection {

	/** The selection percentage. */
	private double percentage;


	public TruncationSelection(double percentage) {

		this.percentage = percentage;
	}


    /** Selects all individuals with a fitness within the best percentage of individuals. The best individuals are selected
	 * in order until the offspring is equal to the number of parents.
     *
     * @param parents				the SubPopulation under pressure
     * @param offspring				the SubPopulation of survivors
     *
     */
    public void doSelect(SubPopulation parents, SubPopulation offspring) {

		parents.sort();
		int best = (int)(parents.getSize() * percentage);
		int i = 0;

		while (offspring.getSize() < parents.getSize()) {
			Individual ind = parents.getIndividual(i);
			offspring.addIndividual(ind);
			if (++i == best)
				i = 0;											// cycle through best percentage
        }
	}

}
