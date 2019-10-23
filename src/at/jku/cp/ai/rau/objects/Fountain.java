package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fountain extends GameObject {
	public final static int LAST_VISITED_BY_DEFAULT = -1;
	private static final long serialVersionUID = 1L;
	public int lastVisitedBy;

	public Fountain(final V pos, final int lastTouchedBy) {
		this.pos = pos;
		rep = 'f';
		isPassable = true;
		isRemovable = false;
		stopsRainbow = false;
		lastVisitedBy = lastTouchedBy;
	}

	public Fountain(final V pos) {
		this(pos, LAST_VISITED_BY_DEFAULT);
	}

	public Fountain(final Fountain m) {
		this(new V(m.pos), m.lastVisitedBy);
	}

	public static Fountain fromString(final String s) {
		final Pattern p = Pattern.compile("f\\(v\\((\\d+),\\ (\\d+)\\), (-\\d+|\\d+)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new Fountain(
					new V(
							Integer.parseInt(m.group(1)),
							Integer.parseInt(m.group(2))),
					Integer.parseInt(m.group(3)));
		}
		throw new RuntimeException("invalid fountain rep!");
	}

	@Override
	public String toString() {
		return String.format("f(%s, %d)", pos, lastVisitedBy);
	}
}
