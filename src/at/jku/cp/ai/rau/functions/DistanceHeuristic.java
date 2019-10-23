package at.jku.cp.ai.rau.functions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

import java.util.function.Function;

public class DistanceHeuristic implements Function<Node, Double> {

	private java.util.function.Function<IBoard, V> start;
	private java.util.function.Function<IBoard, V> goal;
	private java.util.function.BiFunction<V, V, Double> d;

	public DistanceHeuristic(
			final java.util.function.Function<IBoard, V> start,
			final java.util.function.Function<IBoard, V> goal,
			final java.util.function.BiFunction<V, V, Double> distanceMeasure) {
		this.start = start;
		this.goal = goal;
		d = distanceMeasure;
	}

	@Override
	public Double apply(final Node node) {
		final IBoard board = node.getState();
		if (board.isRunning()) {
			final V av = start.apply(board);
			final V bv = goal.apply(board);
			return d.apply(av, bv);
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}
}
