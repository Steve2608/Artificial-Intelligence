import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.*;
import at.jku.cp.ai.utils.Constants;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestEqualities {
	@Test
	public void vs() {
		assertEquals(new V(0, 12), new V(0, 12));
		assertNotEquals(new V(12, 12), new V(12, 13));
	}

	@Test
	public void vsEqualsContract() {
		EqualsVerifier.forClass(V.class).verify();
	}

	@Test
	public void clouds() {
		assertEquals(new Cloud(new V(0, 1)), new Cloud(new V(0, 1)));
		assertNotEquals(new Cloud(new V(1, 1)), new Cloud(new V(0, 0)));
	}

	@Test
	public void cloudsEqualsContract() {
		EqualsVerifier.forClass(Cloud.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withRedefinedSuperclass().verify();
	}

	@Test
	public void rainbows() {
		assertEquals(new Rainbow(new V(0, 1), 10), new Rainbow(new V(0, 1), 10));
		assertNotEquals(new Rainbow(new V(1, 1), 10), new Rainbow(new V(0, 1), 10));
		assertNotEquals(new Rainbow(new V(0, 1), 11), new Rainbow(new V(0, 1), 10));
	}

	@Test
	public void rainbowsEqualsContract() {
		EqualsVerifier.forClass(Rainbow.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withRedefinedSuperclass().verify();
	}

	@Test
	public void unicorns() {
		assertEquals(new Unicorn(new V(0, 1), 0), new Unicorn(new V(0, 1), 0));
		assertNotEquals(new Unicorn(new V(1, 1), 0), new Unicorn(new V(0, 1), 0));
		assertNotEquals(new Unicorn(new V(0, 1), 1), new Unicorn(new V(0, 1), 0));
	}

	@Test
	public void unicornsEqualsContract() {
		EqualsVerifier.forClass(Unicorn.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withRedefinedSuperclass().verify();
	}

	@Test
	public void seeds() {
		assertEquals(new Seed(new V(0, 1), 0, 3, 5), new Seed(new V(0, 1), 0, 3, 5));
		assertNotEquals(new Seed(new V(1, 1), 0, 3, 5), new Seed(new V(0, 1), 0, 3, 5));
		assertNotEquals(new Seed(new V(0, 1), 0, 4, 5), new Seed(new V(0, 1), 0, 3, 5));
		assertNotEquals(new Seed(new V(0, 1), 0, 3, 6), new Seed(new V(0, 1), 0, 3, 5));
	}

	@Test
	public void seedsEqualsContract() {
		EqualsVerifier.forClass(Seed.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withRedefinedSuperclass().verify();
	}

	@Test
	public void walls() {
		assertEquals(new Wall(new V(39, 30)), new Wall(new V(39, 30)));
		assertNotEquals(new Wall(new V(39, 29)), new Wall(new V(0, 0)));
	}

	@Test
	public void wallsEqualsContract() {
		EqualsVerifier.forClass(Rainbow.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withRedefinedSuperclass().verify();
	}

	@Test
	public void paths() {
		assertEquals(new Path(new V(39, 30)), new Path(new V(39, 30)));
		assertNotEquals(new Path(new V(39, 29)), new Path(new V(0, 0)));
	}

	@Test
	public void pathsEqualsContract() {
		EqualsVerifier.forClass(Rainbow.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withRedefinedSuperclass().verify();
	}

	@Test
	public void boards() {
		assertEquals(new Board(), new Board());

		final Board a = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final Board b = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");

		assertEquals(a, b);

		for (final IBoard board : Arrays.asList(a, b)) {
			board.executeMove(Move.DOWN);
			board.executeMove(Move.UP);
		}

		assertEquals(a, b);

		final IBoard c = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final Board d = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");

		//Unicorn player0 = c.getMyUnicorn(0);
		c.executeMove(Move.DOWN);

		assertNotEquals(c, d);

		final IBoard e = d.copy();

		assertEquals(d, e);
	}

	@Test
	public void boards2() {
		final IBoard a = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		a.getFountains().add(new Fountain(new V(1, 1), -1));
		a.getSeeds().add(new Seed(new V(7, 1), 0, 2, 2));
		a.getRainbows().add(new Rainbow(new V(6, 1), 2));

		final IBoard b = a.deepCopy();

		assertEquals(a, b);
	}

	@Test
	public void boardsWithoutTicks() {
		final Board a = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");

		final IBoard b = a.copy();
		b.executeMove(Move.STAY);

		assertEquals(a, b);
	}

	@Test
	public void boardsBackAndForth() {
		final Board a = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");

		final IBoard b = a.copy();
		b.executeMove(Move.RIGHT);
		b.executeMove(Move.STAY);
		b.executeMove(Move.LEFT);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void boardNodes() {
		final List<String> lvl = Arrays.asList(
				"#####",
				"#p..#",
				"#...#",
				"#..p#",
				"#####");

		final IBoardNode a = new IBoardNode(Board.fromLevelRepresentation(lvl));
		final IBoardNode b = new IBoardNode(a.getState());

		((IBoard) b.getState()).executeMove(Move.RIGHT);
		((IBoard) b.getState()).executeMove(Move.STAY);
		((IBoard) b.getState()).executeMove(Move.LEFT);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

}
