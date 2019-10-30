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
		fringe.add(new Pair<>(new Costs(0, 0), start));
		final Set<Node> closed = new HashSet<>();

		for (Pair<Costs, Node> curr = fringe.poll(); curr != null; curr = fringe.poll()) {
			if (endPredicate.test(curr.s)) {
				return curr.s;
			}
			closed.add(curr.s);

			for (final Node n : curr.s.adjacent()) {
				if (!closed.contains(n)) {
					fringe.add(new Pair<>(new ExpandCheaperPathsFirstCosts(f_heuristic, f_cost, n, curr), n));
				}
			}
		}
		return null;
	}

	private static class Costs implements Comparable<Costs> {
		final double heuristic, cost;

		Costs(final double heuristic, final double cost) {
			this.heuristic = heuristic;
			this.cost = cost;
		}

		double getHeuristic() {
			return heuristic;
		}

		double getCost() {
			return cost;
		}

		double sum() {
			return getHeuristic() + getCost();
		}

		@Override
		public int compareTo(final Costs other) {
			return Double.compare(sum(), other.sum());
		}

		@Override
		public String toString() {
			return "Costs{heuristic=" + heuristic + ", cost=" + cost + "}";
		}
	}

	private static class ExpandCheaperPathsFirstCosts extends Costs {

		ExpandCheaperPathsFirstCosts(final Function<Node, Double> f_heuristic,
		                             final Function<Node, Double> f_cost,
		                             final Node expand,
		                             final Pair<Costs, Node> parent) {
			super(f_heuristic.apply(expand), f_cost.apply(expand) + parent.f.getCost());
		}
	}

	private static class ExpandShorterPathsFirstCosts extends Costs {

		ExpandShorterPathsFirstCosts(final Function<Node, Double> f_heuristic,
		                             final Function<Node, Double> f_cost,
		                             final Node expand,
		                             final Pair<Costs, Node> parent) {
			super(f_heuristic.apply(expand) - f_heuristic.apply(parent.s), f_cost.apply(expand) + parent.f.getCost());
		}
	}

}
