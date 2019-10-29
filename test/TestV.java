import at.jku.cp.ai.rau.objects.V;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class TestV {

	@Test
	public void equalityOfV() {
		assertEquals(new V(10932, 138), new V(10932, 138));
		assertNotEquals(new V(0, 1), new V(1, 0));
		assertEquals(new V(0, 0), new V(0, 0));
		assertEquals(new V(1, 3), new V(1, 3));
	}

	@Test
	public void emulOfV() {
		V v1 = new V(0, 1);
		int b = 0;

		V result = V.emul(v1, b);
		assertEquals(new V(0, 0), result);

		v1 = new V(-1, 1);
		b = -1;

		result = V.emul(v1, b);
		assertEquals(new V(1, -1), result);
	}

	@Test
	public void equalsOfV() {
		final V v1 = new V(0, 1);
		final V v2 = new V(0, 1);

		assertFalse(v1.equals(null));

		assertFalse(v1.equals(new Object()));

		assertTrue(v1.equals(v1));
		assertTrue(v1.equals(v2));
	}

	@Test
	public void manhattenOfV() {
		final V v1 = new V(0, 0);
		final V v2 = new V(0, 1);
		final V v3 = new V(1, 1);
		final V v4 = new V(1, 0);

		assertEquals(0, V.manhattan(v1, v1));
		assertEquals(0, V.manhattan(v2, v2));
		assertEquals(0, V.manhattan(v3, v3));
		assertEquals(0, V.manhattan(v4, v4));

		assertEquals(1, V.manhattan(v1, v2));
		assertEquals(1, V.manhattan(v2, v3));
		assertEquals(1, V.manhattan(v3, v4));
		assertEquals(1, V.manhattan(v4, v1));

		assertEquals(1, V.manhattan(v1, v4));
		assertEquals(1, V.manhattan(v4, v3));
		assertEquals(1, V.manhattan(v3, v2));
		assertEquals(1, V.manhattan(v2, v1));

		assertEquals(2, V.manhattan(v1, v3));
		assertEquals(2, V.manhattan(v2, v4));

		assertEquals(2, V.manhattan(v3, v1));
		assertEquals(2, V.manhattan(v4, v2));
	}

	@Test
	public void sameLine() {
		final Random random = new Random(23L);

		for (int i = 0; i < 100; i++) {
			final int a = random.nextInt();
			final int b = random.nextInt();

			final V origin = new V(a, b);
			final V v1 = new V(a + random.nextInt(), b);
			final V v2 = new V(a - random.nextInt(), b);
			final V v3 = new V(a, b + random.nextInt());
			final V v4 = new V(a, b - random.nextInt());

			assertTrue(V.sameLine(origin, v1));
			assertTrue(V.sameLine(origin, v2));
			assertTrue(V.sameLine(origin, v3));
			assertTrue(V.sameLine(origin, v4));

		}
	}

	@Test
	public void checkHashCollisions() {
		final V a = new V(1, -1);
		final V b = new V(2, -1);
		final V c = new V(3, -1);

		assertNotEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a.hashCode(), c.hashCode());
		assertNotEquals(b.hashCode(), c.hashCode());
	}

	@Test
	public void checkHashCollisionsSmoke() {
		final Map<V, Integer> counter = new HashMap<V, Integer>();

		final int runs = 1000;
		for (int i = -runs; i < runs; i++) {
			for (int j = -runs; j < runs; j++) {
				final V a = new V(i, j);
				if (!counter.containsKey(a))
					counter.put(a, 0);

				counter.put(a, counter.get(a) + 1);
			}
		}

		for (final Map.Entry<V, Integer> e : counter.entrySet()) {
			if (e.getValue() > 1) {
				fail("hasherror:" + e);
			}
		}
	}
}
