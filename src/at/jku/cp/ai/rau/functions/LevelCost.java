package at.jku.cp.ai.rau.functions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

public class LevelCost implements Function<Node, Double> {

	int[][] costs;

	public LevelCost(final List<String> _costs) {
		final int width = _costs.get(0).length();
		final int height = _costs.size();
		costs = new int[width][height];

		assert costs[0].length == height;
		assert costs.length == width;

		int y = 0;
		for (final String line : _costs) {
			for (int x = 0; x < line.length(); x++) {
				final char c = line.charAt(x);
				if (Character.isDigit(c))
					costs[x][y] = Character.digit(c, 10);
				else
					costs[x][y] = 0;
			}

			y++;
		}
	}

	public static LevelCost fromFile(final String filename) {
		try {
			return new LevelCost(Files.readAllLines(Paths.get(filename)));
		} catch (final IOException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public Double apply(final Node node) {
		final IBoard board = node.getState();
		if (board.isRunning()) {
			final V playerPosition = board.getCurrentUnicorn().pos;
			return (double) costs[playerPosition.x][playerPosition.y];
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}
}
