import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestLevel {
	@Test
	public void levelFromRepresentation() {
		final List<String> lvl = new ArrayList<>();
		lvl.add("########");
		lvl.add("#p#p#cf#");
		lvl.add("########");

		final Board expected = Board.fromLevelRepresentation(lvl);
		final List<String> states = Board.toStateRepresentation(expected);

		final IBoard actual = Board.fromStateRepresentation(states);
		assertEquals(expected, actual);
	}

	@Test
	public void invalidLevel() {
		final List<String> lvl = new ArrayList<>();
		lvl.add("###");
		lvl.add("#x#");
		lvl.add("###");

		try {
			Board.fromLevelRepresentation(lvl);
			fail();
		} catch (final Throwable t) {
			assertEquals(RuntimeException.class, t.getClass());
		}
	}

	@Test
	public void sizesRectangular() {
		final List<String> lines = new ArrayList<>();
		lines.add("###");
		lines.add("#.#");
		lines.add("#.#");
		lines.add("###");

		final IBoard board = Board.fromLevelRepresentation(lines);

		assertEquals(4, board.getHeight());
		assertEquals(3, board.getWidth());
	}

	@Test
	public void sizesPolygon() {
		final List<String> lines = new ArrayList<>();
		lines.add("###");
		lines.add("#.####");
		lines.add("#....#");
		lines.add("#.##.#");
		lines.add("#.#..#");
		lines.add("####.#");
		lines.add("   ###");

		final IBoard board = Board.fromLevelRepresentation(lines);

		assertEquals(7, board.getHeight());
		assertEquals(6, board.getWidth());
	}

	@Test
	public void unicornPositions() {
		final List<String> lines = new ArrayList<>();
		lines.add("######");
		lines.add("#p...#"); // this one comes first at (1, 1)
		lines.add("#....#");
		lines.add("#...p#"); // this one comes second at (4, 3)
		lines.add("######");

		final IBoard board = Board.fromLevelRepresentation(lines);
		final List<Unicorn> unicorns = board.getUnicorns();
		assertEquals(2, unicorns.size());
		assertEquals(new V(1, 1), unicorns.get(0).pos);
		assertEquals(new V(4, 3), unicorns.get(1).pos);
	}

	@Test
	public void cloudPositions() {
		final List<String> lines = new ArrayList<>();
		lines.add("#######");
		lines.add("#..cc.#"); // (3, 1), (4, 1)
		lines.add("#..c..#"); // (3, 2)
		lines.add("#c....#"); // (1, 3)
		lines.add("#######");

		final IBoard board = Board.fromLevelRepresentation(lines);
		final List<Cloud> clouds = board.getClouds();
		assertEquals(4, clouds.size());
		assertEquals(new V(3, 1), clouds.get(0).pos);
		assertEquals(new V(4, 1), clouds.get(1).pos);
		assertEquals(new V(3, 2), clouds.get(2).pos);
		assertEquals(new V(1, 3), clouds.get(3).pos);
	}
}
