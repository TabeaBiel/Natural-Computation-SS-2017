package gpoker.gp;

import evSOLve.JEvolution.gp.ProgramNode;
import gpoker.Director;
import gpoker.Move;
import org.jdom2.Element;

/**
 * The raise move with a bet to be gathered from the children.
 * 
 * @author Helmut A. Mayer
 * @since September 19, 2016
 */
public class RaiseMove extends ProgramNode {

	/** The node's move. */
	private Move move;


	/** The default constructor. */
	public RaiseMove() {

		children = new ProgramNode[1];
		move = new Move(Move.RAISE);
	}


	/** The XML constructor.
	 *
	 * @param element	an XML tag
	 */
	public RaiseMove(Element element) {

		this();
	}


	/** A deep clone.
	 *
	 * @return	the clone
	 */
	public ProgramNode clone() {

		RaiseMove clone = (RaiseMove)super.clone();
		clone.move = move.clone();

		return clone;
	}


	/**
	 * Returns the operand type of the child.
	 *
	 * @param index	a child index
	 *
	 * @return	the type of the child given by index
	 */
	public int getOperandType(int index) {

		return TYPE_DOUBLE;
	}


	/**
	 * Returns the result type.
	 * 
	 * @return		TYPE_MOVE
	 */
	public int getResultType() {
		return IfMove.TYPE_MOVE;
	}

	
	/**
	 * Evaluates the node and returns the raise with the bet.
	 * 
	 * @return	the raise move
	 */
	public Object eval() {

		Object val = children[0].eval();
		double potFactor = val == null ? 0.0 : (Double)val;
		move.setBet((int)(potFactor * Director.getInstance().getDealer().getPot()));	// not too pretty

		return move;
	}

	
	/**
	 * Returns the string signature of the node.
	 * 
	 * @return the node's signature.
	 */	
	public String getSignature() {

		return "rm";
	}


	/** Saves the raise move to XML.
	 *
	 * @param parent	the parent element
	 *
	 * @return			the node element
	 *
	 */
	public Element toXml(Element parent) {

		return super.toXml(parent);
	}


	/**
	 * Returns the string representation of the node.
	 * 
	 * @return the node's string representation.
	 */
	public String toString() {

		return getSignature();
	}
	
}