package assignment1;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.DistanceHeuristic;
import at.jku.cp.ai.rau.functions.IBoardPredicate;
import at.jku.cp.ai.rau.functions.LevelCost;
import at.jku.cp.ai.rau.nodes.AlwaysMoveNode;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.algorithms.ASTAR;
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
import java.util.function.Function;
import java.util.function.Predicate;

@RunWith(Parameterized.class)
public class TestHeuristicAndCostSearchers {
	@Rule
	public Timeout timeout = Timeout.seconds(1);
	private String pathToLevel;

	public TestHeuristicAndCostSearchers(final Integer i) {
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
	public void testASTARforEuclideanDistance() throws Exception {
		testSearcherForLevel(
				Board.fromLevelFile(pathToLevel + "/level"),
				AlwaysMoveNode.class,
				ASTAR.class,
				new DistanceHeuristic(
						board -> board.getCurrentUnicorn().pos,
						board -> board.getFountains().get(0).pos,
						V::euclidean
				),
				LevelCost.fromFile(pathToLevel + "/costs"),
				PathUtils.fromFile(pathToLevel + "/astar_ec.path"));
	}

	@Test
	public void testASTARforManhattanDistance() throws Exception {
		testSearcherForLevel(
				Board.fromLevelFile(pathToLevel + "/level"),
				AlwaysMoveNode.class,
				ASTAR.class,
				new DistanceHeuristic(
						board -> board.getCurrentUnicorn().pos,
						board -> board.getFountains().get(0).pos,
						(a, b) -> (double) V.manhattan(a, b)
				),
				LevelCost.fromFile(pathToLevel + "/costs"),
				PathUtils.fromFile(pathToLevel + "/astar_mh.path"));
	}

	private void testSearcherForLevel(
			final IBoard board,
			final Class<?> nodeClazz,
			final Class<?> searcherClazz,
			final Function<Node, Double> heuristic,
			final Function<Node, Double> cost,
			final List<V> expectedPath) throws Exception {

		final IBoard startBoard = board.copy();
		final Fountain end = board.getFountains().get(0);

		final Predicate<Node> endReached = new IBoardPredicate(
				b -> b.isRunning() && b.getCurrentUnicorn().pos.equals(end.pos));

		final List<Move> expectedMoveSequence = PathUtils.vsToMoves(expectedPath);
		final List<IBoard> expectedBoardStates = PathUtils.movesToIBoards(expectedMoveSequence, board.copy());

		final Search searcher = (Search) searcherClazz.getDeclaredConstructor(Function.class, Function.class).newInstance(heuristic, cost);
		final Node startNode = (Node) nodeClazz.getDeclaredConstructor(IBoard.class).newInstance(startBoard);
		final Node endNode = searcher.search(startNode, endReached);

		final List<Node> path = PathUtils.getPath(endNode);
		final List<IBoard> actualBoardStates = PathUtils.getStates(path);
		final List<Move> actualMoveSequence = PathUtils.getActions(path);

		if (!TestUtils.listEquals(expectedBoardStates, actualBoardStates)) {
			System.out.println("expected : " + expectedMoveSequence);
			System.out.println("actual   : " + actualMoveSequence);

			final double expectedCost = PathUtils.getPathCost(board.copy(), expectedMoveSequence, cost);
			final double actualCost = PathUtils.getPathCost(board.copy(), actualMoveSequence, cost);

			System.out.println("expected cost : " + expectedCost);
			System.out.println("actual cost   : " + actualCost);

			PathUtils.comparePathCost(board.copy(), expectedMoveSequence, actualMoveSequence, heuristic, cost);
		}

		TestUtils.assertListEquals(expectedMoveSequence, actualMoveSequence);
		TestUtils.assertListEquals(expectedBoardStates, actualBoardStates);
	}
}
