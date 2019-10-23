package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Seed extends GameObject {
	private static final long serialVersionUID = 1L;

	public static int DEFAULT_FUSE = 8;
	public static int DEFAULT_RANGE = 3;
	public int fuse;
	public int range;
	public int spawnedBy;

	public Seed(final V pos, final int spawnedBy, final int fuse, final int range) {
		this.pos = pos;
		this.fuse = fuse;
		this.range = range;
		this.spawnedBy = spawnedBy;
		rep = '*';
		isPassable = false;
		isRemovable = true;
		stopsRainbow = true;
	}

	public Seed(final Seed s) {
		this(new V(s.pos), s.spawnedBy, s.fuse, s.range);
	}

	public static Seed fromString(final String s) {
		final Pattern p = Pattern.compile("s\\(v\\((\\d+),\\ (\\d+)\\),\\ (\\d+),\\ (\\d+), (\\d+)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new Seed(new V(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
					Integer.parseInt(m.group(3)),
					Integer.parseInt(m.group(4)),
					Integer.parseInt(m.group(5)));
		}
		throw new RuntimeException("invalid seed rep!");
	}

	@Override
	public String toString() {
		return String.format("s(%s, %d, %d, %d)", pos.toString(), spawnedBy, fuse, range);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + fuse;
		result = prime * result + range;
		result = prime * result + spawnedBy;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Seed other = (Seed) obj;
		if (fuse != other.fuse)
			return false;
		if (range != other.range)
			return false;
		return spawnedBy == other.spawnedBy;
	}

}
