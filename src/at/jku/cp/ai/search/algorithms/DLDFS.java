package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.StackWithFastContains;

import java.util.function.Predicate;

public class DLDFS implements Search {

	private StackWithFastContains<Node> path;
	private int limit;

	public DLDFS(final int limit) {
		this.limit = limit;
	}

	@Override
	public Node search(final Node start, final Predicate<Node> endPredicate) {
		path = new StackWithFastContains<>();
		return dls(start, endPredicate, limit);
	}

	private Node dls(final Node root, final Predicate<Node> endPredicate, final int depth) {
		if (endPredicate.test(root)) return root;
		if (depth <= 0) return null;

		path.push(root);
		for (final Node n : root.adjacent()) {
			if (!path.contains(n)) {
				final Node result = dls(n, endPredicate, depth - 1);
				if (result != null) return result;
			}
		}
		path.pop();
		return null;
	}

}
