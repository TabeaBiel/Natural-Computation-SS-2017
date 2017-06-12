package gpoker.gp.fitness;

import evSOLve.JEvolution.JEvolution;
import evSOLve.JEvolution.misc.Utilities;
import gpoker.Dealer;
import gpoker.Director;
import gpoker.Player;
import gpoker.misc.Scalar;
import gpoker.misc.StatSheet;
import gpoker.misc.TimeSeries;
import gpoker.misc.Xml;
import gpoker.players.GPlayer;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * The Culture evaluator determines the fitness of a player by letting it play games against master players in a
 * culture. The culture grows dynamically, as a player beating every culture player is added to the culture.
 *
 * @author Helmut A. Mayer
 * @since May 13, 2016
 */
public class Culture extends FitnessEvaluator {

	/** The number of players in a game. */
	private int playersPerTable;

	/** The number of players in a population. */
	private int totalEntries;

	/** The current number of players having played a game. */
	private int entries;

	/** The culture. */
	private List<GPlayer> culture;

	/* A culture candidate. */
	private GPlayer candidate;

	/** A stat sheet. */
	private StatSheet statSheet;


	/**
	 * Creates the evaluator via XML.
	 *
	 * @param element an XML player element
	 */
	public Culture(Element element) {

		playersPerTable = Xml.getProperty(element, "PlayersPerTable", 2);
		totalEntries = JEvolution.getInstance().getPopulationSize();
		culture = new ArrayList<>();
		statSheet = new StatSheet();
		statSheet.add(new TimeSeries("MasterCount"));
	}


	/**
	 * Evaluates the fitness by playing games against the culture. A player beating all culture players is considered
	 * as a culture candidate, if it is better than a potential former candidate. At the end of a generation the candidate
	 * (if there is one) is added to the culture.
	 *
	 * @param player	an evolved player
	 *
	 */
	public void evaluate(GPlayer player) {

		if (entries == 0 && JEvolution.getInstance().getGenerationCount() == 0) {		// HACK
			JEvolution.getInstance().getParents().setMigrationInterval(50);
			JEvolution.getInstance().getParents().setMigrationRate(0.0);
		}
		++entries;
		player.setFitness(0.0);							// reset
		if (culture.size() == 0)
			add((GPlayer)player.clone());                // first master is random
		playGames(player);
		if (entries == totalEntries) {
			tendCulture();
			entries = 0;
			System.out.println(this);
		}
	}


	/** Takes care of the culture by eliminating losing masters and adding a potential candidate. */
	private void tendCulture() {

		for (int i = culture.size() - 1; i >= 0; i--) {
			GPlayer master = culture.get(i);
			if (master.getFitness() < 0.0) {
				culture.remove(master);
				System.out.println("\nRemoved master with age " + (int)master.getStatSheet().getScalar(0).getValue());
			}
			else
				master.getStatSheet().getTimeSeries(0).addValue(master.getFitness());		// record earnings
		}
		if (candidate != null) {
			add((GPlayer)candidate.clone());
			candidate = null;
		}
		statSheet.getTimeSeries(0).addValue(culture.size());
	}


	/** Adds a player and sets up its stat sheet.
	 *
	 * @param player	an evolved player
	 */
	private void add(GPlayer player) {

		StatSheet stats = player.getStatSheet();
		stats.add(new Scalar("Age", JEvolution.getInstance().getGenerationCount()));
		stats.add(new Scalar("Sub", entries / 50));									// TODO improve
		stats.add(new TimeSeries("Earnings"));
		stats.getTimeSeries(0).addValue(player.getFitness());
		culture.add(player);
		System.out.println("\nMaster #" + culture.size() + " " + player);
	}


	/** Plays games against each culture player. The wins of each game are added to the fitness of the player.
	 * A player beating all culture players is considered as a culture candidate, if it is better than a potential
	 * former candidate.
	 *
	 *
	 * @param player	a culture opponent
	 *
	 */
	private void playGames(GPlayer player) {

		Dealer dealer = Director.getInstance().getDealer();
		List<Player> table = dealer.getPlayers();
		table.clear();
		table.add(player);
		int winCount = 0;

		for (GPlayer p : culture) {
			table.add(p);
			dealer.start();
			if (player.getWins() > 0) 									// do not consider a tie winner
				++winCount;
			player.setFitness(player.getFitness() + player.getWins());	// add on to previous wins
			p.setFitness((p.getFitness() + p.getWins()));				// add on culture
			table.remove(p);
		}
		if (winCount == culture.size()) {
//			if (candidate == null || player.getFitness() > candidate.getFitness())
//				candidate = player;
			add((GPlayer)player.clone());
		}
	}


	/** Returns the XML representation of the culture.
	 *
	 * @return			the culture in XML
	 */
	public Element toXml() {

		Element c = new Element("Culture");

		for (GPlayer gp : culture) {
			Element e = new Element("GPlayer");
			c.addContent(e);
			gp.getProgram().toXml(e);
			gp.getStatSheet().toXml(e);
		}
		statSheet.toXml(c);

		return c;
	}


	/**
	 * The string representation of the culture.
	 *
	 * @return a string describing the culture
	 */
	public String toString() {

		String s = "\nMasters" + '\n';

		for (GPlayer p : culture) {
			s += "Chips: " + Utilities.fixedPoint.format(p.getFitness() / 1.0E6) + "M ";
			s += "MaxChips: " + Utilities.fixedPoint.format(p.getStatSheet().getTimeSeries(0).getMax() / 1.0E6) + "M ";
			s += "Sub: " + (int)p.getStatSheet().getScalar("Sub").getValue() + " ";
			s += "Age: " + (int)p.getStatSheet().getScalar("Age").getValue() + '\n';
		}
		return s;
	}

}
