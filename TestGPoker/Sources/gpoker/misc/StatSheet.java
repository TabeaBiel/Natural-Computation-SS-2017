package gpoker.misc;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/** The StatSheet allows to record an arbitrary number of scalar data and time series. This is useful for collecting
 * data during experiment runs.
 *
 * @author Helmut A. Mayer
 * @since May 19, 2016
 */
public class StatSheet {

	/** The list of scalars. */
	private List<Scalar> scalars;

	/** The list of time series. */
	private List<TimeSeries> timeSeries;

	/** Constructs a stat sheet. */
	public StatSheet() {

	}


	/**
	 * Adds a scalar to the stat sheet.
	 *
	 * @param scalar a scalar
	 */
	public void add(Scalar scalar) {

		if (scalars == null)
			scalars = new ArrayList<>(1);

		scalars.add(scalar);
	}


	/**
	 * Adds a time series to the stat sheet.
	 *
	 * @param series a time series
	 */
	public void add(TimeSeries series) {

		if (timeSeries == null)
			timeSeries = new ArrayList<>(1);

		timeSeries.add(series);
	}


	/**
	 * Returns the scalar with given index.
	 *
	 * @param index the index of the scalar in the stat sheet
	 *
	 * @return the scalar
	 */
	public Scalar getScalar(int index) {

		return scalars.get(index);
	}


	/**
	 * Returns the scalar with given name.
	 *
	 * @param name the name of the scalar
	 *
	 * @return the first scalar with given name, null if not found
	 */
	public Scalar getScalar(String name) {

		for (Scalar s : scalars) {
			if (s.getName().equals(name))
				return s;
		}
		return null;
	}


	/**
	 * Returns the time series with given index.
	 *
	 * @param index the index of the time series in the stat sheet
	 *
	 * @return the time series
	 */
	public TimeSeries getTimeSeries(int index) {

		return timeSeries.get(index);
	}


	/**
	 * Returns the time series with given name.
	 *
	 * @param name the name of the time series
	 *
	 * @return the first time series with given name, null if not found
	 */
	public TimeSeries getTimeSeries(String name) {

		for (TimeSeries t : timeSeries) {
			if (t.getName().equals(name))
				return t;
		}
		return null;
	}


	/** Adds the XML description to the given element.
	 *
	 * @param e		an XML tag
	 *
	 */
	public void toXml(Element e) {

		if (scalars != null) {
			for (Scalar s : scalars)
				s.toXml(e);
		}
		if (timeSeries != null) {
			for (TimeSeries ts : timeSeries)
				ts.toXml(e);
		}
	}

}
