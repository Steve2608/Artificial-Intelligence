package assignment1;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.algorithms.GBFS;
import at.jku.cp.ai.utils.PathUtils;
import at.jku.cp.ai.utils.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public class TestHeuristicSearchersEdgecases {
	@Rule
	public TestRule globalTimeout = Timeout.seconds(1);
	
	@Test
	public void gbfsNoInfiniteLoop() {
		noInfiniteLoop(new GBFS(new Function<Node, Double>(){
			@Override
			public Double apply(Node t) {
				return 0.0;
			}
		}));
	}
	
	private void noInfiniteLoop(Search searcher) {
		IBoard board = Board.fromLevelRepresentation(Arrays.asList(
				"####",
				"#p.#",
				"####"));

		IBoard startBoard = board.copy();
		Predicate<Node> endReached = b -> false;
		
		Node startNode = new IBoardNode(startBoard);
		Node endNode = searcher.search(startNode, endReached);

		List<Node> path = PathUtils.getPath(endNode);
		List<Node> actualBoardNodes = PathUtils.getStates(path);
		List<IBoard> actualBoardStates = PathUtils.getStates(actualBoardNodes);
		TestUtils.assertListEquals(Collections.emptyList(), actualBoardStates);
		
		List<Move> actualMoveSequence = PathUtils.getActions(path);
		TestUtils.assertListEquals(Collections.emptyList(), actualMoveSequence);
	}
}
