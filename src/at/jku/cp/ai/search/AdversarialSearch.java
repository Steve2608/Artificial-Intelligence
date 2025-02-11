package at.jku.cp.ai.search;


import at.jku.cp.ai.search.datastructures.Pair;

import java.util.function.Function;

public interface AdversarialSearch {
	/**
	 * This method takes a node from the state space and an evalFunction. The
	 * evalFunction should get called when the search terminates. Each leaf of the search-tree
	 * should therefore have a value from this evalFunction associated with it.
	 * <p>
	 * The function returns a pair, namely the most beneficial state we want to be in, given the
	 * current state and the evalFunction. Additionally we'll return the value of the evalFunction
	 * that has led to this decision.
	 *
	 * @param start        the starting node in state space
	 * @param evalFunction the eval function that scores a leaf
	 * @return Pair<T, Double> a pair (bestMove, score)
	 */
	Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction);
}
