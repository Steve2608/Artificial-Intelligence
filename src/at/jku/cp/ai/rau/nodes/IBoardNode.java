package at.jku.cp.ai.rau.nodes;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IBoardNode implements Node {
	private IBoardNode parent;
	private IBoard board;
	private Move move;

	public IBoardNode(final IBoard board) {
		this(null, null, board);
	}

	public IBoardNode(final IBoardNode parent, final Move move, final IBoard board) {
		this.parent = parent;
		this.move = move;
		this.board = board;
	}

	@Override
	public List<Node> adjacent() {
		if (!board.isRunning())
			return Collections.emptyList();

		final List<Node> successors = new ArrayList<>();
		final List<Move> possible = board.getPossibleMoves();

		for (final Move move : possible) {
			final IBoard next = board.copy();
			next.executeMove(move);
			successors.add(new IBoardNode(this, move, next));
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
		return (State) board;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Action> Action getAction() {
		return (Action) move;
	}

	@Override
	public String toString() {
		return board.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (board == null ? 0 : board.hashCode());
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
		final IBoardNode other = (IBoardNode) obj;
		if (board == null) {
			return other.board == null;
		} else return board.equals(other.board);
	}

}
