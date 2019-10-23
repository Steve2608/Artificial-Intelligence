package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Cloud extends GameObject {
	private static final long serialVersionUID = 1L;

	public Cloud(final V pos) {
		this.pos = pos;
		rep = 'c';
		isPassable = false;
		isRemovable = true;
		stopsRainbow = true;
	}

	public Cloud(final Cloud c) {
		this(new V(c.pos));
	}

	public static Cloud fromString(final String s) {
		final Pattern p = Pattern.compile("c\\(v\\((\\d+),\\ (\\d+)\\)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new Cloud(new V(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))));
		}
		throw new RuntimeException("invalid cloud rep!");
	}

	@Override
	public String toString() {
		return String.format("c(%s)", pos);
	}

}
