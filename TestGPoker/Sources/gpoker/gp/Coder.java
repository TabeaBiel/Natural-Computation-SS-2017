package gpoker.gp;

import evSOLve.JEvolution.gp.ProgramNode;
import evSOLve.JEvolution.gp.Tree;
import evSOLve.JEvolution.misc.TreeIterator;
import gpoker.Player;
import gpoker.misc.Xml;
import gpoker.players.CodePlayer;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.Collection;
import java.util.List;

/**
 * @author Helmut A. Mayer
 * @since May 19, 2016
 *
 */
public class Coder {


	/** Returns a program tree constructed from an XML file.
	 *
	 * @param fileName	the XML file name
	 *
	 * @return          the program tree from XML
	 */
	public static Tree createProgram(String fileName) {

		if (fileName == null)
			return null;

		Document doc = Xml.buildDocument(fileName);
		Element root = doc.getRootElement();
		Element t = root.getChild("Tree");
		if (t == null)
			return null;
		return new Tree(t);
	}


	/** Returns a program tree constructed from a culture file. As the culture file contains a number of players,
	 * the index specifies the player.
	 *
	 * @param fileName	the XML culture file name
	 * @param index		the index of the player in the culture
	 *
	 */
	public static void createProgram(String fileName, int index, CodePlayer player) {

		if (fileName == null)
			return;

		Document doc = Xml.buildDocument(fileName);
		Element root = doc.getRootElement();
		List<Element> pe = root.getChildren("GPlayer");
		Element t = pe.get(index).getChild("Tree");
		player.setProgram(new Tree(t));
		player.setName(player.getName() + "M" + (int)Xml.getProperty(pe.get(index), "Age", 0.0));
	}


	/** Adds all masters of a culture file into the given culture container. The masters are receiving names
	 * based on their age, e.g. "M34".
	 *
	 * @param fileName    an XML culture file
	 * @param culture   a container for the masters
	 */
	public static void createCulture(String fileName, Collection<Player> culture) {

		if (fileName != null) {
			Document doc = Xml.buildDocument(fileName);
			Element root = doc.getRootElement();
			for (Element e : root.getChildren("GPlayer")) {
				Element t = e.getChild("Tree");
				CodePlayer p = new CodePlayer(new Tree(t));
				p.setName("M" + (int)Xml.getProperty(e, "Age", 0.0));
				culture.add(p);
			}
		}
	}

	public static void main(String[] args) {

		Tree player = createProgram("testPlayer.xml");
		System.out.println(player);
		boolean edit;

		do {
			edit = false;
			TreeIterator tit = player.iterator();
			while (tit.hasNext()) {
				ProgramNode node = (ProgramNode)tit.next();
				edit |= node.edit();
			}
		} while (edit);

		System.out.println(player);
	}

}
