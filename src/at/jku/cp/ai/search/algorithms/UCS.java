package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class UCS implements Search {

	private Function<Node, Double> cost;

	public UCS(final Function<Node, Double> cost) {
		this.cost = cost;
	}

	@Override
	public Node search(final Node start, final Predicate<Node> endPredicate) {
		if (endPredicate.test(start)) return start;

		final StablePriorityQueue<Double, Node> fringe = new StablePriorityQueue<>();
		fringe.add(new Pair<>(cost.apply(start), start));
		final Set<Node> closed = new HashSet<>();

		Pair<Double, Node> curr;
		do {
			curr = fringe.poll();
			closed.add(curr.s);
			for (final Node n : curr.s.adjacent()) {
				if (!closed.contains(n)) {
					if (endPredicate.test(n)) return n;
					fringe.offer(new Pair<>(curr.f + cost.apply(n), n));
					closed.add(n);
				}
			}
		} while (!fringe.isEmpty());
		return null;
	}

}