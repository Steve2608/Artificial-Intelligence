package at.jku.cp.ai.rau.nodes;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OnlyPositionNode implements Node {
	private OnlyPositionNode parent;
	private IBoard board;
	private Move move;
	private V pos;

	public OnlyPositionNode(final IBoard board, final V pos) {
		parent = null;
		move = null;
		this.board = board;
		this.pos = pos;
	}

	public OnlyPositionNode(final OnlyPositionNode parent, final Move move, final V pos) {
		this.parent = parent;
		board = parent.board;
		this.move = move;
		this.pos = pos;
	}

	@Override
	public List<Node> adjacent() {
		if (!board.isRunning())
			return Collections.emptyList();

		final Map<Move, V> mapping = Board.getMoveToDirectionMapping();
		final List<Node> successors = new ArrayList<>();
		for (final Move move : Move.values()) {
			final V next = V.add(pos, mapping.get(move));
			if (board.isPassable(next)) {
				successors.add(new OnlyPositionNode(this, move, next));
			}
		}

		return successors;
	}

	@Override
	public Node parent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		return !board.isRunning();
	}

	@Override
	public boolean isRoot() {
		return parent == null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <State> State getState() {
		return (State) pos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Action> Action getAction() {
		return (Action) move;
	}

	@Override
	public String toString() {
		return pos.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (board == null ? 0 : board.hashCode());
		result = prime * result + (pos == null ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OnlyPositionNode other = (OnlyPositionNode) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (pos == null) {
			return other.pos == null;
		} else return pos.equals(other.pos);
	}


}
