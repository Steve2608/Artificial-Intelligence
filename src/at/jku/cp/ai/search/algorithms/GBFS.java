package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class GBFS implements Search {

	private Function<Node, Double> heuristic;

	public GBFS(final Function<Node, Double> heuristic) {
		this.heuristic = heuristic;
	}

	@Override
	public Node search(final Node start, final Predicate<Node> endPredicate) {
		final StablePriorityQueue<Double, Node> fringe = new StablePriorityQueue<>();
		fringe.add(new Pair<>(heuristic.apply(start), start));
		final Set<Node> closed = new HashSet<>();
		closed.add(start);

		Node current;
		do {
			current = fringe.poll().s;
			if (endPredicate.test(current)) return current;

			for (final Node n : current.adjacent()) {
				if (!closed.contains(n)) {
					fringe.add(new Pair<>(heuristic.apply(n), n));
					closed.add(n);
				}
			}
		} while (!fringe.isEmpty());
		return null;
	}

}