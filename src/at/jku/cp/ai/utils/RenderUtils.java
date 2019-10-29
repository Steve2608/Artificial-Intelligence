package at.jku.cp.ai.utils;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.*;
import at.jku.cp.ai.search.Node;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RenderUtils {
	public final static int hexblue = 0x19aeff;
	public final static int hexred = 0xff4141;
	public final static Color blue = new Color(hexblue);
	public final static Color red = new Color(hexred);
	public final static Color purple = new Color(0xba00ff);
	public final static Color lightpurple = new Color(0xd76cff);
	public final static Color darkgray = new Color(0x2d2d2d);
	public final static Color medgray = new Color(0x666666);
	public final static Color lightgray = new Color(0xcccccc);
	public final static Color white = new Color(0xffffff);
	public final static Color yellow = new Color(0xffff3e);

	// render several boards next to each other
	public static String column(final IBoard... boards) {
		final StringBuilder sb = new StringBuilder();

		int maxH = 0;
		for (final IBoard board : boards) {
			maxH = Math.max(maxH, board.getHeight());
		}

		for (int y = 0; y < maxH; y++) {
			for (final IBoard board : boards) {
				final char[][] rep = board.getTextBoard();

				if (board.getHeight() <= maxH) {
					for (int x = 0; x < board.getWidth(); x++) {
						sb.append(rep[x][y]);
						sb.append(' ');
					}
				} else {
					sb.append("  ".repeat(board.getWidth()));
				}
				sb.append(' ');
			}
			sb.append('\n');
		}

		return sb.toString();
	}

	// render several boards next to each other
	public static String column(final Function<Node, Double> heuristic, final Function<Node, Double> cost, final IBoard... boards) {
		final StringBuilder sb = new StringBuilder();

		int maxH = 0;
		for (final IBoard board : boards) {
			maxH = Math.max(maxH, board.getHeight());
		}

		for (int y = 0; y < maxH; y++) {
			for (final IBoard board : boards) {
				final Map<V, String> costs = new HashMap<>();
				final Node current = new IBoardNode(board);
				final List<Node> adjacent = current.adjacent();
				for (final Node next : adjacent) {
					final IBoard nextBoard = next.getState();
					final V pos = nextBoard.getCurrentUnicorn().pos;
					final int realCost = (int) (heuristic.apply(next) + cost.apply(next));
					costs.put(pos, Integer.toString(realCost));
				}

				final char[][] rep = board.getTextBoard();

				if (board.getHeight() <= maxH) {
					for (int x = 0; x < board.getWidth(); x++) {
						String cell = "";

						if (costs.containsKey(new V(x, y))) {
							cell = costs.get(new V(x, y));
						} else {
							cell = Character.toString(rep[x][y]);
						}

						sb.append(String.format("%2s", cell));
					}
				} else {
					sb.append("  ".repeat(board.getWidth()));
				}
				sb.append(' ');
			}
			sb.append('\n');
		}

		return sb.toString();
	}

	public static String asString(final char[][] rep) {
		if (rep.length == 0) {
			return "completely empty board";
		}

		final StringBuilder sb = new StringBuilder();

		final String separator = System.getProperty("line.separator");
		sb.append(separator);
		for (int x = 0; x < rep.length; x++) {
			sb.append(String.format("%-2d", x));
		}
		sb.append(separator);

		for (int y = 0; y < rep[0].length; y++) {
			for (int x = 0; x < rep.length - 1; x++) {
				sb.append(rep[x][y]);
				sb.append(" ");
			}
			sb.append(rep[rep.length - 1][y]);
			sb.append(" ");
			sb.append(y);
			sb.append(separator);
		}
		return sb.toString();
	}

	public static String asStringNoInfo(final char[][] rep) {
		if (rep.length == 0) {
			return "completely empty board";
		}

		final StringBuilder sb = new StringBuilder();

		final String separator = System.getProperty("line.separator");
		for (int y = 0; y < rep[0].length; y++) {
			for (final char[] chars : rep) {
				sb.append(chars[y]);
			}
			sb.append(separator);
		}
		return sb.toString();
	}

	public static String visualizePath(final IBoard board, final List<Node> edges) {
		final char[][] canvas = board.getTextBoard();
		for (final Node node : edges) {
			final V pos = ((IBoard) node.getState()).getCurrentUnicorn().pos;
			canvas[pos.x][pos.y] = '@';
		}

		return asString(canvas);
	}

	public static void draw(final Graphics2D gfx, final GameObject g, final int w) {
		if (g instanceof Wall) {
			gfx.setColor(darkgray);
		} else if (g instanceof Path) {
			gfx.setColor(medgray);
		} else if (g instanceof Cloud) {
			gfx.setColor(lightgray);
		} else if (g instanceof Seed) {
			gfx.setColor(purple);
		} else if (g instanceof Rainbow) {
			gfx.setColor(lightpurple);
		} else if (g instanceof Unicorn) {
			if (((Unicorn) g).id == 0) {
				gfx.setColor(blue);
			} else {
				gfx.setColor(red);
			}
		} else if (g instanceof Fountain) {
			gfx.setColor(yellow);
		} else {
			throw new RuntimeException("unknown?");
		}

		gfx.setBackground(gfx.getColor());

		if (g instanceof Fountain) {
			if (((Fountain) g).lastVisitedBy == 0) {
				gfx.setBackground(blue);
			} else if (((Fountain) g).lastVisitedBy == 1) {
				gfx.setBackground(red);
			} else {
				gfx.setBackground(yellow);
			}
		}

		gfx.clearRect(g.pos.x * w, g.pos.y * w, w, w);

		gfx.drawRect(g.pos.x * w, g.pos.y * w, w, w);
		gfx.drawRect(g.pos.x * w + 1, g.pos.y * w + 1, w - 2, w - 2);
	}

	public static void writeBoardAsPNG(final String filename, final IBoard board) {
		final int w = 11;
		try {
			final BufferedImage image = new BufferedImage(
					board.getWidth() * w,
					board.getHeight() * w,
					BufferedImage.TYPE_INT_RGB);

			final Graphics2D gfx = image.createGraphics();
			for (final List<? extends GameObject> objs : board.getAllObjects()) {
				for (final GameObject g : objs) {
					draw(gfx, g, w);
				}
			}

			ImageIO.write(image, "png", new File(filename));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
