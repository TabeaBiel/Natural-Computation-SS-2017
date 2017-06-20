package gpoker;

import gpoker.misc.Utilities;
import gpoker.misc.Xml;
import gpoker.modes.GameMode;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MyDealer extends Dealer{

	private Map<String, PlayerStat> stats;

	public MyDealer(){
		super();
		stats = new HashMap<String, PlayerStat>();
	}

	public MyDealer(Element element){
		super(element);
		stats = new HashMap<String, PlayerStat>();
	}

	@Override
	protected boolean doBettingRound(Player player) {
		if (stage > PRE_FLOP)
			player = button;									// post-flop
		player = getNextPlayer(player);
		allowMoves();											// all active may make a move

		while (player.hasMove()) {
			if (player.hasChips()) {
				Move move = player.act();

				if (!player.isTrusted() || move.getType() == Move.RAISE)
					checkMove(player, move);						// is it allowed?
				executeMove(player, move);
				analyseMove(player, move);
				if (activePlayers.size() == 1)
					return false;                                    // end of hand
			}
			else
				Director.getInstance().getReporter().reportAllIn(player);
			player.setMove(false);									// has had its right to move
			player = getNextPlayer(player);
		}
		return true;												// hand continues
	}

	@Override
	public void addPlayer(Player player) {

		players.add(player);

		stats.put(player.getName(), new PlayerStat());
	}

	private void analyseMove(Player player, Move move){
		PlayerStat stat = stats.get(player.getName());
		int type = move.getType();

		switch(type){
			case 0:
				stat.addFold(stage);
				break;
			case 1:
				stat.addCheck(stage);
				break;
			case 2:
				stat.addCall(stage);
				break;
			case 3:
				stat.addRaise(move.getBet(), pot, stage);
				break;

			default:
		}
		
	}

	@Override
	protected void printStats(){
		for(Player p:players){
			System.out.println();
			System.out.println();
			System.out.println();
			String name = p.getName();
			System.out.println("Player: "+name);
			PlayerStat stat = stats.get(name);
			System.out.println("NumberOfPreFlopMoves: "+stat.getPreFlopMoves());
			System.out.println("PreFlopFolds: "+stat.getPreFlopFolds());
			System.out.println("PreFlopChecks: "+stat.getPreFlopChecks());
			System.out.println("PreFlopCalls: "+stat.getPreFlopCalls());
			System.out.println("PreFlopRaises: "+stat.getPreFlopRaises());
			System.out.println("BetPerPreFlopRaise: "+stat.getBetPerPreFlopRaise());
			System.out.println("PreFlopTightness: "+stat.getPreFlopTightness());
			System.out.println("PreFlopAggresionPercentage: "+stat.getPreFlopAggressionPercentage());
			System.out.println("PreFlopAggresion: "+stat.getPreFlopAggression());
			System.out.println();
			System.out.println("NumberOfMoves: "+stat.getMoves());
			System.out.println("Folds: "+stat.getFolds());
			System.out.println("Checks: "+stat.getChecks());
			System.out.println("Calls: "+stat.getCalls());
			System.out.println("Raises: "+stat.getRaises());
			System.out.println("BetPerRaise: "+stat.getBetPerRaise());
			System.out.println("PostFlopTightness: "+stat.getPostFlopTightness());
			System.out.println("PostFlopAggresionPercentage: "+stat.getPostFlopAggressionPercentage());
			System.out.println("PostFlopAggresion: "+stat.getPostFlopAggression());
			System.out.println();
			System.out.println("OverallTightness: "+stat.getOverallTightness());
			System.out.println("OverallAggression: "+stat.getOverallAggression());
		}
	}


	private class PlayerStat{
		private int moves;
		private int preFlopMoves;
		private int preFlopFolds;
		private int preFlopChecks;
		private int preFlopCalls;
		private int preFlopRaises;
		private double betPerPreFlopRaise; //relative to pot
		private int folds;
		private int checks;
		private int calls;
		private int raises;
		private double betPerPostFlopRaise;
		private double betPerRaise; //relativ to pot


		private PlayerStat(){
			moves = 0;
			preFlopMoves = 0;
			preFlopFolds = 0;
			preFlopChecks = 0;
			preFlopRaises = 0;
			betPerPreFlopRaise = 0;
			preFlopCalls = 0;
			folds = 0;
			checks = 0;
			calls = 0;
			raises = 0;
			betPerPostFlopRaise = 0;
			betPerRaise = 0;
		}

		private void addFold(int stage){
			if(stage == 0){
				preFlopMoves++;
				preFlopFolds++;
			}

			folds++;
			moves++;
		}

		private void addCheck(int stage){
			if(stage == 0){
				preFlopMoves++;
				preFlopChecks++;
			}

			checks++;
			moves++;
		}

		private void addCall(int stage){
			if(stage == 0){
				preFlopMoves++;
				preFlopCalls++;
			}

			calls++;
			moves++;
		}

		private void addRaise(int bet, int pot, int stage){
			double ratio = (double) bet / (double) pot;

			if(stage == 0){
				preFlopMoves++;
				if(preFlopRaises == 0){
					preFlopRaises++;
					betPerPreFlopRaise = ratio;
				}else{
					betPerPreFlopRaise = (betPerPreFlopRaise * preFlopRaises + ratio)/++preFlopRaises; 
				}
			}else{
				if(raises - preFlopRaises == 0){
					betPerPostFlopRaise = ratio;
				}else{
					betPerPostFlopRaise = (betPerPostFlopRaise * (raises - preFlopRaises) + ratio)/(raises - preFlopRaises + 1); 
				}
			}

			if(raises==0){
				raises++;
				betPerRaise = ratio;
			}else{
				betPerRaise = (betPerRaise * raises + ratio)/++raises; 
			}
			moves++;
		}

		private int getMoves(){
			return moves;
		}

		private int getPreFlopMoves(){
			return preFlopMoves;
		}

		private int getPreFlopFolds(){
			return preFlopFolds;
		}

		private int getPreFlopChecks(){
			return preFlopChecks;
		}

		private int getPreFlopCalls(){
			return preFlopCalls;
		}

		private int getPreFlopRaises(){
			return preFlopRaises;
		}

		private double getBetPerPreFlopRaise(){
			return betPerPreFlopRaise;
		}

		protected int getFolds(){
			return folds;
		}

		private int getChecks(){
			return checks;
		}

		private int getCalls(){
			return calls;
		}

		private int getRaises(){
			return raises;
		}

		private double getBetPerRaise(){
			return betPerRaise;
		}

		private double getPreFlopTightness(){
			//double foldPart = (double) preFlopFolds / (double) (preFlopMoves - preFlopChecks);
			//double callPart = (1 - (double) (preFlopCalls + preFlopRaises) / (double) preFlopMoves); 

			return Math.pow(0.1, (double)(preFlopCalls + preFlopRaises) / (double)(preFlopFolds + preFlopChecks + 1) );
		}

		private double getPostFlopTightness(){
			//double foldPart = (double) (folds - preFlopFolds) / (double) (moves - preFlopMoves - checks + preFlopChecks);
			//double callPart = (1 - (double) (calls - preFlopCalls + raises -preFlopRaises) / (double) (moves - preFlopMoves)); 

			return Math.pow(0.1, (double)(calls - preFlopCalls + raises - preFlopRaises) / (double)(folds - preFlopFolds + checks - preFlopChecks + 1) );
		}

		private double getOverallTightness(){
			return getPreFlopTightness();
		}

		private double getPreFlopAggressionPercentage(){
			return (double) preFlopRaises / (double) preFlopMoves;
		}

		private double getPostFlopAggressionPercentage(){
			return (double) (raises - preFlopRaises) / (double) (moves - preFlopMoves);
		}

		private double getPreFlopAggression(){
			/*double betPart;

			if(betPerPreFlopRaise < 1)
				betPart = betPerPreFlopRaise;
			else
				betPart = 1;
			*/
			double betPart = 1 - Math.pow(0.707106781, betPerPreFlopRaise);
			double raisePart = 1 - Math.pow((double) preFlopRaises / preFlopMoves -1, 2);
			return Math.max(betPart, raisePart); //3 * getPreFlopAggressionPercentage() / 4 + betPart / 4;
		}

		private double getPostFlopAggression(){
			/*double betPart;

			if(betPerPostFlopRaise < 1)
				betPart = betPerPostFlopRaise;
			else
				betPart = 1;
			*/
			double betPart = 1 - Math.pow(0.707106781, betPerPostFlopRaise);
			double raisePart = 1 - Math.pow((double) (raises - preFlopRaises) / (moves - preFlopMoves) -1, 2);
			return Math.max(betPart, raisePart);		//3 * getPostFlopAggressionPercentage() / 4 + betPart / 4;
		}

		private double getOverallAggression(){
			double betPart = 1 - Math.pow(0.707106781, betPerRaise);
			double raisePart = 1 - Math.pow((double) (raises) / (moves) -1, 2);
			return Math.max(betPart, raisePart);
		}
	}
}