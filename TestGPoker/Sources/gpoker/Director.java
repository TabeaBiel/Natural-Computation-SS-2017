package gpoker;

import gpoker.gp.GPoker;
import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import gpoker.modes.GameMode;
import gpoker.modes.TournamentMode;
import gpoker.players.GPlayer;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The director manages all parts of GPoker.
 *
 * @author Edgar Ebensperger
 * @author Helmut A. Mayer
 */
public class Director {

	/** The singleton. */
	private static Director director;

	/** The technical trivia. */
	private Document document;

	/** The configuration file name. */
	private static String xmlFileName = "sample2.xml";

	/** The poker dealer. */
	private Dealer dealer;

	/** The tournament mode. */
	private TournamentMode tournamentMode;

	/** The reporter. */
	private Reporter reporter;

	/**
	 * Constructs the singleton.
	 */
	private Director() {

	}


	/**
	 * Returns the singleton.
	 *
	 * @return the director singleton.
	 */
	public static Director getInstance() {

		if (director == null)
			director = new Director();
		return director;
	}


	/** Returns the tournament mode.
	 *
	 * @return		the mode
	 */
	public TournamentMode getTournamentMode() {

		return tournamentMode;
	}


	/**
	 * Returns the dealer.
	 *
	 * @return dealer.
	 */
	public Dealer getDealer() {

		return dealer;
	}


	/** Returns the reporter. */
	public Reporter getReporter() {

		return reporter;
	}


	/**
	 * Returns the GPoker document.
	 *
	 * @return the document with all attributes.
	 */
	public Document getDocument() {

		return document;
	}


	/** Starts the game. If a GPlayer is among the players, this player wants to be evolved, so evolution is started.
	 * Otherwise, it is a normal game and the dealer starts it. */
	public void start() {

		Player player = getPlayer("GPlayer");
		if (player != null) {
			reporter.setLevel(Reporter.QUIET);										// no output from poker reporter
			GPoker.evolve((GPlayer)player, Xml.getChildOf(document.getRootElement(), "Evolution"));
			for (Player p : dealer.getPlayers())
				System.out.println(p);
		} else
			tournamentMode.start();
	}


	/** Sets up XML, dealer, players, and the game mode. */
	private void setup() {

		document = Xml.buildDocument(xmlFileName);
		reporter = new Reporter(Xml.getChildOf(document.getRootElement(), "Reporter"));
		dealer = Dealer.create(Xml.getChildOf(document.getRootElement(), "Dealer"));
		dealer.setGameMode(GameMode.create(Xml.getChildOf(document.getRootElement(), "GameMode")));
		tournamentMode = TournamentMode.create(Xml.getChildOf(document.getRootElement(), "TournamentMode"));

		List<Element> players = document.getRootElement().getChildren("Player");
		for (Element e : players){
			tournamentMode.addPlayer(Player.create(e));
			//added
			dealer.addPlayer(Player.create(e));
		}

	}


	/**
	 * Checks if a specific player takes part in the game.
	 *
	 * @param className		the unqualified class name of a player
	 *
	 * @return				the player with the class name, null if none
	 */
	public Player getPlayer(String className) {

		for (Player p : tournamentMode.getPlayers()) {
			if (p.getClass().getName().contains(className))
				return p;
		}
		return null;
	}


	/**
	 * The entry point.
	 *
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {

		boolean validOption = false;

		if (args.length > 0) {
			if (Utilities.indexOf(args, "-h") != -1) {
				printHelp();
				return;
			}
			if (Utilities.indexOf(args, "-x") != -1) {
				xmlFileName = args[Utilities.indexOf(args, "-x") + 1];
				validOption = true;
			}
			if (!validOption) {
				System.err.println("Unknown option '" + args[0] + "' !");
				printHelp();
				return;
			}
		}

		Director director = Director.getInstance();
		director.setup();
		director.start();
		director.printStats();
	}

	//added
	private void printStats(){
		dealer.printStats();
	}


	/**
	 * Prints help text on usage of GPoker.
	 */
	private static void printHelp() {

		System.out.println("Usage: director [-options]");
		System.out.println("-x <fileName>    use the XML configuration file with <fileName>, default 'gpoker.xml'");
		System.out.println("-h               prints this text");
	}



}
