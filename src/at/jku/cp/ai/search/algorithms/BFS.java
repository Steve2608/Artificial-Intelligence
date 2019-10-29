package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;


public class BFS implements Search {

	@Override
	public Node search(final Node start, final Predicate<Node> endPredicate) {
		if (endPredicate.test(start)) return start;

		final Queue<Node> fringe = new LinkedList<>();
		fringe.add(start);
		final Set<Node> closed = new HashSet<>();

		do {
			final Node curr = fringe.poll();
			for (final Node n : curr.adjacent()) {
				if (!closed.contains(n)) {
					if (endPredicate.test(n)) return n;
					fringe.offer(n);
					closed.add(n);
				}
			}
		} while (!fringe.isEmpty());
		return null;
	}

}
