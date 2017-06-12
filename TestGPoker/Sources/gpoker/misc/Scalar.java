package gpoker.misc;

import org.jdom2.Element;

/** The scalar is a single value with a name.
 *
 * @author Helmut A. Mayer
 * @since May 19, 2016
 */
public class Scalar {

	/** The name of the scalar value. */
	private String name;

	/** The value. */
	private double value;


	/** Constructs the scalar.
	 *
	 * @param name		the name
	 * @param value     the value
	 */
	public Scalar(String name, double value) {

		this.name = name;
		this.value = value;
	}


	/** Returns the scalar's name.
	 *
	 * @return		the name
	 */
	public String getName() {

		return name;
	}


	/** Returns the scalar's value. */
	public double getValue() {

		return value;
	}


	/** Adds the XML description of the scalar to the given element.
	 *
	 * @param e		an XML tag
	 */
	public void toXml(Element e) {

		Xml.addChildTo(e, name, String.valueOf(value));
	}

}
