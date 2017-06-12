package gpoker.players;

import evSOLve.JEvolution.Phenotype;
import evSOLve.JEvolution.chromosomes.Chromosome;
import evSOLve.JEvolution.gp.Tree;
import gpoker.gp.fitness.FitnessEvaluator;
import gpoker.misc.Xml;
import org.jdom2.Element;

import java.util.List;

/**
 * The GPlayer is a player evolved by GP in multiple games. The fitness of the
 * GPlayer is evaluated by the corresponding fitness evaluator. The details are defined in the XML configuration.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class GPlayer extends CodePlayer implements Phenotype {

	/** The fitness evaluator. */
	private FitnessEvaluator fe;

	/** The player's fitness. */
	private double fitness;


	/**
	 * Creates the player via XML.
	 *
	 * @param element an XML player element
	 */
	public GPlayer(Element element) {

		super(element);
	}


	/**
	 * A deep clone. The genotype is cloned by JEvolution and with GP the phenotype (program tree) is the genotype.
	 *
	 * @return the cloned phenotype
	 */
	public Object clone() {

		return super.clone();
	}


	/**
	 * Returns the program tree.
	 *
	 * @return the program
	 */
	public Tree getProgram() {

		return program;
	}


	/**
	 * The GPlayers moves cannot be trusted.
	 *
	 * @return false.
	 */
	public boolean isTrusted() {

		return false;
	}


	/**
	 * Returns the fitness evaluator.
	 *
	 * @return the evaluator
	 */
	public FitnessEvaluator getFitnessEvaluator() {

		return fe;
	}


	/**
	 * Sets the fitness evaluator.
	 *
	 * @param fe determines the fitness of the player
	 */
	public void setFitnessEvaluator(FitnessEvaluator fe) {

		this.fe = fe;
	}


	/**
	 * The genotype-phenotype mapper only has to obtain the program tree, which is genotype and phenotype.
	 *
	 * @param genotype the tree chromosome
	 */
	public void doOntogeny(List<Chromosome> genotype) {

		program = (Tree)genotype.get(0).getBases();
	}


	/**
	 * Calculates the fitness..
	 */
	public void calcFitness() {

		fe.evaluate(this);
	}


	/**
	 * Access to the fitness of the phenotype.
	 *
	 * @return The fitness of the phenotype.
	 */
	public double getFitness() {

		return fitness;
	}


	/**
	 * Sets the fitness of this player.
	 *
	 * @param fitness a value
	 */
	public void setFitness(double fitness) {

		this.fitness = fitness;
	}


	/**
	 * Saves the phenotype to XML. This is for XML via JEvolution.
	 *
	 * @param element a JDOM element
	 */
	public void toXml(Element element) {

		Xml.addChildTo(element, "Size", String.valueOf(program.size()));        // just to do something
	}


	/**
	 * Saves this GPlayer to XML. This is for XML via GPoker.
	 *
	 */
	public Element toXml() {

		Element gp = new Element("GPlayer");
		program.toXml(gp);
		return gp;
	}


	/**
	 * The string representation of the phenotype.
	 *
	 * @return a string describing the program tree
	 */
	public String toString() {

		String s = super.toString();
		return s + ": " + program;
	}

}
