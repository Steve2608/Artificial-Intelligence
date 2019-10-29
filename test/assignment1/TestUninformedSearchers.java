package assignment1;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.IBoardPredicate;
import at.jku.cp.ai.rau.nodes.AlwaysMoveNode;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.algorithms.BFS;
import at.jku.cp.ai.search.algorithms.DLDFS;
import at.jku.cp.ai.search.algorithms.IDS;
import at.jku.cp.ai.utils.Constants;
import at.jku.cp.ai.utils.PathUtils;
import at.jku.cp.ai.utils.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestUninformedSearchers {
	@Rule
	public Timeout timeout = Timeout.seconds(40);
	private String pathToLevel;


	public TestUninformedSearchers(final Integer i) {
		pathToLevel = String.format(Constants.ASSET_PATH + "/assignment1/L%d", i);
	}

	@Parameters
	public static Collection<Object[]> generateParams() {
		final List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < Constants.NUMBER_OF_LEVELS; i++) {
			params.add(new Object[]{i});
		}
		return params;
	}

	@Test
	public void testBFS() throws Exception {
		testSearcherForLevel(
				Board.fromLevelFile(pathToLevel + "/level"),
				PathUtils.fromFile(pathToLevel + "/bfs.path"),
				IBoardNode.class,
				new BFS());
	}

	@Test
	public void testIDS() throws Exception {
		final List<V> pathToGoal = PathUtils.fromFile(pathToLevel + "/bfs.path");
		testSearcherForLevel(
				Board.fromLevelFile(pathToLevel + "/level"),
				pathToGoal,
				AlwaysMoveNode.class,
				new IDS(pathToGoal.size()));

	}

	@Test
	public void testDFS() throws Exception {
		testSearcherForLevel(
				Board.fromLevelFile(pathToLevel + "/level"),
				PathUtils.fromFile(pathToLevel + "/dfs.path"),
				AlwaysMoveNode.class,
				new DLDFS(40));
	}

	private void testSearcherForLevel(final IBoard board, final List<V> expectedPath, final Class<?> nodeClazz, final Search searcher) throws Exception {
		final IBoard startBoard = board.copy();
		final Fountain end = board.getFountains().get(0);

		final Predicate<Node> endReached = new IBoardPredicate(b -> b.isRunning() && b.getCurrentUnicorn().pos.equals(end.pos));

		final List<Move> expectedMoveSequence = PathUtils.vsToMoves(expectedPath);
		final List<IBoard> expectedBoardStates = PathUtils.movesToIBoards(expectedMoveSequence, board.copy());

		final Node startNode = (Node) nodeClazz.getDeclaredConstructor(IBoard.class).newInstance(startBoard);
		final Node endNode = searcher.search(startNode, endReached);

		if (null == endNode) {
			fail("goal not found!");
		}

		// HINT: you may use this to see the actual path you found
		// System.out.println(RenderUtils.visualizePath(startBoard,
		// PathUtils.getPath(endNode)));

		final List<Node> path = PathUtils.getPath(endNode);
		final List<IBoard> actualBoardStates = PathUtils.getStates(path);
		TestUtils.assertListEquals(expectedBoardStates, actualBoardStates);

		final List<Move> actualMoveSequence = PathUtils.getActions(path);
		TestUtils.assertListEquals(expectedMoveSequence, actualMoveSequence);
	}
}
