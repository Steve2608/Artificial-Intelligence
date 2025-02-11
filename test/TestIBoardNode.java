import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.Seed;
import at.jku.cp.ai.search.Node;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestIBoardNode {

	@Test
	public void saneHashCode() {
		final IBoard board = Board.fromLevelRepresentation(Arrays.asList(
				"###",
				"#p#",
				"###"));

		final IBoardNode node = new IBoardNode(board);

		final int expectedHash = node.hashCode();

		Node current = node;
		for (int i = 0; i < Seed.DEFAULT_FUSE; i++) {
			assertEquals(2, current.adjacent().size());

			// this is always the 'STAY' node
			current = current.adjacent().get(0);
		}
		final int actualHash = current.hashCode();

		assertEquals(expectedHash, actualHash);
	}

	@Test
	public void differentHashCode() {
		final IBoard board = Board.fromLevelRepresentation(Arrays.asList(
				"###",
				"#p#",
				"###"));

		board.executeMove(Move.SPAWN);

		final IBoardNode node = new IBoardNode(board);

		final int expectedHash = node.hashCode();

		Node current = node;
		for (int i = 0; i < Seed.DEFAULT_FUSE - 1; i++) {
			// this is always the 'STAY' node
			current = current.adjacent().get(0);
			final int actualHash = current.hashCode();
			assertNotEquals(expectedHash, actualHash);
		}
	}
}
