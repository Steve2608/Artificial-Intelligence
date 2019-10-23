package at.jku.cp.ai.rau.functions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.utils.RenderUtils;

import java.util.Random;
import java.util.function.Function;

public class RandomCost implements Function<Node, Double> {
	private int[][] costs;

	public RandomCost(final int width, final int height, final Random random, final int upperBound) {
		costs = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				costs[x][y] = 1 + random.nextInt(upperBound - 1);
			}
		}
	}

	@Override
	public Double apply(final Node node) {
		final IBoard board = node.getState();
		final V pos = board.getCurrentUnicorn().pos;
		return (double) costs[pos.x][pos.y];
	}

	public String render(final IBoard board) {
		final char[][] rep = board.getTextBoard();
		for (final Path p : board.getPaths()) {
			rep[p.pos.x][p.pos.y] = Character.forDigit(costs[p.pos.x][p.pos.y], 10);
		}
		return RenderUtils.asStringNoInfo(rep);
	}
}
