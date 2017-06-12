package gpoker.misc;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/** A time series holds a number of values, which may be arranged in multiple dimensions. A time series with dim = 1 is
 * a simple series of values. A series with dim = 2 could be function values of a function f(x), where the first value is
 * the x value and the next is the function value.
 *
 * @author Helmut A. Mayer
 * @since May 19, 2016
 */
public class TimeSeries {

	/** The name. */
	private String name;

	/** The values of the time series. */
	private List<Double> values;

	/** The dimension of the time series. */
	private int dim;


	/** Constructs a time series with dimension 1.
	 *
	 * @param name	the name
	 */
	public TimeSeries(String name) {

		this(name, 1);
	}


	/** Constructs a time series.
	 *
	 * @param name		the name
	 * @param dim		the dimension
	 */
	public TimeSeries(String name, int dim) {

		this.name = name;
		this.dim = dim;
		values = new ArrayList<>();
	}


	/** Returns the name of the time series.
	 *
	 * @return		the name
	 */
	public String getName() {

		return name;
	}


	/** Returns the time series values.
	 *
	 * @return		the time series
	 */
	public List<Double> getValues() {

		return values;
	}


	/** Adds a value to the time series.
	 *
	 * @param value		a value
	 */
	public void addValue(double value) {

		values.add(value);
	}


	/** Adds a number of values to the time series.
	 *
	 * @param values		a number of values
	 */
	public void addValues(double... values) {

		for (double val : values)
			this.values.add(val);
	}


	/** Returns the maximal value of the time series considering the dimension and assuming that the function value
	 * is the one with highest index in a dim-tuple, e.g. (1, 3, 2, 4) with dimension 2 returns 4.
	 *
	 * @return	the maximal value
	 *
	 */
	public double getMax() {

		double max = -Double.MAX_VALUE;

		for (int i = dim - 1; i < values.size(); i += dim) {
			  if (values.get(i) > max)
				  max = values.get(i);
		}
		return max;
	}


	/** Adds the XML representation of the time series to the given element.
	 *
	 * @param e		an XML tag
	 *
	 */
	public void toXml(Element e) {

		Xml.addChildTo(e, name, toString());
	}


	/** Returns the string representation of the time series, where dim values are on each line separated by a blank.
	 * This format is tailored to gnuplot.
	 *
	 * @return	a string describing the time series
	 */
	public String toString() {

		String s = "";
		int d = 0;

		for (double val : values) {
			s += val;
			if (++d == dim) {
				s += '\n';
				d = 0;
			}
			else
				s += " ";
		}

		return s;
	}

}
