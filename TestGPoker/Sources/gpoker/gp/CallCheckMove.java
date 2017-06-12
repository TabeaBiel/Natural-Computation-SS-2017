package gpoker.gp;

import evSOLve.JEvolution.gp.ProgramNode;
import evSOLve.JEvolution.misc.Utilities;
import evSOLve.JEvolution.misc.Xml;
import gpoker.Move;
import org.jdom2.Element;

/**
 * This move terminal can only be CHECK or CALL.
 * 
 * @author Helmut A. Mayer
 * @since  September 19, 2016
 */
public class CallCheckMove extends ProgramNode {

	/** The nodes move. */
	private Move move;


	/** The default constructor. */
	public CallCheckMove() {

		move = new Move(Move.CHECK);
		randomize();
	}


	/** The XML constructor.
	 *
	 * @param element	an XML tag
	 */
	public CallCheckMove(Element element) {

		this();
		move.setType(Xml.getProperty(element, "Type", Move.CHECK));
	}


	/** A deep clone.
	 *
	 * @return	the clone
	 */
	public ProgramNode clone() {

		CallCheckMove clone = (CallCheckMove)super.clone();
		clone.move = move.clone();

		return clone;
	}


	/**
	 * Returns the result type Move.
	 * 
	 * @return	TYPE_MOVE
	 */
	public int getResultType() {
		return IfMove.TYPE_MOVE;
	}

	
	/**
	 * Evaluates the node and returns the move.
	 * 
	 * @return	the move
	 */
	public Object eval() {

		return move;
	}

	
	/** Picks a random move. */
	public void randomize() {

		int type = Utilities.nextIntegerInRange(Move.CHECK, Move.CALL);
		move.setType(type);
	}

	
	/**
	 * Toggles the move.
	 */
	public void mutate() {

		int type = move.getType() == Move.CHECK ? Move.CALL : Move.CHECK;
		move.setType(type);
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
		return n;
	}


	/**
	 * Returns the string representation of the node.
	 * 
	 * @return the node's string representation.
	 */
	public String toString() {

		return getSignature() + "[" + move + "]";
	}
	
}