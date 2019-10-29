import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestMoveSequences {
	private Board masterBoard;
	private List<Move> path;
	private List<List<Move>> expectedValid;

	public TestMoveSequences(final List<String> lvl, final List<Move> path, final List<List<Move>> expected) {
		masterBoard = Board.fromLevelRepresentation(lvl);
		this.path = path;
		expectedValid = expected;
	}

	@Parameters
	public static Collection<Object[]> generateParams() {
		final List<Object[]> params = new ArrayList<Object[]>();

		params.add(new Object[]{
				Arrays.asList(
						"#######",
						"#.....#",
						"#..p..#",
						"#.....#",
						"#######"
				),
				Arrays.asList(Move.STAY, Move.STAY),
				Arrays.asList(
						Arrays.asList(Move.values()),
						Arrays.asList(Move.values())
				)
		});

		params.add(new Object[]{
				Arrays.asList(
						"###",
						"#p#",
						"#.###",
						"#...#",
						"#####"
				),
				Arrays.asList(Move.DOWN, Move.DOWN, Move.RIGHT, Move.RIGHT),
				Arrays.asList(
						Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.DOWN),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT)
				)
		});

		params.add(new Object[]{
				Arrays.asList(
						"##########",
						"#p#......#",
						"#.#.######",
						"#.......f#",
						"#.#.######",
						"#........#",
						"##########"
				),
				Arrays.asList(Move.DOWN, Move.DOWN, Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.RIGHT),
				Arrays.asList(
						Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN, Move.UP),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.DOWN, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.DOWN, Move.LEFT, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT)
				)
		});

		return params;
	}

	@Test
	public void completeMoveSequencePossible() {
		final IBoard board = masterBoard.copy();
		for (int i = 0; i < path.size(); i++) {
			final List<Move> current = expectedValid.get(i);
			allMovesActuallyPossible(board, current);
			final Move move = path.get(i);
			board.executeMove(move);
		}
	}

	private void allMovesActuallyPossible(final IBoard testBoard, final List<Move> currentExpectedValid) {
		for (final Move move : currentExpectedValid) {
			final IBoard board = testBoard.copy();

			// ***can't*** execute valid move? --> goto fail!
			if (!board.executeMove(move)) {
				fail("could NOT execute '" + move + "' !");
			}
		}

		final List<Move> expectedInvalid = new ArrayList<>();

		for (final Move move : Move.values()) {
			if (!currentExpectedValid.contains(move)) {
				expectedInvalid.add(move);
			}
		}

		for (final Move move : expectedInvalid) {
			final IBoard board = testBoard.copy();

			// ***can*** execute invalid move? --> goto fail!
			if (board.executeMove(move)) {
				fail("could execute '" + move + "' despite being forbidden !");
			}
		}
	}
}
