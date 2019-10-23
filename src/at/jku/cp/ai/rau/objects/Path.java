package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path extends GameObject {

	private static final long serialVersionUID = 1L;

	public Path(final V pos) {
		this.pos = pos;
		rep = '.';
		isPassable = true;
		isRemovable = false;
		stopsRainbow = false;
	}

	public Path(final Path path) {
		this(new V(path.pos));
	}

	public static Path fromString(final String s) {
		final Pattern p = Pattern.compile("p\\(v\\((\\d+),\\ (\\d+)\\)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new Path(new V(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))));
		}
		throw new RuntimeException("invalid path rep!");
	}

	@Override
	public String toString() {
		return String.format("p(%s)", pos);
	}
}
