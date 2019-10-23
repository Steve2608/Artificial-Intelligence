package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class MinMaxSearch implements AdversarialSearch {

	private final BiPredicate<Integer, Node> searchLimitingPredicate;
	private Function<Node, Double> boardEvaluationFunction;

	/**
	 * To limit the extent of the search, this implementation should honor a
	 * limiting predicate. The predicate returns 'true' as long as we are below
	 * the limit, and 'false', if we exceed the limit.
	 *
	 * @param searchLimitingPredicate
	 */
	public MinMaxSearch(final BiPredicate<Integer, Node> searchLimitingPredicate) {
		this.searchLimitingPredicate = searchLimitingPredicate;
	}

	public Pair<Node, Double> search(final Node start, final Function<Node, Double> evalFunction) {
		boardEvaluationFunction = evalFunction;

		Node result = start;
		double best = Double.NEGATIVE_INFINITY;
		for (final Node n : start.adjacent()) {
			final double val = min(n, 1);
			if (val > best) {
				best = val;
				result = n;
			}
		}
		return new Pair<>(result, best);
	}

	private double min(final Node current, final int depth) {
		if (!searchLimitingPredicate.test(depth, current) || current.isLeaf())
			return boardEvaluationFunction.apply(current);

		double val = Double.NEGATIVE_INFINITY;
		for (final Node n : current.adjacent()) {
			val = Math.max(val, max(n, depth + 1));
		}
		return val;
	}

	private double max(final Node current, final int depth) {
		if (!searchLimitingPredicate.test(depth, current) || current.isLeaf())
			return boardEvaluationFunction.apply(current);

		double val = Double.POSITIVE_INFINITY;
		for (final Node n : current.adjacent()) {
			val = Math.min(val, min(n, depth + 1));
		}
		return val;
	}

}