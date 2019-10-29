package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class ASTAR implements Search {

	private Function<Node, Double> heuristic;

	private Function<Node, Double> cost;

	public ASTAR(final Function<Node, Double> heuristic, final Function<Node, Double> cost) {
		this.heuristic = heuristic;
		this.cost = cost;
	}

	@Override
	public Node search(final Node start, final Predicate<Node> endPredicate) {
		final StablePriorityQueue<Costs, Node> fringe = new StablePriorityQueue<>();
		fringe.add(new Pair<>(new Costs(heuristic.apply(start), cost.apply(start)), start));
		final Set<Node> closed = new HashSet<>();

		do {
			final Pair<Costs, Node> curr = fringe.poll();
			if (endPredicate.test(curr.s)) return curr.s;
			closed.add(curr.s);

			for (final Node n : curr.s.adjacent()) {
				if (!closed.contains(n)) {
					fringe.add(new Pair<>(new Costs(heuristic.apply(n), curr.f.cost + cost.apply(n)), n));
				}
			}
		} while (!fringe.isEmpty());
		return null;
	}

	private static class Costs implements Comparable<Costs> {
		private final double heuristic, cost;

		private Costs(final double heuristic, final double cost) {
			this.heuristic = heuristic;
			this.cost = cost;
		}

		@Override
		public int compareTo(final Costs other) {
			return Double.compare(heuristic + cost, other.heuristic + other.cost);
		}
	}

}
