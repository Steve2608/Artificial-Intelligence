package at.jku.cp.ai.rau.objects;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class V implements Comparable<V>, Serializable {
	private static final long serialVersionUID = 1L;

	public final short x;
	public final short y;

	/**
	 * although this constructor takes 'int's, internally
	 * the values are stored as 'short'
	 *
	 * @param x
	 * @param y
	 */
	public V(final int x, final int y) {
		this.x = (short) x;
		this.y = (short) y;
	}

	public V(final V other) {
		this(other.x, other.y);
	}

	public static V add(final V a, final V b) {
		return new V(a.x + b.x, a.y + b.y);
	}

	public static V emul(final V a, final int b) {
		return new V(a.x * b, a.y * b);
	}

	public static V fromString(final String s) {
		final Pattern p = Pattern.compile("v\\((\\d+),\\ (\\d+)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new V(Integer.parseInt(m.group(1)), Integer.parseInt(m
					.group(2)));
		}
		throw new RuntimeException("invalid cloud rep!");
	}

	public static double euclidean(final V a, final V b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public static int manhattan(final V a, final V b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}

	public static boolean sameLine(final V a, final V b) {
		return 0 == Math.abs(a.x) - Math.abs(b.x)
				|| 0 == Math.abs(a.y) - Math.abs(b.y);
	}

	public static V sub(final V a, final V b) {
		return new V(a.x - b.x, a.y - b.y);
	}

	@Override
	public String toString() {
		return String.format("v(%d, %d)", x, y);
	}

	@Override
	public int compareTo(final V other) {
		if (hashCode() < other.hashCode()) {
			return -1;
		} else if (hashCode() == other.hashCode()) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int hashCode() {
		return x << 16 | y & 0xFFFF;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final V other = (V) obj;
		if (x != other.x)
			return false;
		return y == other.y;
	}
}
