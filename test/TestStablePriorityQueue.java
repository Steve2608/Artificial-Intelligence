import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestStablePriorityQueue {

	List<Pair<Integer, String>> expected = Arrays.asList(
			new Pair<>(1, "A"),
			new Pair<>(1, "B"),
			new Pair<>(1, "C"),
			new Pair<>(2, "D"),
			new Pair<>(3, "E"),
			new Pair<>(4, "F"));

	@Test
	public void testOrdering1() {
		final StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		for (final Pair<Integer, String> p : expected) {
			pq.add(p);
		}

		for (final Pair<Integer, String> p : expected) {
			assertEquals(p, pq.poll());
		}
	}

	@Test
	public void testOrdering2() {
		final StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		final List<Pair<Integer, String>> fixture = Arrays.asList(
				new Pair<>(1, "A"),
				new Pair<>(2, "D"),
				new Pair<>(3, "E"),
				new Pair<>(1, "B"),
				new Pair<>(4, "F"),
				new Pair<>(1, "C"));

		for (final Pair<Integer, String> p : fixture) {
			pq.add(p);
		}

		for (final Pair<Integer, String> p : expected) {
			assertEquals(p, pq.poll());
		}
	}

	@Test
	public void testOrdering3() {
		final StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		final List<Pair<Integer, String>> fixture = Arrays.asList(
				new Pair<>(4, "F"),
				new Pair<>(2, "D"),
				new Pair<>(1, "A"),
				new Pair<>(1, "B"),
				new Pair<>(3, "E"),
				new Pair<>(1, "C"));

		for (final Pair<Integer, String> p : fixture) {
			pq.add(p);
		}

		for (final Pair<Integer, String> p : expected) {
			assertEquals(p, pq.poll());
		}
	}

	@Test
	public void testOrderingViaAddAll() {
		final StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		final List<Pair<Integer, String>> fixture = Arrays.asList(
				new Pair<>(4, "F"),
				new Pair<>(1, "A"),
				new Pair<>(2, "D"),
				new Pair<>(1, "B"),
				new Pair<>(3, "E"),
				new Pair<>(1, "C"));

		pq.addAll(fixture);

		for (final Pair<Integer, String> p : expected) {
			assertEquals(p, pq.poll());
		}
	}
}
