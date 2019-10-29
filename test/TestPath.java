import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.V;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestPath {

	private static void pathEqualsTest(final Path p1, final Path p2) {
		assertEquals(p1.pos, p2.pos);
		assertEquals(p1.rep, p2.rep);
		assertEquals(p1.isPassable, p2.isPassable);
		assertEquals(p1.isRemovable, p2.isRemovable);
		assertEquals(p1.stopsRainbow, p2.stopsRainbow);
	}

	@Test
	public void testPathV() {
		final Path p1 = new Path(new V(1, 1));
		assertEquals(new V(1, 1), p1.pos);
		assertEquals('.', p1.rep);
		assertTrue(p1.isPassable);
		assertFalse(p1.isRemovable);
		assertFalse(p1.stopsRainbow);
	}

	@Test
	public void testPathPath() {
		final Path p1 = new Path(new V(1, 1));
		final Path p2 = new Path(p1);
		pathEqualsTest(p1, p2);
	}

	@Test
	public void testToStringAndBack() {
		final Path p1 = new Path(new V(1, 1));
		final Path p2 = Path.fromString(p1.toString());
		pathEqualsTest(p1, p2);
	}
}
