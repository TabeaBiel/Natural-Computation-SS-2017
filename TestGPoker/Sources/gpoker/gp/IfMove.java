package gpoker.gp;

import evSOLve.JEvolution.gp.NullNode;
import evSOLve.JEvolution.gp.ProgramNode;
import evSOLve.JEvolution.gp.nodes.ConstBoolean;
import gpoker.Move;
import org.jdom2.Element;

/**
 * A function node returning a poker move. The left child represents a condition which if true, evaluates
 * the center child, otherwise the right child.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class IfMove extends ProgramNode {

	/** The TYPE_MOVE identifier. */
	public static final int TYPE_MOVE = 666;


	/** Constructs the function with three operands. */
	public IfMove() {

		children = new ProgramNode[3];
	}


	/**
	 * The XML constructor.
	 *
	 * @param element an XML tag
	 */
	public IfMove(Element element) {

		this();
	}


	/**
	 * Returns the operand type of the children.
	 *
	 * @param index	a child index
	 *
	 * @return	the type of the child given by index
	 */
	public int getOperandType(int index) {

		if (index == 0)
			return TYPE_BOOLEAN;

		return TYPE_MOVE;
	}


	/**
	 * Returns the result type.
	 *
	 * @return TYPE_INT.
	 */
	public int getResultType() {

		return TYPE_MOVE;
	}


	/**
	 * Evaluates the if condition and returns the value of the true or false path.
	 *
	 * @return boolean value of the path selected by the condition.
	 */
	public Object eval() {

		Object ante = children[0].eval();	// condition
		Object cons;						// consequence

		boolean a = ante != null && (boolean)ante;
		cons = a ? children[1].eval() : children[2].eval();			// true or false path

		return cons == null ? new Move(Move.FOLD) : cons;
	}


	/**
	 * Exchanges true and false path.
	 */
	public void mutate() {

		ProgramNode n = children[2];
		children[2] = children[1];
		children[1] = n;
	}


	/**
	 * Edits the node in the following way:
	 *
	 * <li>if the condition node is  <code>ConstBoolean</code> or a <code>NullNode</code> this node is replaced by
	 * the corresponding branch
	 * </li>
	 *
	 * @return true, if edited
	 */
	public boolean edit() {

		if (getLevel() == 0)
			return false;  											// TODO cannot edit root

		ProgramNode editNode = null;

		if (children[0] instanceof ConstBoolean || children[0] instanceof NullNode) {
			Boolean val = (Boolean)children[0].eval();
			if (val == null)											// if from NullNode
				val = false;
			editNode = val ? children[1] : children[2];
			replace(editNode);											// replace this node with the constant path
		}
		return editNode != null;
	}


	/**
	 * Returns the string signature of the node.
	 *
	 * @return the node's signature.
	 */
	public String getSignature() {

		return "ifm";
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
