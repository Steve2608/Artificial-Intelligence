import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestLoopAdd {
	private static List<Integer> source = new ArrayList<>();

	@Before
	public void setup() {
		for (int i = 0; i < 10000000; i++) {
			source.add(i);
		}
	}

	@Test
	public void loopAdd() {
		final Queue<Integer> queue = new LinkedList<>();
		for (final int x : source) {
			queue.add(x);
		}
	}

	@Test
	public void loopAdd2() {
		final Queue<Integer> queue = new LinkedList<>();
		queue.addAll(source);
	}
}
