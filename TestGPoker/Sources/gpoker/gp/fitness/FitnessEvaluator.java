package gpoker.gp.fitness;

import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import gpoker.players.GPlayer;
import org.jdom2.Element;

/** The fitness evaluator determining the fitness of an evolved player.
 *
 * @author Helmut A. Mayer
 * @since May 10, 2016
 */
public abstract class FitnessEvaluator {


	/** Creates an evaluator by its class name found in XML.
	 *
	 * @param element	the XML element with evaluator information
	 *
	 * @return          the evaluator, null if problems
	 */
	public static FitnessEvaluator create(Element element) {

		String className = Xml.getProperty(element, "class", "OneSeat");
		className = "gpoker.gp.fitness." + className;
		String[] constructorClassNames = {"org.jdom2.Element"};
		Object[] constructorParameters = {element};

		return (FitnessEvaluator)Utilities.instantiate(className, constructorClassNames, constructorParameters);
	}


	/** Returns the XML representation of the evaluator. This default implementation returns null.
	 *
	 * @return          the child node representing the fitness evaluator (here null)
	 */
	public Element toXml() {

		return null;
	}


	/** Evaluates the fitness of an evolved player.
	 *
	 * @param player	an evolved player
	 */
	public abstract void evaluate(GPlayer player);

}
