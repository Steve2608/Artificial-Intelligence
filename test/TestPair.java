import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.utils.Constants;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestPair {
	@Test
	public void hashCodesEqual() {
		for (final Move move : Move.values()) {
			final Pair<IBoard, Move> a = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl"), move);
			final Pair<IBoard, Move> b = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl"), move);

			assertEquals(a, b);
			assertEquals(a.hashCode(), b.hashCode());
		}
	}

	@Test
	public void hashCodesNotEqualBCMoves() {
		final Pair<IBoard, Move> a = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl"), Move.DOWN);
		final Pair<IBoard, Move> b = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl"), Move.UP);

		assertNotEquals(a, b);
		assertNotEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void hashCodesNotEqualBCBoards() {
		final Pair<IBoard, Move> a = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/arena1.lvl"), Move.STAY);
		final Pair<IBoard, Move> b = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/arena2.lvl"), Move.STAY);

		assertNotEquals(a, b);
		assertNotEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void useInMap() {
		final Board a = Board.fromLevelFile(Constants.ASSET_PATH + "/arena1.lvl");
		final Board b = Board.fromLevelFile(Constants.ASSET_PATH + "/arena2.lvl");

		final Map<Pair<IBoard, Move>, Integer> qmatrix = new HashMap<>();
		qmatrix.put(new Pair<>(a.copy(), Move.DOWN), 1);
		qmatrix.put(new Pair<>(b.copy(), Move.UP), 2);

		for (int i = 0; i < 10; i++) {
			a.executeMove(Move.STAY);
			b.executeMove(Move.STAY);
		}

		assertEquals(2, qmatrix.size());

		assertEquals(1, (int) qmatrix.get(new Pair<>(a.copy(), Move.DOWN)));
		assertEquals(2, (int) qmatrix.get(new Pair<>(b.copy(), Move.UP)));
	}

	@Test
	public void useInMap2() {
		final Board a = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");
		final Board b = Board.fromLevelFile(Constants.ASSET_PATH + "/default.lvl");

		final IBoard aKey = a.copy();
		final IBoard bKey = b.copy();

		final Map<Pair<IBoard, Move>, Integer> qmatrix = new HashMap<>();
		qmatrix.put(new Pair<>(aKey, Move.DOWN), 1);
		qmatrix.put(new Pair<>(bKey, Move.UP), 2);

		final Random random = new Random(0L);
		for (int i = 0; i < 100; i++) {
			a.executeMove(Move.values()[random.nextInt(Move.values().length)]);
			b.executeMove(Move.values()[random.nextInt(Move.values().length)]);
		}

		assertNotEquals(a, aKey);
		assertNotEquals(b, bKey);

		assertEquals(2, qmatrix.size());

		assertEquals(1, (int) qmatrix.get(new Pair<>(aKey, Move.DOWN)));
		assertEquals(2, (int) qmatrix.get(new Pair<>(bKey, Move.UP)));
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Pair.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
}
