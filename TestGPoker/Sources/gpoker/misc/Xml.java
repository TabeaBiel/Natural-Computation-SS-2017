package gpoker.misc;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A class assisting in XML processing based on JDOM.
 *
 * @author Helmut A. Mayer
 * @version $Id: Xml.java 646 2016-05-27 20:50:15Z helmut $
 * @since October 6, 2005
 */
public class Xml {

	/**
	 * Returns a JDOM document built from a file, or from scratch, if the file does not exist.
	 *
	 * @param fileName the name of the file
	 *
	 * @return a JDOM document (may be empty)
	 */
	static public Document buildDocument(String fileName) {

		File file = new File(fileName);
		Document document = null;

		if (file.exists()) {
			try {
				SAXBuilder builder = new SAXBuilder(); // may be validated against a doc type, but we have no DTD
				document = builder.build(file);
			} catch (JDOMException | IOException jde) {
				jde.printStackTrace();
			}
		}
		if (document == null)
			document = new Document();
		return document;
	}


	/**
	 * Creates a child element and adds it to parent.
	 *
	 * @param parent the parent element
	 * @param name   the child's name
	 * @param text   the child's text
	 *
	 * @return the child
	 */
	static public Element addChildTo(Element parent, String name, String text) {

		Element child = new Element(name);
		child.setText(text);
		parent.addContent(child);

		return child;
	}


	/**
	 * Returns the child of an element and creates the child, if it does not exist.
	 *
	 * @param element a JDOM element
	 * @param name    the child's name
	 *
	 * @return the child
	 */
	static public Element getChildOf(Element element, String name) {

		Element child = element.getChild(name);
		if (child == null) {
			child = new Element(name);
			element.addContent(child);
		}
		return child;
	}


	/**
	 * Returns the content of either a child or a attribute of node parent
	 *
	 * @param node     The node
	 * @param property specifies the name of the desired property
	 *
	 * @return the content of the child or attribute of the node
	 */
	public static String getNodeContent(Element node, String property) {

		if (node == null)
			return null;
		Element child = node.getChild(property);
		if (child != null)
			return child.getText();
		return node.getAttributeValue(property);
	}


	/**
	 * Writes a JDOM document to a file of given name.
	 *
	 * @param document a JDOM document
	 * @param fileName the name of the file
	 */
	static public void save(Document document, String fileName) {

		try {
			XMLOutputter out = new XMLOutputter();
			out.setFormat(Format.getPrettyFormat());
			out.output(document, new FileOutputStream(new File(fileName)));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Writes a JDOM tree to a file of given name.
	 *
	 * @param root		the root of a JDOM tree
	 * @param fileName 	the name of the file
	 */
	static public void save(Element root, String fileName) {

		Document document = new Document();
		document.setRootElement(root);
		save(document, fileName);
	}


	/**
	 * Returns the content of either a child node or an attribute of the XML element. <br>
	 * If the xml element or the child or the attribute is null the specified default value is returned.
	 *
	 * @param properties   XML element
	 * @param propertyName the name of the property
	 * @param defaultValue default
	 *
	 * @return property or default, if undefined
	 */
	public static String getProperty(Element properties, String propertyName, String defaultValue) {

		if (properties == null)
			return defaultValue;

		String content = getNodeContent(properties, propertyName);
		if (content == null || content.equals(""))
			return defaultValue;

		return content;
	}


	/**
	 * Returns the content of either a child node or an attribute of the XML element. If the xml element or the child or the attribute is null or a
	 * parse error occurs the specified default value is returned.
	 *
	 * @param properties   XML element
	 * @param propertyName the name of the property
	 * @param defaultValue default
	 *
	 * @return property or default, if undefined
	 */
	public static int getProperty(Element properties, String propertyName, int defaultValue) {

		if (properties == null)
			return defaultValue;

		int value;
		String strProp = getNodeContent(properties, propertyName);
		if (strProp == null) {
			// desired property not found!
			// now add it to XML structure
			addChildTo(properties, propertyName, Integer.toString(defaultValue));
			value = defaultValue;
		} else {
			try {
				value = Integer.parseInt(strProp);
			} catch (NumberFormatException e) {
				value = defaultValue;
			}
		}
		return value;
	}


	/**
	 * Returns the content of either a child node or an attribute of the XML element. If the xml element or the child or the attribute is null or a
	 * parse error occurs the specified default value is returned.
	 *
	 * @param properties   XML element
	 * @param propertyName the name of the property
	 * @param defaultValue default
	 *
	 * @return property or default, if undefined
	 */
	public static double getProperty(Element properties, String propertyName, double defaultValue) {

		if (properties == null)
			return defaultValue;

		double value;
		String strProp;
		strProp = getNodeContent(properties, propertyName);

		if (strProp == null) {
			// desired property not found!
			// now add it to XML structure
			addChildTo(properties, propertyName, Double.toString(defaultValue));
			value = defaultValue;
		} else {
			try {
				value = Double.parseDouble(strProp);
			} catch (NumberFormatException e) {
				value = defaultValue;
			}
		}
		return value;
	}


	/**
	 * Returns the content of either a child node or an attribute of the XML element. If the xml element or the child or the attribute is null the
	 * specified default value is returned.
	 *
	 * @param properties   XML element
	 * @param propertyName the name of the property
	 * @param defaultValue default
	 *
	 * @return property or default, if undefined
	 */
	public static boolean getProperty(Element properties, String propertyName, boolean defaultValue) {

		if (properties == null)
			return defaultValue;

		boolean value;
		String strProp;
		strProp = getNodeContent(properties, propertyName);

		if (strProp == null) {
			// desired property not found!
			// now add it to XML structure
			addChildTo(properties, propertyName, Boolean.toString(defaultValue));
			value = defaultValue;
		} else {
			value = Boolean.parseBoolean(strProp);
		}
		return value;
	}
	//
}