package gpoker.gp;

import evSOLve.JEvolution.JEvolution;
import evSOLve.JEvolution.JEvolutionException;
import evSOLve.JEvolution.JEvolutionReporter;
import evSOLve.JEvolution.gp.TreeChromosome;
import evSOLve.JEvolution.gp.nodes.*;
import gpoker.gp.fitness.FitnessEvaluator;
import gpoker.misc.TruncationSelection;
import gpoker.misc.Xml;
import gpoker.players.GPlayer;
import org.jdom2.Element;

/**
 * Starts the evolution with all given parameters.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class GPoker {

	/** Starts the evolution of GPlayer.
	 *
	 * @param player		the player model
	 * @param evolution     the XML tag for parameters
	 *
	 */
	public static void evolve(GPlayer player, Element evolution) {

		// instantiate all evolution related objects
		JEvolution GP = JEvolution.getInstance();											// + call it a GP
// 		GP.setMaximization(false);															// o minimization problem
		JEvolutionReporter GPReporter = (JEvolutionReporter)GP.getReporter();				// - get the reporter
		TreeChromosome chrom = new TreeChromosome(4, IfMove.TYPE_MOVE);					    // + create a tree chromosome with depth 4 and result type
		chrom.setNodeMutation(false);

		/* Add function and terminal nodes. */
		chrom.addNode(new IfMove());						// function
		chrom.addNode(new LessEqualBoolean()); 				// function
		chrom.addNode(new MultDouble());                    // function
		chrom.addNode(new DivDouble());						// function
		chrom.addNode(new AddDouble());						// function
//		chrom.addNode(new AndBoolean());                    // function
//		chrom.addNode(new OrBoolean());                    	// function
//		chrom.addNode(new NotBoolean());                    // function
		chrom.addNode(new RaiseMove());						// function, computes bet

//		chrom.addNode(new ConstMove());						// constant, terminal
		chrom.addNode(new CallCheckMove());					// constant, terminal
		chrom.addNode(new ConstDouble(0.0, 1.0));		 	// constant in unit interval, terminal
		chrom.addNode(new VarDouble());						// variable, terminal
//		chrom.addNode(new ConstBoolean()); 					// terminal

		// set GP parameters
		try {
			chrom.setSoupType(TreeChromosome.RAMPED);
			chrom.setMutationRate(Xml.getProperty(evolution, "mutationRate", 0.05));
			chrom.setCrossoverRate(Xml.getProperty(evolution, "crossoverRate", 1.0));
// 			Utilities.setRandomSeed(42);
//			GP.setSubPopulations(20);
			GP.setPopulationSize(Xml.getProperty(evolution, "populationSize", 100));		// keep it here!
			//GP.setSelection(new TournamentSelection(2));
			//GP.setSelection(new TruncationSelection(0.3));

			GP.addChromosome(chrom);							// + tell GP about your chromosome
			player.setFitnessEvaluator(FitnessEvaluator.create(Xml.getChildOf(evolution, "FitnessEvaluator")));
			GP.setPhenotype(player);							// + tell GP about your Phenotype class
//			GP.setSelection(new TruncationSelection(0.0));
//			GP.setPopulationSize(2000, 5000);
//			SubPopulation seed = GP.getSeedPopulation();
//			seed.fill(100000);

			GP.setMaximalGenerations(Xml.getProperty(evolution, "numberOfGenerations", 50));
			// o , maybe use 5,000 generations for a good solution

			GPReporter.setReportLevel(JEvolutionReporter.BRIEF);

		} catch (JEvolutionException e) {
			System.out.println(e.toString());
			System.out.println("Continuing with default values.");
		}

//		while (GP.doEvolve(1) != 0)												// single step generation
//			;

		// start evolution
		GP.doEvolve();															// + evolution run


		GPlayer best = (GPlayer)GPReporter.getBestIndividual().getPhenotype();
		Xml.save(best.toXml(), "best.xml");

		Xml.save(player.getFitnessEvaluator().toXml(), "culture.xml");

		//Individual bestFromFile = new Individual("bestResult.xml");

		//System.out.println("equals() returns: " + best.getGenotype().equals(bestFromFile.getGenotype()));
		//System.out.println(best.getGenotype().get(0));
//		System.out.println(bestFromFile.getGenotype().get(0));
	}

}
