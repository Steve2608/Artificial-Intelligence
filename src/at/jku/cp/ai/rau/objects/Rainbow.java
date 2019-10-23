package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Rainbow extends GameObject {

	private static final long serialVersionUID = 1L;

	public static int DEFAULT_DURATION = 2;
	public int duration;

	public Rainbow(final V pos, final int duration) {
		this.pos = pos;
		this.duration = duration;
		rep = '+';
		isPassable = true;
		isRemovable = false;
		stopsRainbow = false;
	}

	public Rainbow(final Rainbow r) {
		this(new V(r.pos), r.duration);
	}

	public static Rainbow fromString(final String s) {
		final Pattern p = Pattern.compile("r\\(v\\((\\d+),\\ (\\d+)\\),\\ (\\d+)\\)");
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return new Rainbow(new V(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))), Integer.parseInt(m.group(3)));
		}
		throw new RuntimeException("invalid rainbow rep!");
	}

	@Override
	public String toString() {
		return String.format("r(%s, %d)", pos, duration);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + duration;
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
		final Rainbow other = (Rainbow) obj;
		return duration == other.duration;
	}


}
