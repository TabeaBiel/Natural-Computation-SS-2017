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
		System.out.println(players.size());
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
				if(stage == 0)
					stat.addPreFlopFold();
				else
					stat.addFold();
				break;
			case 1:
				stat.addCheck();
				break;
			case 2:
				stat.addCall(move.getBet());
				break;
			case 3:
				if(stat == null) System.out.println("we found null");
				stat.addRaise(move.getBet());
				break;

			default:
		}
		
	}


	protected class PlayerStat{
		private int preFlopFolds;
		private int folds;
		private int checks;
		private int calls;
		private double betPerCall;
		private int raises;
		private double betPerRaise;

		protected PlayerStat(){
			preFlopFolds = 0;
			folds = 0;
			checks = 0;
			calls = 0;
			betPerCall = 0;
			raises = 0;
			betPerRaise = 0;
		}

		protected void addPreFlopFold(){
			preFlopFolds++;
			folds++;
		}

		private void addFold(){
			folds++;
		}

		protected void addCheck(){
			checks++;
		}

		protected void addCall(int bet){
			if(calls==0){
				calls++;
				betPerCall = bet;
			}else{
				betPerCall = (betPerCall * calls + bet)/++calls; 
			}
		}

		protected void addRaise(int bet){
			if(raises==0){
				raises++;
				betPerRaise = bet;
			}else{
				betPerRaise = (betPerRaise * raises + bet)/++raises; 
			}
		}

		protected int getPreFlopFolds(){
			return preFlopFolds;
		}

		protected int getFolds(){
			return folds;
		}

		protected int getChecks(){
			return checks;
		}

		protected int getCalls(){
			return calls;
		}

		protected double getBetPerCall(){
			return betPerCall;
		}

		protected int getRaises(){
			return raises;
		}

		protected double getBetPerRaise(){
			return betPerRaise;
		}
	}
}