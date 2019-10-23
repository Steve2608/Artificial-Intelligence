package at.jku.cp.ai.rau.endconditions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Unicorn;

import java.util.List;

final public class NoEnd implements EndCondition {
	@Override
	public boolean hasEnded(final IBoard board, final List<Cloud> evaporated, final List<Unicorn> sailing) {
		return false;
	}

	@Override
	public int getWinner() {
		return -1;
	}

	@Override
	public EndCondition copy() {
		return this;
	}

	@Override
	public String getOutcome() {
		return "NOEND";
	}
}
