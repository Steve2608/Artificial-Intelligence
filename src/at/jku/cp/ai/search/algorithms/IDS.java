package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

import java.util.function.Predicate;

public class IDS implements Search {

	private int limit;

	public IDS(final int limit) {
		this.limit = limit;
	}

	@Override
	public Node search(final Node start, final Predicate<Node> endPredicate) {
		Node result = null;
		for (int depth = 0; depth <= limit && result == null; depth++) {
			result = new DLDFS(depth).search(start, endPredicate);
		}
		return result;
	}

}