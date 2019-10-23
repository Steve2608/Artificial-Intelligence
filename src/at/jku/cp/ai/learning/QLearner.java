package at.jku.cp.ai.learning;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.datastructures.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QLearner {

	private final Map<Pair<IBoard, Move>, Double> qmatrix;

	private final int numEpisodes;
	private final Random random;
	private final double discountFactor;
	private final boolean verbose;
	private final boolean bonus;

	public QLearner(final Random random, final int numEpisodes, final double discountFactor, final boolean verbose) {
		this(random, numEpisodes, discountFactor, verbose, false);
	}

	/**
	 * @param random         the random number generator to be used
	 * @param numEpisodes    the number of episodes for learning the model
	 * @param discountFactor this determines the importance of future rewards; in our test
	 *                       cases this plays a very minor role.
	 */
	public QLearner(final Random random, final int numEpisodes, final double discountFactor, final boolean verbose, final boolean bonus) {
		if (discountFactor <= 0d) throw new RuntimeException("discountFactor must be greater than 0.0!");

		this.random = random;
		this.numEpisodes = numEpisodes;
		this.discountFactor = discountFactor;
		this.verbose = verbose;
		qmatrix = new HashMap<>();
		this.bonus = bonus;
	}

	/**
	 * This is the method that has to learn the qmatrix, which will be used later on!
	 *
	 * @param board Kommentar, damit die IDE ned schreit
	 */
	public void learnQFunction(final IBoard board) {
		for (int e = 0; e < numEpisodes; e++) {
			final IBoard learnBoard = board.copy();

			if (bonus) learnBoard.getCurrentUnicorn().pos = randomStartPosition(learnBoard);

			while (!hasWon(learnBoard) && unicornAlive(learnBoard)) {
				final List<Move> moves = learnBoard.getPossibleMoves();

				final Move move = moves.get(random.nextInt(moves.size()));
				final IBoard prevBoard = learnBoard.copy();
				learnBoard.executeMove(move);

				updateQMatrix(prevBoard, learnBoard, move);
			}
		}
	}

	/*
	Bit hacky, since we cannot guarantee that the unicorn is set in a position, from where it can reach the goal
	########
	#####.##
	########
	##U...G#
	########
	For that to work we would need something like a depth limiting predicate, that kills the search after like 1000 steps
	 */
	private V randomStartPosition(final IBoard board) {
		V position;
		do {
			position = new V(random.nextInt(board.getWidth()), random.nextInt(board.getHeight()));
		} while (!board.isPassable(position));
		return position;
	}

	/**
	 * this method uses the (learned) qmatrix to determine best move, given the current
	 * situation!
	 *
	 * @param board Kommentar, damit die IDE ned schreit
	 * @return Kommentar, damit die IDE ned schreit
	 */
	public Move getMove(final IBoard board) {
		final List<Move> moves = board.getPossibleMoves();

		double bestScore = Double.NEGATIVE_INFINITY;
		Move bestMove = Move.STAY;

		for (final Move move : moves) {
			final double score = qmatrix.getOrDefault(new Pair<>(board, move), 0d);
			if (score > bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}

		return bestMove;
	}

	private void updateQMatrix(final IBoard prevBoard, final IBoard learnBoard, final Move move) {
		if (unicornAlive(learnBoard))
			qmatrix.put(new Pair<>(prevBoard, move), getReward(learnBoard) + discountFactor * getMaxScore(learnBoard));
	}

	private boolean unicornAlive(final IBoard board) {
		return board.getUnicorns().size() == 1;
	}

	private boolean hasWon(final IBoard board) {
		// Since there is just one unicorn, the winning ID should be 0
		return board.getEndCondition().getWinner() == 0;
	}

	private double getReward(final IBoard board) {
		return unicornAlive(board) && board.getClouds().isEmpty() ? 100d : 0d;
	}

	private double getMaxScore(final IBoard board) {
		return board.getPossibleMoves().stream().mapToDouble(move -> qmatrix.getOrDefault(new Pair<>(board, move), 0d)).max().orElse(0d);
	}

}