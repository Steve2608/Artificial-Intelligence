package at.jku.cp.ai.search;

import java.util.List;

public interface Node {
	<State> State getState();

	<Action> Action getAction();

	Node parent();

	List<Node> adjacent();

	boolean isRoot();

	boolean isLeaf();

	int hashCode();

	boolean equals(Object obj);
}
