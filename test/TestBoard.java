import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.EndCondition;
import at.jku.cp.ai.rau.endconditions.NoEnd;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
import at.jku.cp.ai.rau.objects.*;
import at.jku.cp.ai.utils.Constants;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestBoard {
	protected Class<?> clazz;

	public TestBoard(final Class<?> clazz) {
		this.clazz = clazz;
	}

	@Parameters
	public static Collection<Object[]> generateParams() {
		final List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[]{Board.class});
		return params;
	}

	private static byte[] toBytes(final IBoard board) {
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(board);
			out.close();
			return bos.toByteArray();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static IBoard fromBytes(final byte[] bytes) {
		try {
			final ByteArrayInputStream ios = new ByteArrayInputStream(bytes);
			final ObjectInput in = new ObjectInputStream(ios);
			final IBoard board = (IBoard) in.readObject();
			in.close();
			return board;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private IBoard fromLevelFile(final String filename) {
		try {
			final Method m = clazz.getMethod("fromLevelFile", String.class);
			return (IBoard) m.invoke(clazz, filename);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private IBoard fromLevelRepresentation(final List<String> lvl) {
		try {
			final Method m = clazz.getMethod("fromLevelRepresentation", List.class);
			return (IBoard) m.invoke(clazz, lvl);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(clazz)
				.suppress(Warning.REFERENCE_EQUALITY, Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	public void saneHashCode() {
		final IBoard board = Board.fromLevelRepresentation(Arrays.asList(
				"###",
				"#p#",
				"###"));

		final int expectedHash = board.hashCode();

		for (int i = 0; i < 10; i++) {
			board.executeMove(Move.STAY);
			board.executeMove(Move.LEFT);
			board.executeMove(Move.RIGHT);
			board.executeMove(Move.UP);
			board.executeMove(Move.DOWN);
		}
		final int actualHash = board.hashCode();

		assertEquals(expectedHash, actualHash);
	}

	@Test
	public void deepCopyBoard() {

		final IBoard a = fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final IBoard b = a.deepCopy();

		assertEquals(a, b);

		assertNotSame(a.getWalls(), b.getWalls());
		assertNotSame(a.getPaths(), b.getPaths());

		assertNotSame(a.getFountains(), b.getFountains());
		assertNotSame(a.getClouds(), b.getClouds());
		assertNotSame(a.getUnicorns(), b.getUnicorns());
		assertNotSame(a.getSeeds(), b.getSeeds());
		assertNotSame(a.getRainbows(), b.getRainbows());
	}

	@Test
	public void deepCopyBoardHashCode() {

		final IBoard a = fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final IBoard b = a.deepCopy();

		assertEquals(a.hashCode(), b.hashCode());

		assertNotSame(a.getWalls(), b.getWalls());
		assertNotSame(a.getPaths(), b.getPaths());

		assertNotSame(a.getFountains(), b.getFountains());
		assertNotSame(a.getClouds(), b.getClouds());
		assertNotSame(a.getUnicorns(), b.getUnicorns());
		assertNotSame(a.getSeeds(), b.getSeeds());
		assertNotSame(a.getRainbows(), b.getRainbows());
	}

	@Test
	public void copyBoardHashCode() {

		final IBoard a = fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final IBoard b = a.copy();

		assertEquals(a.hashCode(), b.hashCode());

		assertSame(a.getWalls(), b.getWalls());
		assertSame(a.getPaths(), b.getPaths());

		assertNotSame(a.getFountains(), b.getFountains());
		assertNotSame(a.getClouds(), b.getClouds());
		assertNotSame(a.getUnicorns(), b.getUnicorns());
		assertNotSame(a.getSeeds(), b.getSeeds());
		assertNotSame(a.getRainbows(), b.getRainbows());
	}

	@Test
	public void copyIsActuallyDifferent() {

		final IBoard a = fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final IBoard b = a.copy();

		assertEquals(a, b);

		b.executeMove(Move.SPAWN);

		assertNotEquals(a, b);
	}

	@Test
	public void copyWithDifferentTickIsEqual() {

		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"####",
						"#p.#",
						"#..#",
						"#..#",
						"#.p#",
						"####"
				));

		final IBoard b = a.copy();

		assertEquals(a, b);

		// p0 moves down
		b.executeMove(Move.DOWN);

		// p1 moves up
		b.executeMove(Move.UP);

		// p0 moves up again
		b.executeMove(Move.UP);

		// p1 moves down again
		b.executeMove(Move.DOWN);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a.getTick(), b.getTick());
	}

	@Test
	public void copyWithSeedsIsEqualAgainAfterBlossoming() {

		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"####",
						"#p.#",
						"#..#",
						"#..#",
						"#.p#",
						"####"
				));

		final IBoard b = a.copy();

		assertEquals(a, b);

		b.executeMove(Move.RIGHT);
		b.executeMove(Move.STAY);
		b.executeMove(Move.SPAWN);
		b.executeMove(Move.STAY);
		b.executeMove(Move.LEFT);
		b.executeMove(Move.STAY);
		b.executeMove(Move.DOWN);
		b.executeMove(Move.STAY);
		b.executeMove(Move.STAY);
		b.executeMove(Move.STAY);
		b.executeMove(Move.STAY);
		b.executeMove(Move.STAY);
		b.executeMove(Move.UP);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a.getTick(), b.getTick());
	}

	@Test
	public void copyBoard() {
		final IBoard a = fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final IBoard b = a.copy();

		assertEquals(a, b);

		assertSame(a.getWalls(), b.getWalls());
		assertSame(a.getPaths(), b.getPaths());

		assertNotSame(a.getFountains(), b.getFountains());
		assertNotSame(a.getClouds(), b.getClouds());
		assertNotSame(a.getUnicorns(), b.getUnicorns());
		assertNotSame(a.getSeeds(), b.getSeeds());
		assertNotSame(a.getRainbows(), b.getRainbows());
	}

	@Test
	public void fewPossibleMoves() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"###",
						"#p#",
						"###"
				));

		final List<Move> moves = a.getPossibleMoves();
		assertThat(moves, hasItem(Move.STAY));
		assertThat(moves, hasItem(Move.SPAWN));
	}

	@Test
	public void seedOnTopOfSeed() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"###",
						"#p#",
						"###"
				));

		a.executeMove(Move.SPAWN);
		assertFalse(a.executeMove(Move.SPAWN));
	}

	@Test
	public void seedOnTopOfSeedTwoPlayers() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"####",
						"#pp#",
						"####"
				));

		a.executeMove(Move.SPAWN); // p0
		assertFalse(a.executeMove(Move.LEFT)); // p1 cannot go right b/c of
		// seed;
	}

	@Test
	public void seedCounter() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"###",
						"#p#",
						"###"
				));

		assertTrue(a.executeMove(Move.SPAWN));
		assertEquals(Unicorn.MAX_SEEDS - 1, a.getUnicorns().get(0).seeds);
	}

	@Test
	public void seedCounterResets() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"######",
						"#p...#",
						"######"
				));

		assertTrue(a.executeMove(Move.SPAWN));
		assertEquals(Unicorn.MAX_SEEDS - 1, a.getUnicorns().get(0).seeds);

		a.executeMove(Move.RIGHT);
		a.executeMove(Move.RIGHT);
		a.executeMove(Move.RIGHT);

		for (int i = 0; i < Seed.DEFAULT_FUSE - Seed.DEFAULT_RANGE - 1; i++)
			a.executeMove(Move.STAY);

		assertEquals(0, a.getSeeds().size());
		assertEquals(3, a.getRainbows().size());
		assertEquals(Unicorn.MAX_SEEDS, a.getUnicorns().get(0).seeds);
	}

	@Test
	public void noMoreThanMaxSeeds() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"#########",
						"#p......#",
						"#########"
				));

		assertTrue(a.executeMove(Move.SPAWN));
		assertTrue(a.executeMove(Move.RIGHT));

		assertTrue(a.executeMove(Move.SPAWN));
		assertTrue(a.executeMove(Move.RIGHT));

		assertThat(a.getPossibleMoves(), hasItem(Move.SPAWN));

		assertTrue(a.executeMove(Move.SPAWN));
		assertTrue(a.executeMove(Move.RIGHT));

		assertEquals(3, a.getSeeds().size());

		assertThat(a.getPossibleMoves(), not(hasItem(Move.SPAWN)));
		assertFalse(a.executeMove(Move.SPAWN));
	}

	@Test
	public void updateSeedsAndRainbows() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"###",
						"#p#",
						"###"
				));
		a.setEndCondition(new NoEnd());

		final V pos = a.getCurrentUnicorn().pos;

		a.executeMove(Move.SPAWN); // Seed.DEFAULT_FUSE - 1 | 4
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 2 | 3
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 3 | 2
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 4 | 1
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 5 | 0

		for (int i = 0; i < Seed.DEFAULT_FUSE - 5; i++)
			a.executeMove(Move.STAY);

		final Rainbow expectedRainbow = new Rainbow(pos, Rainbow.DEFAULT_DURATION - 1);

		assertEquals(1, a.getRainbows().size());
		assertThat(a.at(pos), hasItem(expectedRainbow));
		assertEquals(expectedRainbow, a.getRainbows().get(0));
	}

	@Test
	public void updateClouds() {
		final List<String> lvl = new ArrayList<String>();

		lvl.add("####");
		lvl.add("#pc#");
		lvl.add("####");

		final IBoard a = fromLevelRepresentation(lvl);

		assertEquals(1, a.getClouds().size());

		a.executeMove(Move.SPAWN); // Seed.DEFAULT_FUSE - 1 = 4
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 2 = 3
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 3 = 2
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 4 = 1
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 1 = 0

		for (int i = 0; i < Seed.DEFAULT_FUSE - 5; i++)
			a.executeMove(Move.STAY);

		assertEquals(2, a.getRainbows().size());
		assertEquals(0, a.getClouds().size());
	}

	@Test
	public void rainbowsStopAtClouds() {
		final List<String> lvl = new ArrayList<String>();

		lvl.add("#####");
		lvl.add("#pc.#");
		lvl.add("#####");

		final IBoard a = fromLevelRepresentation(lvl);

		assertEquals(1, a.getClouds().size());

		a.executeMove(Move.SPAWN); // Seed.DEFAULT_FUSE - 1 = 4
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 2 = 3
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 3 = 2
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 4 = 1
		a.executeMove(Move.STAY); // Seed.DEFAULT_FUSE - 5 = 0

		for (int i = 0; i < Seed.DEFAULT_FUSE - 5; i++)
			a.executeMove(Move.STAY);

		assertEquals(2, a.getRainbows().size());
		assertEquals(0, a.getClouds().size());
	}

	@Test
	public void checkRemovable() {
		final List<String> lvl = new ArrayList<String>();

		lvl.add("########");
		lvl.add("#p...cf#");
		lvl.add("########");

		final IBoard a = fromLevelRepresentation(lvl);

		assertTrue(a.isRemovable(new V(5, 1)));
		assertTrue(a.executeMove(Move.RIGHT));
		assertTrue(a.executeMove(Move.RIGHT));
		assertTrue(a.executeMove(Move.RIGHT));
		assertFalse(a.executeMove(Move.RIGHT));
		assertTrue(a.executeMove(Move.SPAWN));
		assertTrue(a.executeMove(Move.LEFT));
		assertTrue(a.executeMove(Move.LEFT));
		assertTrue(a.executeMove(Move.LEFT));
	}

	@Test
	public void updateUnicorns() {
		final List<String> lvl = new ArrayList<String>();

		lvl.add("######");
		lvl.add("#p..p#");
		lvl.add("######");

		final IBoard a = fromLevelRepresentation(lvl);

		// whether the real unicorn is sent sailing, is only testable via a
		// custom
		// endcondition
		final Holder<List<Unicorn>> actualSailing = new Holder<>();

		a.setEndCondition(new EndCondition() {
			@Override
			public boolean hasEnded(final IBoard board, final List<Cloud> evaporated,
			                        final List<Unicorn> sailing) {
				if (sailing.size() > 0) {
					actualSailing.obj = sailing;
					return true;
				}
				return false;
			}

			@Override
			public int getWinner() {
				return -1;
			}

			@Override
			public EndCondition copy() {
				return this;
			}

			@Override
			public String getOutcome() {
				return "TEST";
			}
		});

		assertEquals(2, a.getUnicorns().size());

		a.executeMove(Move.SPAWN);
		a.executeMove(Move.SPAWN);
		for (int i = 0; i < Seed.DEFAULT_FUSE - 2; i++)
			a.executeMove(Move.STAY);

		assertFalse(a.isRunning());
		assertFalse(a.executeMove(Move.STAY));

		assertEquals(3, a.getRainbows().size());
		assertEquals(1, actualSailing.obj.size());
	}

	@Test
	public void testFileRoundtrip() throws IOException {
		final File lvlFile = new File(Files.createTempDirectory(null).toString(),
				"test.lvl");

		final Board expectedBoard = Board.fromLevelRepresentation(
				Arrays.asList(
						"#######",
						"#.pcf.#",
						"#######"));
		expectedBoard.executeMove(Move.LEFT);
		expectedBoard.executeMove(Move.SPAWN);
		expectedBoard.executeMove(Move.RIGHT);
		expectedBoard.executeMove(Move.SPAWN);

		Board.toFile(lvlFile.toString(), expectedBoard);
		final IBoard actualBoard = Board.fromFile(lvlFile.toString());

		assertEquals(expectedBoard, actualBoard);
	}

	@Test
	public void whatsAt() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"#######",
						"#.pcf.#",
						"#######"));
		a.executeMove(Move.SPAWN);

		assertEquals(1, a.at(new V(1, 1)).size()); // path
		assertEquals(3, a.at(new V(2, 1)).size()); // path | unicorn | seed
		assertEquals(2, a.at(new V(3, 1)).size()); // path | cloud
		assertEquals(2, a.at(new V(4, 1)).size()); // path | fountain
		assertEquals(1, a.at(new V(5, 1)).size()); // path

		assertEquals(a.at(new V(1, 1)).get(0), new Path(new V(1, 1)));

		assertEquals(a.at(new V(2, 1)).get(0), new Path(new V(2, 1)));
		assertEquals(a.at(new V(2, 1)).get(1), new Unicorn(new V(2, 1), 0));
		assertEquals(a.at(new V(2, 1)).get(2),
				new Seed(new V(2, 1),
						0, Seed.DEFAULT_FUSE - 1, Seed.DEFAULT_RANGE));

		assertEquals(a.at(new V(3, 1)).get(0), new Path(new V(3, 1)));
		assertEquals(a.at(new V(3, 1)).get(1), new Cloud(new V(3, 1)));

		assertEquals(a.at(new V(4, 1)).get(0), new Path(new V(4, 1)));
		assertEquals(a.at(new V(4, 1)).get(1), new Fountain(new V(4, 1), -1));

		assertEquals(a.at(new V(5, 1)).get(0), new Path(new V(5, 1)));
	}

	@Test
	public void fountainsCorrect() {
		final IBoard a = fromLevelRepresentation(
				Arrays.asList(
						"#######",
						"#..p..#",
						"#.....#",
						"#.....#",
						"#..f..#",
						"#######"));
		assertEquals(1, a.getFountains().size());
		assertThat(a.getFountains(), hasItem(new Fountain(new V(3, 4))));
	}

	@Test
	public void blankIsNotWalkable() {
		final List<String> lines = new ArrayList<>();
		lines.add("#####");
		lines.add("#p f#");
		lines.add("#####");

		final IBoard board = fromLevelRepresentation(lines);

		assertFalse(board.isPassable(new V(2, 1)));
	}

	@Test
	public void isSerializable() throws Exception {
		final IBoard board = fromLevelRepresentation(
				Arrays.asList(
						"#######",
						"#.pcf.#",
						"#######"));
		board.getSeeds().add(new Seed(new V(1, 1), 0, 1, 1));
		board.getSeeds().add(new Seed(new V(5, 1), 0, 2, 1));

		assertEquals(board, fromBytes(toBytes(board)));
	}

	@Test
	public void isSerializable2() throws Exception {
		final IBoard board = fromLevelRepresentation(
				Arrays.asList(
						"#######",
						"#ppppp#",
						"#######"));
		board.executeMove(Move.LEFT);
		board.executeMove(Move.RIGHT);
		board.executeMove(Move.RIGHT);
		board.executeMove(Move.RIGHT);
		board.executeMove(Move.RIGHT);
		board.executeMove(Move.RIGHT);

		assertEquals(board, fromBytes(toBytes(board)));
	}

	@Test
	public void isSerializable3() throws Exception {
		final IBoard board = fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		board.executeMove(Move.LEFT);
		board.executeMove(Move.RIGHT);
		board.executeMove(Move.DOWN);
		board.executeMove(Move.RIGHT);
		board.executeMove(Move.UP);
		board.executeMove(Move.SPAWN);

		assertEquals(board, fromBytes(toBytes(board)));
	}

	@Test
	public void doesNotCrashIfNoFountains() {
		final IBoard board = fromLevelRepresentation(
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
		final IBoard board = fromLevelRepresentation(
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
		final IBoard board = fromLevelRepresentation(
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
		final IBoard board = fromLevelRepresentation(
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
		final IBoard board = fromLevelRepresentation(
				Arrays.asList(
						"#####",
						"#pfp#",
						"#####"));

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
		assertEquals(Fountain.LAST_VISITED_BY_DEFAULT, board.getFountains().get(0).lastVisitedBy);

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
		final IBoard board = fromLevelRepresentation(
				Arrays.asList(
						"#######",
						"#p.f.p#",
						"#######"));

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

	class Holder<T> {
		public T obj;

		public Holder() {
			obj = null;
		}
	}
}
