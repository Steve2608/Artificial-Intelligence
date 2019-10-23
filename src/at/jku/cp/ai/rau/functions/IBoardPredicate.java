package at.jku.cp.ai.rau.functions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.search.Node;

import java.util.function.Predicate;

public class IBoardPredicate implements Predicate<Node> {
	private Predicate<IBoard> p;

	public IBoardPredicate(final Predicate<IBoard> p) {
		this.p = p;
	}

	@Override
	public boolean test(final Node node) {
		final IBoard board = node.getState();
		return p.test(board);
	}
}
