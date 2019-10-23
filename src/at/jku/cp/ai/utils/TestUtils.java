package at.jku.cp.ai.utils;

import at.jku.cp.ai.rau.IBoard;

import java.util.List;

import static org.junit.Assert.fail;

public abstract class TestUtils {
	private TestUtils() {

	}

	public static <T> boolean listEquals(final List<T> expected, final List<T> actual) {
		if (expected.size() != actual.size()) {
			System.out.println(String.format("list sizes differ! (%d != %d)", expected.size(), actual.size()));
			return false;
		}

		for (int i = 0; i < expected.size(); i++) {
			final T tExpected = expected.get(i);
			final T tActual = actual.get(i);

			if (!tExpected.equals(tActual)) {
				System.out.println("elements differ at pos (" + i + ")");
				System.out.println("expected : " + tExpected);
				System.out.println("actual   : " + tActual);
				return false;
			}
		}
		return true;
	}

	public static <T> void assertListEquals(final List<T> expected, final List<T> actual) {
		if (expected.size() > 0 && expected.get(0) instanceof IBoard) {
			@SuppressWarnings("unchecked") final boolean equal = boardsEquals((List<IBoard>) expected, (List<IBoard>) actual);
			if (!equal) {
				fail();
			}
		}

		final boolean equal = listEquals(expected, actual);
		if (!equal) {
			fail();
		}
	}

	private static boolean boardsEquals(final List<IBoard> expected, final List<IBoard> actual) {
		final int minListSize = Math.min(expected.size(), actual.size());
		for (int i = 0; i < minListSize; i++) {
			final IBoard e = expected.get(i);
			final IBoard a = actual.get(i);

			if (!e.equals(a)) {
				System.out.println("--------------------------------------");
				System.out.println("elements differ at pos (" + i + ")");


				for (int j = Math.max(0, i - 1); j < Math.min(i + 2, minListSize); j++) {
					System.out.println(String.format("--- pos (%d) ---", j));
					System.out.println(RenderUtils.column(expected.get(j), actual.get(j)));
				}
				return false;
			}
		}
		return true;
	}
}
