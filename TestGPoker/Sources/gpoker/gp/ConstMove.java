package gpoker.gp;

import evSOLve.JEvolution.gp.ProgramNode;
import evSOLve.JEvolution.misc.Utilities;
import evSOLve.JEvolution.misc.Xml;
import gpoker.Director;
import gpoker.Move;
import org.jdom2.Element;

/**
 * Constant move terminal (node) can either be fold, check, call or raise.
 * 
 * @author Edgar Ebensperger.
 */
public class ConstMove extends ProgramNode {

	/** The nodes move. */
	private Move move;

	/** The pot factor for betting. */
	private double potFactor;

	/** The mutation factor to be used for the choice of mutation variant. */
	private double mutFactor;


	/** The default constructor. */
	public ConstMove() {

		move = new Move(Move.FOLD);
		randomize();
	}


	/** The XML constructor.
	 *
	 * @param element	an XML tag
	 */
	public ConstMove(Element element) {

		this();
		move.setType(Xml.getProperty(element, "Type", Move.FOLD));
		potFactor = Xml.getProperty(element, "PotFactor", 1.0);				// TODO mutation factor not in XML
	}


	/** A deep clone.
	 *
	 * @return	the clone
	 */
	public ProgramNode clone() {

		ConstMove clone = (ConstMove)super.clone();
		clone.move = move.clone();

		return clone;
	}


	/**
	 * Returns the result type Move.
	 * 
	 * @return TYPE_MOVE.
	 */
	public int getResultType() {
		return IfMove.TYPE_MOVE;
	}

	
	/**
	 * Evaluates the node and returns the move.
	 * 
	 * @return The move of the node.
	 */
	public Object eval() {

		if (move.getType() == Move.RAISE)
			move.setBet(((int)(potFactor * Director.getInstance().getDealer().getPot())));		// not too pretty
		return move;
	}

	
	/**
	 * Changes the move of the node in a random fashion.
	 */
	public void randomize() {
		
		int type = Utilities.nextIntegerInRange(Move.FOLD, Move.RAISE);
		move.setType(type);
		potFactor = Utilities.nextRealInRange(0.0, 2.0);				// 0-2 pot size
		mutFactor = Math.random();
	}

	
	/**
	 * Mutates the node depending on the mutation factor. Either the move type is mutated or the pot factor. The idea
	 * behind this is that a RAISE move may be established, which can then be fine-tuned with the pot factor.
	 */
	public void mutate() {

		if (mutFactor < Math.random()) {
			int type = Utilities.nextIntegerInRange(Move.FOLD, Move.RAISE);		// mutation of move type
			move.setType(type);
		}
		else {
			double delta = potFactor * 0.05;
			potFactor += Utilities.nextRealInRange(-delta, delta);				// small uniform mutation of pot factor
		}
	}

	
	/**
	 * Returns the string signature of the node.
	 * 
	 * @return the node's signature.
	 */	
	public String getSignature() {
		return "cm";
	}


	/** Saves the constant move to XML.
	 *
	 * @param parent	the parent element
	 *
	 * @return			the node element
	 *
	 */
	public Element toXml(Element parent) {

		Element n = super.toXml(parent);

		Xml.addChildTo(n, "Type", String.valueOf(move.getType()));
		if (move.getType() == Move.RAISE)
			Xml.addChildTo(n, "PotFactor", String.valueOf(potFactor));
		return n;
	}


	/**
	 * Returns the string representation of the node.
	 * 
	 * @return the node's string representation.
	 */
	public String toString() {

		String s = move.toString();

		if (move.getType() == Move.RAISE)
			s += "(" + Utilities.fixedPoint.format(potFactor) + ")";

		return getSignature() + "[" + s + "]";
	}
	
}