package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Unicorn extends GameObject {
	public static final int MAX_SEEDS = 3;
	private static final long serialVersionUID = 1L;
	public int id;
	public int seeds;

	public Unicorn(final V pos, final int id, final int seeds) {
		this.pos = pos;
		this.id = id;
		rep = Character.forDigit(this.id, 10);
		isPassable = true;
		isRemovable = true;
		stopsRainbow = false;
		this.seeds = seeds;
	}

	public Unicorn(final V pos, final int id) {
		this(pos, id, MAX_SEEDS);
	}

	public Unicorn(final Unicorn u) {
		this(new V(u.pos), u.id, u.seeds);
	}

	public static Unicorn fromString(final String s) {
		final Pattern p = Pattern.compile("u\\(v\\((\\d+),\\ (\\d+)\\),\\ (\\d+),\\ (\\d+)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new Unicorn(new V(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
					Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
		}
		throw new RuntimeException("invalid unicorn rep!");
	}

	@Override
	public String toString() {
		return String.format("u(%s, %d, %d)", pos, id, seeds);
	}

}
