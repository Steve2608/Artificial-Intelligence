package at.jku.cp.ai.rau.endconditions;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.search.datastructures.Pair;

import java.io.Serializable;
import java.util.*;

final public class PointCollecting implements EndCondition, Serializable {
	private static final long serialVersionUID = 1L;
	private int winner;

	private Outcome outcome;
	private Map<Integer, Integer> scores;
	private int maxTick;

	public PointCollecting() {
		this(Integer.MAX_VALUE); // (almost) no turn limit
	}

	/**
	 * @param maxTick the maximum tick, after which the game ends
	 *                if maxTick is '2', then the last tick in which a move
	 *                can be executed, is 2, and the game ends in this move
	 */
	public PointCollecting(final int maxTick) {
		this.maxTick = maxTick;
		winner = -1;
		outcome = Outcome.SCORE;
		scores = new HashMap<>();
	}

	public PointCollecting(final PointCollecting other) {
		maxTick = other.maxTick;
		winner = other.winner;
		outcome = other.outcome;
		scores = new HashMap<>(other.scores);
	}

	public void setWinnerOnTimeout(final int winner) {
		outcome = Outcome.TIMEOUT;
		this.winner = winner;
	}

	public void setWinnerOnMemout(final int winner) {
		outcome = Outcome.MEMOUT;
		this.winner = winner;
	}

	@Override
	public boolean hasEnded(final IBoard board, final List<Cloud> evaporated, final List<Unicorn> sailing) {
		if (outcome == Outcome.TIMEOUT) {
			return true;
		}

		if (board.getUnicorns().size() == 0) {
			outcome = Outcome.KODRAW;
			winner = -1;
			return true;
		}

		if (board.getUnicorns().size() == 1 && sailing.size() > 0) {
			outcome = Outcome.KOWIN;
			winner = board.getUnicorns().get(0).id;
			return true;
		}

		for (final Fountain m : board.getFountains()) {
			if (m.lastVisitedBy != Fountain.LAST_VISITED_BY_DEFAULT) {
				if (!scores.containsKey(m.lastVisitedBy)) {
					scores.put(m.lastVisitedBy, 0);
				}

				scores.put(m.lastVisitedBy, scores.get(m.lastVisitedBy) + 1);
			}
		}

		return board.getTick() >= maxTick;
	}

	public int getScore(final int unicorn_id) {
		if (scores.containsKey(unicorn_id)) {
			return scores.get(unicorn_id);
		} else {
			return 0;
		}
	}

	@Override
	public int getWinner() {
		if (outcome == Outcome.TIMEOUT || outcome == Outcome.MEMOUT)
			return winner;

		if (outcome == Outcome.SCORE) {
			if (scores.size() == 1) {
				return scores.entrySet().iterator().next().getKey();
			}

			final List<Pair<Integer, Integer>> sorted = new ArrayList<>();

			for (final Map.Entry<Integer, Integer> entry : scores.entrySet()) {
				final int unicorn_id = entry.getKey();
				final int score = entry.getValue();

				sorted.add(new Pair<>(unicorn_id, score));
			}

			Collections.sort(sorted, new Comparator<Pair<Integer, Integer>>() {
				@Override
				public int compare(final Pair<Integer, Integer> o1, final Pair<Integer, Integer> o2) {
					return -1 * Integer.compare(o1.s, o2.s);
				}
			});

			if (sorted.size() == 0 || sorted.get(0).s.equals(sorted.get(1).s)) {
				return -1;
			} else {
				return sorted.get(0).f;
			}
		} else {
			// Outcome.KODRAW
			// Outcome.KOWIN
			return winner;
		}
	}

	@Override
	public String getOutcome() {
		return outcome.toString();
	}

	@Override
	public EndCondition copy() {
		return new PointCollecting(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxTick;
		result = prime * result + (outcome == null ? 0 : outcome.hashCode());
		result = prime * result + (scores == null ? 0 : scores.hashCode());
		result = prime * result + winner;
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
		final PointCollecting other = (PointCollecting) obj;
		if (maxTick != other.maxTick)
			return false;
		if (outcome != other.outcome)
			return false;
		if (scores == null) {
			if (other.scores != null)
				return false;
		} else if (!scores.equals(other.scores))
			return false;
		return winner == other.winner;
	}

	public enum Outcome {
		TIMEOUT, MEMOUT, KOWIN, KODRAW, SCORE
	}

}
