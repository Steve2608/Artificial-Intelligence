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
public class TestMoves {
	private Board masterBoard;
	private List<Move> expectedValid;

	public TestMoves(final List<String> lvl, final List<Move> expected) {
		masterBoard = Board.fromLevelRepresentation(lvl);
		expectedValid = expected;
	}

	@Parameters
	public static Collection<Object[]> generateParams() {

		final List<Object[]> params = new ArrayList<Object[]>();

		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#...#",
						"#.p.#",
						"#...#",
						"#####"
				),
				Arrays.asList(Move.values())
		});

		params.add(new Object[]{
				Arrays.asList(
						"###",
						"#p#",
						"#.#",
						"###"
				),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN)
		});

		params.add(new Object[]{
				Arrays.asList(
						"###",
						"#.#",
						"#p#",
						"###"
				),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.UP)
		});

		params.add(new Object[]{
				Arrays.asList(
						"####",
						"#p.#",
						"####"
				),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.RIGHT)
		});

		params.add(new Object[]{
				Arrays.asList(
						"####",
						"#.p#",
						"####"
				),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT)
		});

		params.add(new Object[]{
				Arrays.asList(
						"###",
						"#p#",
						"###"
				),
				Arrays.asList(Move.STAY, Move.SPAWN)
		});

		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#*pf#",
						"#####"
				),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.RIGHT)
		});

		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#*p*#",
						"#####"
				),
				Arrays.asList(Move.STAY, Move.SPAWN)
		});

		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#.*.#",
						"#*p*#",
						"#.*.#",
						"#####"
				),
				Arrays.asList(Move.STAY, Move.SPAWN)
		});

		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#fff#",
						"#fpf#",
						"#fff#",
						"#####"
				),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT, Move.UP, Move.DOWN)
		});

		return params;
	}

	@Test
	public void allMovesActuallyPossible() {
		for (final Move move : expectedValid) {
			final IBoard board = masterBoard.copy();

			// ***can't*** execute valid move? --> goto fail!
			if (!board.executeMove(move)) {
				fail("could NOT execute '" + move + "' !");
			}
		}

		final List<Move> expectedInvalid = new ArrayList<>();

		for (final Move move : Move.values()) {
			if (!expectedValid.contains(move)) {
				expectedInvalid.add(move);
			}
		}

		for (final Move move : expectedInvalid) {
			final IBoard board = masterBoard.copy();

			// ***can*** execute invalid move? --> goto fail!
			if (board.executeMove(move)) {
				fail("could execute '" + move + "' despite being forbidden !");
			}
		}
	}
}