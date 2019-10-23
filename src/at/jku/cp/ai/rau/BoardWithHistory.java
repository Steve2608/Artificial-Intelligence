package at.jku.cp.ai.rau;

import at.jku.cp.ai.rau.endconditions.EndCondition;
import at.jku.cp.ai.rau.objects.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoardWithHistory implements Serializable, IBoard {
	private static final long serialVersionUID = 1L;

	private IBoard start;
	private IBoard board;
	private List<Move> moves;

	public BoardWithHistory(final IBoard board) {
		start = board.deepCopy();
		this.board = board;
		moves = new ArrayList<>();
	}

	public BoardWithHistory(final BoardWithHistory board, final List<Move> moves) {
		start = board.start;
		this.board = board.copy();
		this.moves = new ArrayList<>();
		for (final Move move : moves)
			this.moves.add(move);
	}

	public IBoard getBoard() {
		return board;
	}

	public List<Move> getHistory() {
		return moves;
	}

	public void toFile(final String filename) {
		//TODO: write save code
		throw new UnsupportedOperationException();
	}

	public BoardWithHistory fromFile(final String filename) {
		//TODO: write loading code
		throw new UnsupportedOperationException();
	}

	@Override
	public IBoard copy() {
		return new BoardWithHistory(this, moves);
	}

	@Override
	public IBoard deepCopy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GameObject> at(final V pos) {
		return board.at(pos);
	}

	@Override
	public boolean isStoppingRainbow(final V pos) {
		return board.isStoppingRainbow(pos);
	}

	@Override
	public boolean isRemovable(final V pos) {
		return board.isRemovable(pos);
	}

	@Override
	public boolean isRainbowAt(final V pos) {
		return board.isRainbowAt(pos);
	}

	@Override
	public boolean isPassable(final V pos) {
		return board.isPassable(pos);
	}

	@Override
	public List<Move> getPossibleMoves() {
		return board.getPossibleMoves();
	}

	@Override
	public boolean executeMove(final Move move) {
		final boolean result = board.executeMove(move);
		moves.add(move);
		return result;
	}

	@Override
	public String toString() {
		return board.toString();
	}

	@Override
	public char[][] getTextBoard() {
		return board.getTextBoard();
	}

	@Override
	public Unicorn getCurrentUnicorn() {
		return board.getCurrentUnicorn();
	}

	@Override
	public int getTick() {
		return board.getTick();
	}

	@Override
	public int getWidth() {
		return board.getWidth();
	}

	@Override
	public int getHeight() {
		return board.getHeight();
	}

	@Override
	public boolean isRunning() {
		return board.isRunning();
	}

	@Override
	public EndCondition getEndCondition() {
		return board.getEndCondition();
	}

	@Override
	public void setEndCondition(final EndCondition endCondition) {
		board.setEndCondition(endCondition);
	}

	@Override
	public List<Wall> getWalls() {
		return board.getWalls();
	}

	@Override
	public List<Path> getPaths() {
		return board.getPaths();
	}

	@Override
	public List<Fountain> getFountains() {
		return board.getFountains();
	}

	@Override
	public List<Cloud> getClouds() {
		return board.getClouds();
	}

	@Override
	public List<Unicorn> getUnicorns() {
		return board.getUnicorns();
	}

	@Override
	public List<Seed> getSeeds() {
		return board.getSeeds();
	}

	@Override
	public List<Rainbow> getRainbows() {
		return board.getRainbows();
	}

	@Override
	public List<List<? extends GameObject>> getAllObjects() {
		return board.getAllObjects();
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
		final BoardWithHistory other = (BoardWithHistory) obj;
		if (board == null) {
			return other.board == null;
		} else return board.equals(other.board);
	}

	public String getHistoryAsRepresentationStrings() {
		final StringBuilder sb = new StringBuilder();
		final IBoard current = start.deepCopy();
		final String LS = System.lineSeparator();
		Board.toStateRepresentation(current).forEach(s -> sb.append(s).append(LS));
		sb.append(LS);
		for (final Move move : moves) {
			current.executeMove(move);
			Board.toStateRepresentation(current).forEach(s -> sb.append(s).append(LS));
			sb.append(LS);
		}
		return sb.toString();
	}

}
