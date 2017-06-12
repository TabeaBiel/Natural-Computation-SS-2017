package gpoker.players;

import boone.NetFactory;
import boone.NeuralNet;
import boone.PatternSet;
import boone.map.Function;
import boone.training.BackpropTrainer;
import gpoker.*;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Arrays;

public class TDPlayer extends Player {

	// ------------- Globals needed for Boone ----------------

	private NeuralNet net;
	private double[] pattern;

	private BackpropTrainer trainer;
	private PatternSet set;
	private ArrayList<Double> input;
	private ArrayList<Double> target; // output value

	// remember input values of last stage for TD
	private double[] lastInput = new double[7];
	// remember output/reward of current stage
	private double Q1;
	public double discount = 1.0;
	private double epsilon = 0.1;
	// indicates the ID of neuron whose input was set to 1
	// while testing which move should be made
	private int moveNeuron;

	// ------------- Globals needed for GPoker ----------------

	// indicates how many moves have been made since the round began
	private int moveCount = 0;
	private double betSize = 0;
	private int RAISE = 0;
	private int CALL = 1;
	private int CHECK = 2;

	/**
	 * Creates the player via XML.
	 *
	 * @param element
	 *            an XML player element
	 */
	public TDPlayer(Element element) {
		super(element);
		constructorHelper();
	}

	/**
	 * Constructs the player.
	 *
	 * @param name
	 *            the player's name
	 * @param chips
	 *            the amount of chips the player has.
	 * @param netInput
	 *            size of the Input for the NN
	 */
	public TDPlayer(String name, int chips, int netInput) {
		super(name, chips);
		constructorHelper();
	}

	/**
	 * Code which should be executed in both constructors
	 */
	private void constructorHelper() {
		pattern = new double[7];
		net = NetFactory.createFeedForward(new int[]{7, 7, 7, 1}, false, null, new BackpropTrainer(), null, null);
		net.getOutputNeuron(0).setActivationFn(new Function.Identity());
		net.getOutputNeuron(0).setUsingBias(false);

		trainer = (BackpropTrainer) net.getTrainer();
		trainer.setLearnRate(0.2);
		set = new PatternSet();
		input = new ArrayList<Double>(7);
		target = new ArrayList<Double>(1); // output value

		set.getInputs().add(input); // add input pattern
		set.getTargets().add(target); // add target value
		trainer.setTrainingData(set);
		trainer.setEpochs(1);
		trainer.setStepMode(true);
	}

	/**
	 * The TDlayers moves cannot be trusted.
	 *
	 * @return false.
	 */
	public boolean isTrusted() {
		return false;
	}

	/**
	 * Called by Dealer to request the next move of the TDPlayer. Deciding on
	 * move, setting input and Q1 for learning and learning by predictions
	 * happens here.
	 * 
	 * @return move which shall be played by TDPlayer
	 */
	public Move act() {

		Dealer dealer = Director.getInstance().getDealer();
		int totalChips = dealer.getTotalChips();
		double potPercentageOfMax = (double)dealer.getPot() / (double)totalChips;
		double chipPercentageOfMax = (double)getChips() / (double)totalChips;
		double handValue = getHandValue();
		double output;

		//reset
		betSize = 0;
		
        output = decideOnMove(potPercentageOfMax, chipPercentageOfMax, handValue);
              
		moveCount++;
		Move move = getMove();

//		System.out.println("Hand = " + Utilities.fixedPoint.format(handValue));
//		System.out.print("Predict: " + Utilities.fixedPoint.format(output) + " -> " + move);
//		if (move.getType() == Move.RAISE)
//			System.out.println("(" + betSize + ")");
//		else
//			System.out.println();

		Q1 = output;
		if (moveCount > 1 && !Double.isNaN(Q1))		// do not learn first and random moves
			learn();
		setLastInput(potPercentageOfMax, chipPercentageOfMax, handValue, moveNeuron);

		return move;
	}

	/**
	 * Learning/Training the ANN by setting input and target of the PatternSet
	 * used by the trainer.
	 */
	private void learn() {
//		System.out.println("-------------- learn ----------------");
//		System.out.println("Q1: " + Q1);
//		System.out.println("Last input: " + Arrays.toString(lastInput));
		target.clear();
		input.clear();

		for (Double i : lastInput)
			input.add(i);

		target.add(discount * Q1);
		trainer.train();

//		System.out.println("------------------------------");
	}

	/**
	 * Fills lastInput array with values of current input.
	 * 
	 * @param h
	 *            input for Neuron 4
	 * @param p
	 *            input for Neuron 5
	 * @param c
	 *            input for Neuron 6
	 * @param m
	 *            number of Neuron that should be set to 1 (indicates which move
	 *            was part of input)
	 */
	private void setLastInput(double h, double p, double c, int m) {
		Arrays.fill(lastInput, 0);
		lastInput[4] = h;
		lastInput[5] = p;
		lastInput[6] = c;
		if (m == RAISE) {
			lastInput[m] = betSize;
		} else 
			lastInput[m] = 1;
	}
        

	@Override
	/**
	 * Override setWins of superclass to include training the ANN
	 * after a round is over/chips have been won/lost.
	 */
	public void addWin(int win) {
		//System.out.println("TD Player addWin(): " +win);
        super.addWin(win);
                
        //Calculates binary reward
//        if(win >= 0) {
//        	Q1 = 1;
//        } else {
//        	Q1 = 0;
//        }
//
        // non binary reward
		Q1 = (double)win / (double)Director.getInstance().getDealer().getTotalChips();
                
		learn();
		moveCount = 0;

//		System.out.println("Total wins: " + getWins());
//		System.out.println("---------------------------- Game Over -----------------------------");
	}

	/**
	 * Tries given input parameters for the ANN with different settings of
	 * "Move"-Neurons. In each round, one of these Neurons is set to 1, the
	 * others to 0. The configuration with the highest output value indicates
	 * which move should be played (stored in moveNeuron)
	 * 
	 * @param a
	 *            input for Neuron 4
	 * @param b
	 *            input for Neuron 5
	 * @param c
	 *            input for Neuron 6
	 * 
	 * @return highest output value
	 */
	private double decideOnMove(double a, double b, double c) {

		double output;
		double maxOutput = -Double.MAX_VALUE;

		// choose a random move instead of choosing
		// the one with the highest output
		if (Math.random() < epsilon) {
			moveNeuron = (int)(Math.random() * 4.0);
			if (moveNeuron == RAISE)
				betSize = ((int)(Math.random() * 4.0) + 1) * 0.25;		// not too pretty
			return Double.NaN;											// indicate random move
		}
		// test every possible move
		for (int i = 0; i < 4; i++) {
			// test different betSizes for raise
			if (i == RAISE) {
				moveNeuron = RAISE;
				for (double j = 0.25; j <= 1; j = j + 0.25) {
					fillPattern(i, j, a, b, c);
					output = evaluateInput(pattern);
					if (maxOutput < output) {
						maxOutput = output;
						betSize = j;
					}
				}
			} else {
				fillPattern(i, 1, a, b, c);
				output = evaluateInput(pattern);
				if (maxOutput < output) {
					maxOutput = output;
					moveNeuron = i;
					betSize = 0;
				}
			}
		}
		return maxOutput;
	}

	private void fillPattern(int i, double j, double a, double b, double c) {
		Arrays.fill(pattern, 0);
		pattern[i] = j;
		pattern[4] = a;
		pattern[5] = b;
		pattern[6] = c; // only one move is selected (1), others are 0
	}
	
	/**
	 * Checks which Poker move corresponds to the Neuron ID stored in
	 * moveNeuron.
	 * 
	 * @return GPoker Move that shall be played
	 */
	private Move getMove() {
		Move moveAct = new Move(Move.FOLD);

		if (moveNeuron == RAISE) {
			moveAct.setType(Move.RAISE);
			moveAct.setBet(getBet());
		} else if (moveNeuron == CALL) {
			moveAct.setType(Move.CALL);
		} else if (moveNeuron == CHECK) {
			moveAct.setType(Move.CHECK);
		}
		return moveAct;
	}

	/**
	 * Returns the amount of chips the TDPlayer bets when raising. The amount
	 * the TDPlayer raises is determined by the value which is calculated
	 * from the players hole cards and what kind of hand he has.
	 *
	 * @return The amount of chips when raising.
	 * @author: Helmut A. Mayer
	 */
	private int getBet() {

		return (int)(Director.getInstance().getDealer().getPot() * betSize);		// standard raise
	}


	/**
	 * Generates ANN output for a given input pattern
	 * 
	 * @param pattern
	 *            input pattern that is used to generate output
	 * @return output value of ANN
	 */
	private double evaluateInput(double[] pattern) {
		net.setInput(pattern);
		net.innervate();
		return net.getOutputNeuron(0).getOutput();
	}

	/**
	 * Associates each handvalue of GPoker with a double number.
	 * 
	 * @return number that is associated with current handvalue
	 */
	private double getHandValue() {

		int handValue = Director.getInstance().getDealer().evaluate(getHand());
		return (double)handValue / (double)Hand.STRAIGHT_FLUSH;
	}

}
