package gpoker.players;

import boone.NetFactory;
import boone.NeuralNet;
import boone.PatternSet;
import boone.Trainer;
import boone.map.Function;
import boone.training.RpropTrainer;
import boone.util.Conversion;
import gpoker.*;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * The ParametricPlayer is a modified CallPlayer with the goal to train
 * an Artificial Neural Network which observes a specific poker player in a
 * heads-up poker game and learns to predict its decisions.
 *
 *
 * Note:
 * There are some Conditions so that the ParametricPlayer works:
 * 1. The opponent must have the name Darwin
 * 2. The opponent must call the method addMove(Move move) in the method act()
 *
 * @author Michael Ferdinand Moser
 * @author Sarah Sophie Sallinger
 * @author Dominik Söllinger
 * @author Andreas René Krug
 */
public class ParametricPlayer extends Player {

    /** The Artificial Neural Network which observes a specific poker player */
    NeuralNet neuralNet;

    /** A List of all previous rounds */
    List<MoveRecord> playHistory;

    /** Number of the last previous rounds which the ANN should train  */
    int nrRecordsToConsider = 1;

    /** The last predicted move, is needed to compare with the current move of the opponent*/
    Move guessedMovePrevious;


    int correctPredictions, totalPredictions = 0;
    int[][] matchMatrix = new int[4][4];

    /**
     * Creates the player via XML.
     *
     * @param element an XML player element
     */
    public ParametricPlayer(Element element) {
        super(element);
        initNetwork();
        initPlayHistory();
    }

    /**
     * Constructs the player.
     *
     * @param name  the player's name
     * @param chips the amount of chips the player has.
     */
    public ParametricPlayer(String name, int chips) {
        super(name, chips);
        initNetwork();
        initPlayHistory();
    }

    /**
     * Initializes the Artificial Neural Network
    */
    private void initNetwork() {
        int[] netDesign = {7, 2, 4};
        neuralNet = NetFactory.createFeedForward(netDesign, true, new Function.Sigmoid(), new RpropTrainer(), null, null);
    }

    /**
     * Initializes the list to save previous Rounds
     */
    private void initPlayHistory() {
        playHistory = new ArrayList<>();
    }

    /**
     * The moves cannot be trusted, as CALL is not always correct.
     *
     * @return false.
     */
    public boolean isTrusted() {
        return false;
    }

    /**
     * Calculates the total number of a specific move
     *
     * @param move the specific move (FOLD, CHECK, CALL, RAISE)
     * @return the total number of a move
     */
    private int totalMovesActual(Move move) {
        int total = 0;

        for (int i = 0; i < matchMatrix.length; i++) {
            total = total + matchMatrix[i][move.getType()];
        }

        return total;
    }

    /**
     * Calculates the prediction value for all correct predictions for a specific move
     *
     * @param move the specific move (FOLD, CHECK, CALL, RAISE)
     * @return the prediction value
     */
    private double getPredictionValueForMove(Move move) {
        if (totalMovesActual(move) == 0) {
            return 0.0;
        }

        return 100 * matchMatrix[move.getType()][move.getType()] / (double) totalMovesActual(move);
    }
    /**
     * Returns always CALL, trains the ANN, and checks whether the last prediction was right 
     *
     * @return the CALL move
     */
    public Move act() {
        Dealer dealer = Director.getInstance().getDealer();
        Player player = getPlayerWithName("Darwin");

        // do nothing in Darwin's first move
        if (player != null && player.getMoves().size() > 0) {
            // note that we do not know the real conditions the player will see,
            // only the current conditions, makes predictions harder :-/
            List<Card> communityCards = dealer.getCommunity();
            int potSize = dealer.getPot();
            int currentBettingStage = dealer.getStage();
            int chipSize = player.getChips();
            int stake = player.getStageBets();
            List<Move> moves = player.getMoves();

            Move lastMove = moves.get(moves.size() - 1);
            MoveRecord moveRecord = new MoveRecord(lastMove, communityCards, stake, chipSize, currentBettingStage, potSize);
            playHistory.add(moveRecord);

            trainNet();

            // was the last prediction right?
            if (guessedMovePrevious != null) {
                totalPredictions++;

                if (lastMove.getType() == guessedMovePrevious.getType()) {
                    correctPredictions++;
                }

                matchMatrix[lastMove.getType()][guessedMovePrevious.getType()]++;
                displayMatrix();
            }

            guessedMovePrevious = guessNextMove(moveRecord);
        }

        Move nextMove = new Move(Move.CALL);
        addMove(nextMove);

        return nextMove;
    }

    /**
     * This Method searches for a player with a given name 	
     *
     * @param name of the requested person
     * @return a player, or null 
     */
    private Player getPlayerWithName(String name) {
        for (Player player : Director.getInstance().getDealer().getPlayers()) {
            if (player.getName().equals(name))
                return player;
        }
        return null;
    }

    /**
     * Displays the matchMatrix with all predictions, or the number of total Predictions, the number of correct predictions in percent
     * and the number of correct predictions in percent for every move. (FOLD, CHECK, CALL, RAISE). 
     */
    private void displayMatrix() {
		/*
        System.out.println("\n\n\t\tFOLD\tCHECK\tCALL\tRAISE" +
         "     ------ CORRECT PREDICTIONS: (" + correctPredictions + "/" +
        totalPredictions + ") ;" + " Percentage: "
         + (100 * ((double) correctPredictions / (double) totalPredictions)) +
        "%");
        
        for (int i = 0; i < 4; i++) {
			switch (i) {
				case 0:
				System.out.print("FOLD\t");
				break;
				case 1:
				System.out.print("CHECK\t");
				break;
				case 2:
				System.out.print("CALL\t");
				break;
				case 3:
				System.out.print("RAISE\t");
				break;
        }
        
        for (int j = 0; j < 4; j++) {
			System.out.print(matchMatrix[i][j] + "\t" + (matchMatrix[i][j] > 1000
			? "" : "\t"));
        }
        
			System.out.println();
        }
		*/	
        System.out.format("%d|%f|%f|%f|%f|%f\n", totalPredictions, 100 * ((double) correctPredictions / (double) totalPredictions), getPredictionValueForMove(new Move(Move.FOLD)),
                getPredictionValueForMove(new Move(Move.CHECK)), getPredictionValueForMove(new Move(Move.CALL)), getPredictionValueForMove(new Move(Move.RAISE)));
    }

    /**
     * Calculates a value from 0 to 1 for the community cards, similar to the pattern player
     *
     * @param communityCards the given community cards
     * @return a value from 0 to 1
     */
    private double mapCommunityCards(List<Card> communityCards) {
        double communityValue = 0;

        for (Card card : communityCards) {
            communityValue += card.getRank() / (double) Card.ACE;
        }

        return communityValue / (communityCards.size() == 0 ? 1 : communityCards.size());
    }

    /**
     * Method to train the ANN. It trains with the nrRecordsToConsider last rounds. 
	 * For example the values(stake, betting stage, community cards ...) of round x with the move (FOLD, CHECK, CALL, RAISE) of round x + 1 as result.
     */
    private void trainNet() {
        double[][] inPatterns = new double[playHistory.size()][7];
        double[][] outPatterns = new double[playHistory.size()][4];

        for (int i = Math.max(1, playHistory.size() - nrRecordsToConsider); i < playHistory.size(); i++) {
            MoveRecord moveRecord = playHistory.get(i - 1);

            int nextMove = playHistory.get(i).getMove().getType();
            int currentMove = moveRecord.getMove().getType();
            int stake = moveRecord.getStake();
            int bettingStage = moveRecord.getBettingStage();
            double communityValue = mapCommunityCards(moveRecord.getCommunityCards());

            double[] input = {0, 0, 0, 0, communityValue, stake / (double) (moveRecord.getChipsSize() + stake), // norm by total number of chips the player had 
								bettingStage / (double) Dealer.RIVER};
            double[] output = {0, 0, 0, 0};

            switch (currentMove) {
                case Move.FOLD:
                    input[0] = 1;
                    break;
                case Move.CHECK:
                    input[1] = 1;
                    break;
                case Move.CALL:
                    input[2] = 1;
                    break;
                case Move.RAISE:
                    input[3] = 1;
                    break;
            }

            switch (nextMove) {
                case Move.FOLD:
                    output[0] = 1;
                    break;
                case Move.CHECK:
                    output[1] = 1;
                    break;
                case Move.CALL:
                    output[2] = 1;
                    break;
                case Move.RAISE:
                    output[3] = 1;
                    break;
            }

            inPatterns[i] = input;
            outPatterns[i] = output;
        }

        PatternSet patternSet = new PatternSet();

        for (int i = Math.max(1, playHistory.size() - nrRecordsToConsider); i < playHistory.size(); i++) {
            patternSet.getInputs().add(Conversion.asList(inPatterns[i]));
            patternSet.getTargets().add(Conversion.asList(outPatterns[i]));
        }

        Trainer trainer = neuralNet.getTrainer();
        trainer.setTestData(patternSet);
        trainer.setTrainingData(patternSet);
        trainer.setEpochs(10);
        trainer.train();
    }

    /**
     * The ANN tries to predict the next move with the values of the current move of the opponent.
     *
     * @param m a record with all important values of the last round
     * @return the predicted move
     */
    private Move guessNextMove(MoveRecord m) {
        double[] input = {0, 0, 0, 0, mapCommunityCards(m.getCommunityCards()), m.getStake() / (double) (getPlayerWithName("Darwin").getChips() + m.getStake()),
                m.getBettingStage() / (double) Dealer.RIVER};

        double[] output = new double[4];

        switch (m.getMove().getType()) {
            case Move.FOLD:
                input[0] = 1;
                break;
            case Move.CHECK:
                input[1] = 1;
                break;
            case Move.CALL:
                input[2] = 1;
                break;
            case Move.RAISE:
                input[3] = 1;
                break;
        }

        neuralNet.setInput(input);
        neuralNet.innervate();

        neuralNet.getOutput(output);

        Move move = new Move(Move.FOLD);

        double max = output[0];
        for (int i = 1; i < 4; i++) {
            if (output[i] > max) {
                max = output[i];
                move.setType(i);
            }
        }

        return move;
    }

    /**
     * This class represents a record for one round. There are stored the pot siz,
     * chip size, stake, betting stage, community cards, and the last move of
     * the opponent.
     */
    private class MoveRecord {
        Move move;

        int potSize;
        int chipsSize;
        int stake;
        int bettingStage;
        List<Card> communityCards;
        boolean ledToWin;

        public MoveRecord(Move move, List<Card> communityCards, int stake, int chipsSize, int bettingStage, int potSize) {
            this.move = move;
            this.communityCards = communityCards;
            this.stake = stake;
            this.chipsSize = chipsSize;
            this.bettingStage = bettingStage;
            this.potSize = potSize;
        }

        public Move getMove() {
            return move;
        }

        public void setMove(Move move) {
            this.move = move;
        }

        public int getPotSize() {
            return potSize;
        }

        public void setPotSize(int potSize) {
            this.potSize = potSize;
        }

        public int getStake() {
            return stake;
        }

        public void setStake(int stake) {
            this.stake = stake;
        }

        public List<Card> getCommunityCards() {
            return communityCards;
        }

        public void setCommunityCards(List<Card> communityCards) {
            this.communityCards = communityCards;
        }

        public int getChipsSize() {
            return chipsSize;
        }

        public void setChipsSize(int chipsSize) {
            this.chipsSize = chipsSize;
        }

        public int getBettingStage() {
            return bettingStage;
        }

        public void setBettingStage(int bettingStage) {
            this.bettingStage = bettingStage;
        }

        public boolean getLedToWin() {
            return ledToWin;
        }

        public void setLedToWin(boolean ledToWin) {
            this.ledToWin = ledToWin;
        }
    }
}
