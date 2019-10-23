package at.jku.cp.ai.rau.functions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.search.Node;

import java.util.function.Function;

public class IBoardFunction implements Function<Node, Double> {
	private Function<IBoard, Double> f;

	public IBoardFunction(final Function<IBoard, Double> f) {
		this.f = f;
	}

	@Override
	public Double apply(final Node node) {
		final IBoard board = node.getState();
		return f.apply(board);
	}
}
