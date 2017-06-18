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

		if(stage == 0){
			stat.addPreFlopMove();
		}

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
				stat.addRaise(move.getBet());
				break;

			default:
		}
		
	}

	@Override
	protected void printStats(){
		for(Player p:players){
			System.out.println();
			String name = p.getName();
			System.out.println("Player: "+name);
			PlayerStat stat = stats.get(name);
			System.out.println("NumberOfMoves: "+stat.getMoves());
			System.out.println("NumberOfPreFlopMoves: "+stat.getPreFlopMoves());
			System.out.println("PreFlopFolds: "+stat.getPreFlopFolds());
			System.out.println("Folds: "+stat.getFolds());
			System.out.println("Checks: "+stat.getChecks());
			System.out.println("Calls: "+stat.getCalls());
			System.out.println("BetPerCall: "+stat.getBetPerCall());
			System.out.println("Raises: "+stat.getRaises());
			System.out.println("BetPerRaise: "+stat.getBetPerRaise());
		}
	}


	private class PlayerStat{
		private int moves;
		private int preFlopMoves;
		private int preFlopFolds;
		private int folds;
		private int checks;
		private int calls;
		private double betPerCall;
		private int raises;
		private double betPerRaise;

		private PlayerStat(){
			moves = 0;
			preFlopMoves = 0;
			preFlopFolds = 0;
			folds = 0;
			checks = 0;
			calls = 0;
			betPerCall = 0;
			raises = 0;
			betPerRaise = 0;
		}

		private void addPreFlopFold(){
			preFlopFolds++;
			folds++;
			moves++;
		}

		private void addPreFlopMove(){
			preFlopMoves++;
		}

		private void addFold(){
			folds++;
			moves++;
		}

		private void addCheck(){
			checks++;
			moves++;
		}

		private void addCall(int bet){
			if(calls==0){
				calls++;
				betPerCall = bet;
			}else{
				betPerCall = (betPerCall * calls + bet)/++calls; 
			}
			moves++;
		}

		private void addRaise(int bet){
			if(raises==0){
				raises++;
				betPerRaise = bet;
			}else{
				betPerRaise = (betPerRaise * raises + bet)/++raises; 
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

		protected int getFolds(){
			return folds;
		}

		private int getChecks(){
			return checks;
		}

		private int getCalls(){
			return calls;
		}

		private double getBetPerCall(){
			return betPerCall;
		}

		private int getRaises(){
			return raises;
		}

		private double getBetPerRaise(){
			return betPerRaise;
		}
	}
}