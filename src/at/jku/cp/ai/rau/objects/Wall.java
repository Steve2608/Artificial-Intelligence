package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wall extends GameObject {
	private static final long serialVersionUID = 1L;

	public Wall(final V pos) {
		this.pos = pos;
		rep = '#';
		isPassable = false;
		isRemovable = false;
		stopsRainbow = true;
	}

	public Wall(final Wall wall) {
		this(new V(wall.pos));
	}

	public static Wall fromString(final String s) {
		final Pattern p = Pattern.compile("w\\(v\\((\\d+),\\ (\\d+)\\)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new Wall(new V(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))));
		}
		throw new RuntimeException("invalid wall rep!");
	}

	@Override
	public String toString() {
		return String.format("w(%s)", pos);
	}
}
