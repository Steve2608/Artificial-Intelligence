package at.jku.cp.ai.rau.endconditions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Unicorn;

import java.util.List;

public interface EndCondition {
	/**
	 * given a board, and the outcome of the actions in the current tick,
	 * determine whether the game has ended
	 *
	 * @param board
	 * @param evaporated
	 * @param sailing
	 * @return the state of the game
	 */
	boolean hasEnded(IBoard board, List<Cloud> evaporated, List<Unicorn> sailing);

	/**
	 * @return -1 if draw, id of winning unicorn otherwise
	 */
	int getWinner();

	boolean equals(Object obj);

	int hashCode();

	EndCondition copy();

	String getOutcome();
}
