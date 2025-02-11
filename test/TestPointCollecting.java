import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.BoardWithHistory;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TestPointCollecting {
	@Test
	public void doesNotCrashIfNoFountains() {
		final IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"####",
						"#p.#",
						"####"));

		final PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		board.executeMove(Move.RIGHT);
		board.executeMove(Move.LEFT);

		assertEquals(0, fc.getScore(0));
	}

	@Test
	public void oneUnicornOneFlag() {
		final IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"####",
						"#pf#",
						"####"));

		final PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(-1, board.getFountains().get(0).lastVisitedBy);

		board.executeMove(Move.RIGHT);

		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		assertEquals(1, fc.getScore(0));
	}

	@Test
	public void twoUnicornsOneFlag() {
		final IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"#####",
						"#pfp#",
						"#####"));
		final PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(-1, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.RIGHT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.STAY);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.LEFT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.LEFT);
		assertEquals(1, board.getFountains().get(0).lastVisitedBy);

		assertEquals(0, board.getEndCondition().getWinner());

		assertEquals(3, fc.getScore(0));
		assertEquals(1, fc.getScore(1));
	}

	@Test
	public void twoUnicornsOneFlagDraw() {
		final IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"#####",
						"#pfp#",
						"#####"));
		final PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(-1, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.RIGHT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.STAY);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.LEFT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.LEFT);
		assertEquals(1, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.LEFT);

		// player 1
		board.executeMove(Move.STAY);

		assertEquals(-1, board.getEndCondition().getWinner());

		assertEquals(3, fc.getScore(0));
		assertEquals(3, fc.getScore(1));
	}

	@Test
	public void twoUnicornsOnOneFlagSimultaenously() {
		final BoardWithHistory board = new BoardWithHistory(
				Board.fromLevelRepresentation(
						Arrays.asList(
								"#####",
								"#pfp#",
								"#####")));

		final PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(Fountain.LAST_VISITED_BY_DEFAULT,
				board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.RIGHT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1 -- as soon as this player steps on the fountain,
		// the fountain is neutral again
		board.executeMove(Move.LEFT);
		assertEquals(Fountain.LAST_VISITED_BY_DEFAULT,
				board.getFountains().get(0).lastVisitedBy);

		board.executeMove(Move.STAY);
		board.executeMove(Move.STAY);
		board.executeMove(Move.STAY);
		board.executeMove(Move.STAY);

		assertEquals(0, board.getEndCondition().getWinner());

		assertEquals(1, fc.getScore(0));
		assertEquals(0, fc.getScore(1));
	}

	@Test
	public void moveLimit() {
		final BoardWithHistory board = new BoardWithHistory(
				Board.fromLevelRepresentation(
						Arrays.asList(
								"#######",
								"#p.f.p#",
								"#######")));

		final PointCollecting fc = new PointCollecting(2);
		board.setEndCondition(fc);

		// tick 0
		assertTrue(board.executeMove(Move.RIGHT));

		// tick 1
		assertTrue(board.executeMove(Move.STAY));

		// tick 2
		assertTrue(board.executeMove(Move.RIGHT));

		assertFalse(board.isRunning());
		assertEquals(2, board.getTick());

		// player 0 won
		assertEquals(0, board.getEndCondition().getWinner());

		// with a score of 1
		assertEquals(1, fc.getScore(0));
		assertEquals(0, fc.getScore(1));
	}

}
