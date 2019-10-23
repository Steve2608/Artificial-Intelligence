package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class AlphaBetaSearch implements AdversarialSearch {

	private final BiPredicate<Integer, Node> searchLimitingPredicate;
	private Function<Node, Double> boardEvaluationFunction;

	/**
	 * To limit the extent of the search, this implementation should honor a
	 * limiting predicate. The predicate returns 'true' as long as we are below the limit,
	 * and 'false', if we exceed the limit.
	 *
	 * @param searchLimitingPredicate
	 */
	public AlphaBetaSearch(final BiPredicate<Integer, Node> searchLimitingPredicate) {
		this.searchLimitingPredicate = searchLimitingPredicate;
	}

	public Pair<Node, Double> search(final Node start, final Function<Node, Double> evalFunction) {
		boardEvaluationFunction = evalFunction;

		Node result = start;
		double best = Double.NEGATIVE_INFINITY;
		for (final Node n : start.adjacent()) {
			final double val = min(n, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			if (val > best) {
				best = val;
				result = n;
			}
		}
		return new Pair<>(result, best);
	}

	private double max(final Node current, final int depth, final double alpha, final double beta) {
		if (!searchLimitingPredicate.test(depth, current) || current.isLeaf())
			return boardEvaluationFunction.apply(current);

		double best = alpha, val;
		for (final Node n : current.adjacent()) {
			val = min(n, depth + 1, best, beta);
			if (val > best) {
				best = val;
				if (best >= beta) break;
			}
		}
		return best;
	}

	private double min(final Node current, final int depth, final double alpha, final double beta) {
		if (!searchLimitingPredicate.test(depth, current) || current.isLeaf())
			return boardEvaluationFunction.apply(current);

		double best = beta, val;
		for (final Node n : current.adjacent()) {
			val = max(n, depth + 1, alpha, best);
			if (val < best) {
				best = val;
				if (best <= alpha) break;
			}
		}
		return best;
	}

}
