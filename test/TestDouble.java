import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDouble {
	@Test
	public void testIntegerToDouble() {
		Double d1 = new Double(0.0);
		Double d2 = new Double(0.0);

		final int intdiff = 1;
		final Double doublediff = new Double(intdiff);
		for (int t = -10000; t < 10000; t++) {
			d1 = (double) t;
			d2 = (double) t + intdiff;

			assertEquals(doublediff, d2 - d1, 0);
			assertTrue(Double.doubleToRawLongBits(doublediff) == Double.doubleToRawLongBits(d2 - d1));
		}
	}
}
