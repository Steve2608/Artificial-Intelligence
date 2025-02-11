package at.jku.cp.ai.search.datastructures;

import java.io.Serializable;

public final class Pair<F, S> implements Serializable {
	private static final long serialVersionUID = 1L;
	public final F f;
	public final S s;

	public Pair(final F f, final S s) {
		this.f = f;
		this.s = s;
	}

	@Override
	public String toString() {
		return String.format("pair(%s, %s)", f.toString(), s.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (f == null ? 0 : f.hashCode());
		result = prime * result + (s == null ? 0 : s.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked") final Pair<F, S> other = (Pair<F, S>) obj;
		if (f == null) {
			if (other.f != null)
				return false;
		} else if (!f.equals(other.f))
			return false;
		if (s == null) {
			return other.s == null;
		} else return s.equals(other.s);
	}

}
