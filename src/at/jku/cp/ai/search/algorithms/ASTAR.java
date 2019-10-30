package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class ASTAR implements Search {

	private Function<Node, Double> f_heuristic, f_cost;

	public ASTAR(final Function<Node, Double> f_heuristic, final Function<Node, Double> f_cost) {
		this.f_heuristic = f_heuristic;
		this.f_cost = f_cost;
	}

	@Override
	public Node search(final Node start, final Predicate<Node> endPredicate) {
		final Queue<Pair<Costs, Node>> fringe = new StablePriorityQueue<>();
		fringe.add(new Pair<>(Costs.from(f_heuristic, f_cost, start), start));
		final Set<Node> closed = new HashSet<>();

		for (Pair<Costs, Node> curr = fringe.poll(); curr != null; curr = fringe.poll()) {
			if (endPredicate.test(curr.s)) return curr.s;
			closed.add(curr.s);

			for (final Node n : curr.s.adjacent()) {
				if (!closed.contains(n)) {
					fringe.add(new Pair<>(Costs.from(f_heuristic, f_cost, n, curr.f), n));
				}
			}
		}
		return null;
	}

	private static class Costs implements Comparable<Costs> {
		private final double heuristic, cost;

		private Costs(final double heuristic, final double cost) {
			this.heuristic = heuristic;
			this.cost = cost;
		}

		private static Costs from(final Function<Node, Double> f_heur, final Function<Node, Double> f_cost, final Node n) {
			return new Costs(f_heur.apply(n), f_cost.apply(n));
		}

		private static Costs from(final Function<Node, Double> f_heur, final Function<Node, Double> f_cost, final Node n, final Costs c) {
			return new Costs(f_heur.apply(n), c.getCost() + f_cost.apply(n));
		}

		private double getHeuristic() {
			return heuristic;
		}

		private double getCost() {
			return cost;
		}

		private double sum() {
			return getHeuristic() + getCost();
		}

		@Override
		public int compareTo(final Costs other) {
			return Double.compare(sum(), other.sum());
		}
	}

}
