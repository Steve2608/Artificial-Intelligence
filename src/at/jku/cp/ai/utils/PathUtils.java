package at.jku.cp.ai.utils;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class PathUtils {

	public static List<Node> getPath(final Node endNode) {
		if (null == endNode)
			return Collections.emptyList();

		final List<Node> path = new ArrayList<>();

		Node current = endNode;
		while (!current.isRoot()) {
			path.add(current);
			current = current.parent();
		}

		path.add(current);

		Collections.reverse(path);

		return path;
	}

	@SuppressWarnings("unchecked")
	public static <A> List<A> getStates(final List<Node> nodes) {
		final List<A> path = new ArrayList<A>();

		for (final Node node : nodes)
			path.add(node.getState());

		return path;
	}

	@SuppressWarnings("unchecked")
	public static <A> List<A> getActions(final List<Node> nodes) {
		final List<A> actions = new ArrayList<>();

		if (nodes.size() > 0) {
			for (final Node node : nodes.subList(1, nodes.size()))
				actions.add(node.getAction());
		}
		return actions;
	}

	public static double getPathCost(final IBoard board, final List<Move> moves, final Function<Node, Double> f) {
		double cumulative = 0.0;
		for (final Move move : moves) {
			final Node node = new IBoardNode(board);
			cumulative += f.apply(node);
			board.executeMove(move);
		}
		return cumulative;
	}

	public static void comparePathCost(final IBoard board, final List<Move> expectedMoves, final List<Move> actualMoves, final Function<Node, Double> heuristic, final Function<Node, Double> cost) {
		final int nMoves = Math.max(actualMoves.size(), expectedMoves.size());
		double actualCost = 0.0;
		double expectedCost = 0.0;
		double asum = 0.0, esum = 0.0;

		final IBoard actualBoard = board.copy();
		final IBoard expectedBoard = board.copy();

		for (int i = 0; i < nMoves; i++) {
			Move actualMove = null;
			if (i < actualMoves.size()) {
				final Node actualNode = new IBoardNode(actualBoard);
				actualCost = heuristic.apply(actualNode) + cost.apply(actualNode);
				asum += cost.apply(actualNode);

				actualMove = actualMoves.get(i);
				actualBoard.executeMove(actualMove);
			}

			Move expectedMove = null;
			if (i < expectedMoves.size()) {
				final Node expectedNode = new IBoardNode(expectedBoard);
				expectedCost = heuristic.apply(expectedNode) + cost.apply(expectedNode);
				esum += cost.apply(expectedNode);

				expectedMove = expectedMoves.get(i);
				expectedBoard.executeMove(expectedMove);
			}

			System.out.println("---------------------");
			System.out.println(String.format("Expected cost so far: %.3f; Actual cost so far: %.3f", esum, asum));
			System.out.println(String.format("exmove %5s excost %.3f acmove %5s accost %.3f", expectedMove, expectedCost, actualMove, actualCost));
			System.out.println(RenderUtils.column(heuristic, cost, expectedBoard, actualBoard));
		}
	}

	public static List<V> asList(final int... coords) {
		final List<V> lst = new ArrayList<>();

		if (coords.length % 2 == 1) {
			throw new RuntimeException("invalid length! must be a multiple of 2!");
		}

		for (int i = 0; i < coords.length; i += 2) {
			lst.add(new V(coords[i], coords[i + 1]));
		}

		return lst;
	}

	public static List<IBoard> movesToIBoards(final List<Move> path, final IBoard board) {
		final List<IBoard> nodes = new ArrayList<>();

		if (path.size() > 0) {
			nodes.add(board);

			final IBoard current = board.copy();

			for (final Move move : path) {
				if (!current.executeMove(move)) {
					throw new RuntimeException("move list is bogus!");
				}
				nodes.add(current.copy());
			}
		}

		return nodes;
	}

	public static List<Move> vsToMoves(final List<V> path) {
		final List<Move> moves = new ArrayList<>();
		for (int i = 1; i < path.size(); i++) {
			final V previous = path.get(i - 1);
			final V current = path.get(i);

			final V d = V.sub(current, previous);
			if (Board.getDirectionToMoveMapping().containsKey(d)) {
				moves.add(Board.getDirectionToMoveMapping().get(d));
			} else {
				throw new RuntimeException("the path is broken!");
			}
		}
		return moves;
	}

	public static List<V> fromFile(final String filename) {
		try {
			final List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
			final List<V> path = new ArrayList<>();
			for (final String line : lines)
				path.add(V.fromString(line));
			return path;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void toFile(final String filename, final List<V> path) {
		try {
			if (Files.exists(Paths.get(filename)))
				Files.delete(Paths.get(filename));
			final List<String> lines = new ArrayList<>();
			for (final V v : path)
				lines.add(v.toString());

			Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
