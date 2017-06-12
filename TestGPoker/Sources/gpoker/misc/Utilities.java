package gpoker.misc;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Common Utility class.
 *
 * @author Helmut A. Mayer
 * @version $Id: Utilities.java 632 2016-03-31 12:38:48Z helmut $
 * @since June 6, 2001
 */

public class Utilities {

	/**
	 * Limits a value to the given boundaries.
	 *
	 * @param val  a value
	 * @param low  lower boundary
	 * @param high upper boundary
	 */
	public static double limitToRange(double val, double low, double high) {

		if (val < low)
			return low;
		else if (val > high)
			return high;
		else
			return val;
	}


	/**
	 * Search an replace each String Array entry
	 *
	 * @param stringArray the array in which to search
	 * @param oldName     old array entry
	 * @param newName     new array entry
	 */
	public static void searchReplaceStringArray(String[] stringArray, String oldName, String newName) {

		if (stringArray == null)
			return;
		// System.out.println("old: "+oldName+"   new: "+newName);
		for (int i = 0; i < stringArray.length; i++) { // check teams
			if (stringArray[i].equals(oldName))
				stringArray[i] = newName; // change to new name
		}
	}


	/**
	 * Searches the first occurrence of String s in String array a.
	 *
	 * @param a a String array
	 * @param s a String
	 *
	 * @return the index of the first occurrence in the String array, -1 if not found
	 */
	public static int indexOf(String[] a, String s) {

		if (a == null)
			return -1;
		for (int i = 0; i < a.length; i++) {
			if (a[i].equals(s))
				return i;
		}
		return -1;
	}


	/**
	 * Returns the number of occurrences of a given character in a String.
	 *
	 * @param s a String
	 * @param c a character
	 *
	 * @return the count, 0 if s is null
	 */
	public static int getCharacterCount(String s, char c) {

		int count = 0;
		if (s == null)
			return count;

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c)
				++count;
		}
		return count;
	}


	/**
	 * Extracts the file extension.
	 *
	 * @param f the File object
	 *
	 * @return the file name extension, null if no extension
	 */
	public static String getExtension(File f) {

		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
			ext = s.substring(i + 1).toLowerCase();
		return ext;
	}


	/**
	 * Strips off the ending String tail of the complete String whole. If tail is not found, the complete String is returned. ATTENTION: There is a
	 * bug, if tail is anywhere in whole.
	 *
	 * @param whole the complete String
	 * @param tail  the ending String
	 *
	 * @return whole with stripped off tail
	 */
	public static String stripOffTail(String whole, String tail) {

		int strip = whole.lastIndexOf(tail);
		if (strip == -1)
			return whole;
		else
			return whole.substring(0, strip);
	}


	/**
	 * Eliminates the substring before the last occurrence of 'c' including 'c'.
	 *
	 * @param terms String
	 * @param c the delimiting character
	 *
	 * @return a new cut String
	 */
	public static String cutHeadAtLast(String terms, char c) {

		terms = terms.trim();
		int cut = terms.lastIndexOf((int)c);
		if (cut == -1)
			return terms;
		return terms.substring(cut + 1, terms.length());
	}


	/**
	 * Eliminates the substring after the last occurrence of 'c' including 'c'.
	 *
	 * @param terms String
	 * @param c the delimiting character
	 *
	 * @return a new cut String
	 */
	public static String cutTailAtLast(String terms, char c) {

		terms = terms.trim();
		int cut = terms.lastIndexOf((int)c);
		if (cut != -1)
			terms = terms.substring(0, cut);
		return terms;
	}


	/**
	 * Eliminates the substring after the first occurrence of 'c' including 'c'.
	 *
	 * @param terms String
	 * @param c the delimiting character
	 *
	 * @return a new cut String
	 */
	public static String cutTailAtFirst(String terms, char c) {

		terms = terms.trim();
		int cut = terms.indexOf((int)c);
		if (cut != -1) // changed, returned empty string, if c not in terms, now returns terms
			terms = terms.substring(0, cut);
		return terms;
	}


	/**
	 * Adds a plural 's' to the word, if the absolute number is larger than one.
	 *
	 * @param number	a number
	 * @param word		a singular noun
	 *
	 * @return 			plural or singular noun depending on number
	 */
	public static String buildPlural(int number, String word) {

		if (Math.abs(number) > 1)
			word += 's';
		return word;
	}


	/**
	 * Checks, if 's' is a full element of 'parts'. The method returns false, if 's' is only a substring of an element of 'parts'.
	 *
	 * @param s     a String to be checked
	 * @param parts a String array
	 */
	public static boolean isElementOf(String s, String[] parts) {

		for (String p : parts) {
			if (p.equals(s))
				return true;
		}
		return false;
	}


	/**
	 * Returns the indices of the objects in a, which are equal to some in b.
	 *
	 * @param a a Vector
	 * @param b another Vector with some objects also in a
	 *
	 * @return the indices, length may be 0, if no equal objects
	 */
	public static int[] equalObjects(List a, List b) {

		int n = b.size();
		for (int i = 0; i < n; i++) {
			int index = a.indexOf(b.get(i));
			if (index != -1)
				b.add(index); // record indices
		}
		n = b.size() - n;
		int[] equals = new int[n];
		for (int i = 0; i < n; i++) {
			Integer index = (Integer)b.remove(b.size() - 1); // restore correct Vector
			equals[i] = index.intValue();
		}
		return equals;
	}


	/**
	 * Takes a Vector of Strings and a character separator and creates a separated text of these. If the Vector is null or empty, an empty String is
	 * returned.
	 *
	 * @param v         a Vector containing Strings
	 * @param separator a separator character
	 *
	 * @return a String of all Strings in v separated by "separator"
	 */
	public static StringBuffer createText(List v, String separator) {

		StringBuffer text = new StringBuffer();
		if (v == null)
			return text;

		for (int i = 0; i < v.size(); i++) {
			text.append((String)v.get(i));
			if (i < (v.size() - 1)) // cosmetics
				text.append(separator);
		}
		return text;
	}


	/**
	 * Takes an array of Strings and a character separator and creates a separated text of these. If the array is null or empty, null is returned.
	 *
	 * @param parts     an array of Strings
	 * @param separator a separator character
	 *
	 * @return a StringBuffer of all Strings in "parts" separated by "separator"
	 */
	public static StringBuffer createText(String[] parts, String separator) {

		if (parts == null || parts.length == 0)
			return null;
		StringBuffer text = new StringBuffer();

		for (int i = 0; i < parts.length; i++) {
			text.append(parts[i]);
			if (i < (parts.length - 1)) // cosmetics
				text.append(separator);
		}
		return text;
	}


	/**
	 * Takes a String separated by c into sub-strings and creates an array of trimmed sub-strings without the separating string. If the String is null
	 * or empty, null is returned. If the separating string is leading or trailing, it is cut in order to avoid empty strings in the returned array.
	 *
	 * @param s a String separated by c
	 * @param c the separating String
	 *
	 * @return a the array of separated trimmed Strings
	 */
	public static String[] createTextArray(String s, String c) {

		if (s == null)
			return null;
		s = s.trim();
		if (s.equals(""))
			return null;
		int first = s.indexOf(c);
		if (first == 0) // leading c
			s = s.substring(c.length()); // cut
		int last = s.lastIndexOf(c);
		if (last == s.length() - c.length()) // trailing c
			s = s.substring(0, last); // cut
		String[] a = s.split(c);
		for (int i = 0; i < a.length; i++)
			a[i] = a[i].trim();
		return a;
	}


	/**
	 * Takes an array of Objects and creates a Vector with all the objects in the array. If the array is null or empty, null is returned.
	 *
	 * @param o an array of Objects
	 *
	 * @return a Vector with the objects from o
	 */
	public static Vector createVector(Object[] o) {

		if (o == null || o.length == 0)
			return null;
		Vector v = new Vector(o.length);

		Collections.addAll(v, o);

		return v;
	}


	/**
	 * Removes the first n elements of the passed Vector.
	 *
	 * @param v the Vector elements are removed from
	 */
	public static void removeFirstNElements(List v, int n) {

		if (v == null || v.isEmpty())
			return;
		if (v.size() < n)
			n = v.size();
		for (int i = 0; i < n; i++)
			v.remove(0);
	}


	/**
	 * Loads a property file into Java properties. This method creates the properties as well (if they do not exist).
	 *
	 * @param propertyFileName the name of the property file
	 *
	 * @return Java properties, null, if error
	 */
	public static Properties loadProperties(String propertyFileName) {

		Properties p;

		if (propertyFileName == null)
			return null;
		p = new Properties();

		try {
			FileInputStream pSource = new FileInputStream(propertyFileName);
			p.load(pSource);
			pSource.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find property file '" + propertyFileName + "'!");
			System.out.println("Continuing with default values.");
			p = null;
		} catch (IOException f) {
			System.err.println("Could not load properties from property file '" + propertyFileName + "'!");
			System.out.println("Continuing with default values.");
			p = null;
		}
		if (p != null)
			System.out.println("Properties extracted from " + propertyFileName);
		return p;
	}


	/**
	 * Stores properties into a property file.
	 *
	 * @param properties       the properties to store
	 * @param propertyFileName the name of the property file
	 */
	public static void storeProperties(Properties properties, String propertyFileName) {

		if (properties == null || propertyFileName == null)
			return;
		try {
			FileOutputStream pSink = new FileOutputStream(propertyFileName);
			properties.store(pSink, null); // no title, maybe add later
			pSink.close();
		} catch (IOException f) {
			System.err.println("Could not store properties to property file '" + propertyFileName + "'!");
			System.out.println("Property file unchanged.");
		}
	}


	/**
	 * Returns a property or a default value. This method extends the functionality of the Java method by checking, if the properties exist at all.
	 *
	 * @param properties   Java Properties
	 * @param property     the name of the property
	 * @param defaultValue default
	 *
	 * @return property or default, if no property
	 */
	public static String getProperty(Properties properties, String property, String defaultValue) {

		if (properties == null)
			return defaultValue;
		else
			return properties.getProperty(property, defaultValue); // returns default, if not found in properties
	}


	/**
	 * Returns the name of an Object's Class. Primitive Types are encoded specially, so be aware of that (be aware of arrays as well).
	 *
	 * @param o an object
	 *
	 * @return the name of the object's class
	 */

	public static String getClassName(Object o) {

		if (o == null)
			return null;
		return o.getClass().getName();
	}


	/**
	 * Instantiates an Object by passing the name of the Class in a String. Only the no-arg constructor is used. If you want to use any other
	 *
	 * @param className the name of the Class to instantiate
	 *
	 * @return a new Object (null, if problems occurred)
	 *
	 * @see #instantiate(String, String[], Object[]).
	 */
	public static Object instantiateByClassName(String className) {

		try {
			Class aClass = Class.forName(className);
			return aClass.newInstance();
		} catch (ClassNotFoundException e) {
			System.err.println(e);
			return null;
		} catch (InstantiationException e) {
			System.err.println(e + ": could not instantiate " + className);
			return null;
		} catch (IllegalAccessException e) {
			System.err.println(e + ": may not access " + className);
			return null;
		}
	}


	/**
	 * Instantiates an Object by passing the name of the Class in a String. The 'constructorClassNames' must contain the names of the Classes passed
	 * to the desired constructor of the Object. If the profile is null, the no-arg constructor is used. The parameters passed to the constructor must
	 * be in 'constructorParameters'.
	 *
	 * @param className             the name of the Class to instantiate
	 * @param constructorClassNames the names of the Classes of the parameters (may be null)
	 * @param constructorParameters the actual parameters to the constructor (may be null)
	 *
	 * @return a new Object (null, if problems occurred)
	 */
	public static Object instantiate(String className, String[] constructorClassNames, Object[] constructorParameters) {

		try {
			Class aClass = Class.forName(className);
			if (constructorClassNames == null) // just no-arg constructor
				return aClass.newInstance();

			Class[] constructorProfile = new Class[constructorClassNames.length];
			for (int i = 0; i < constructorClassNames.length; i++)
				constructorProfile[i] = createClassByName(constructorClassNames[i]); // this works ALSO on primitive types (int, float, ...)

			Constructor constructor = aClass.getConstructor(constructorProfile);
			return constructor.newInstance(constructorParameters);

		} catch (ClassNotFoundException e) {
			System.err.println(e);
			return null;
		} catch (NoSuchMethodException e) {
			System.err.println(e + ": could not find constructor for " + className);
			return null;
		} catch (InstantiationException e) {
			System.err.println(e + ": could not instantiate " + className);
			return null;
		} catch (IllegalAccessException e) {
			System.err.println(e + ": may not access " + className);
			return null;
		} catch (InvocationTargetException e) {
			System.err.println("While instantiating " + className + ": " + e);
			System.err.println("With the message: " + e.getMessage());
			return null;
		}
	}


	/**
	 * Returns a class by its name inclusively the primitive types and arrays of primitive types, e.g., int[].
	 *
	 * @param cl The name of the class / type.
	 *
	 * @return The class of the class / type.
	 */
	public static Class createClassByName(String cl) throws ClassNotFoundException {

		switch (cl) {
			case "int":  // primitive type class
				if (cl.contains("[]")) // Array type primitive type
					return int[].class;
				else
					return int.class;
			case "long":
				if (cl.contains("[]"))
					return long[].class;
				else
					return long.class;
			case "float":
				if (cl.contains("[]"))
					return float[].class;
				else
					return float.class;
			case "double":
				if (cl.contains("[]"))
					return double[].class;
				else
					return double.class;
			case "boolean":
				if (cl.contains("[]"))
					return boolean[].class;
				else
					return boolean.class;
		}
		return Class.forName(cl); // not a primitive type
	}


	/**
	 * Takes the first 'num' bits starting at 'offset' in a BitSet and converts them into a boolean array.
	 *
	 * @param b      a BitSet
	 * @param num    the number of bits to convert from the BitSet
	 * @param offset where to start conversion
	 *
	 * @return the boolean array, null if problems
	 */
	public static boolean[] createBooleanArray(BitSet b, int num, int offset) {

		if (b == null || num == 0)
			return null;

		boolean[] ba = new boolean[num];

		for (int i = 0; i < num; i++)
			ba[i] = b.get(i + offset);

		return ba;
	}


	/**
	 * Converts a bit gene in a scaled double value. WARNING: this method only works up to a length of 31 (due to the size of int), could be improved
	 * to 63 by using long.
	 *
	 * @param gene   the bit gene
	 * @param from   the first position to decode
	 * @param length the number of bits to decode
	 * @param low    the lower bound of the value to be expressed
	 * @param high   the upper bound of the value to be expressed
	 *
	 * @return the expressed double value scaled to [low, high]
	 */
	public static double express(BitSet gene, int from, int length, double low, double high) {

		int actualValue = 0;

		for (int i = 0; i < length; i++) { // bits to int
			actualValue <<= 1;
			if (gene.get(from + i))
				actualValue |= 1;
		}
		int maxValue = (1 << length) - 1;
		return low + (high - low) * (double)actualValue / (double)maxValue;
	}
	//
}
