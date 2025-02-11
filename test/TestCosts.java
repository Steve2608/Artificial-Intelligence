import at.jku.cp.ai.rau.BlankLevel;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.RandomCost;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class TestCosts {
	@Test
	public void range() {
		final int N = 50;
		final int ub = 10;

		final IBoard blank = new BlankLevel(N, N);
		blank.getUnicorns().add(new Unicorn(new V(1, 1), 0));

		final IBoardNode node = new IBoardNode(blank);

		// make 'reasonably sure', that range stays between [1, ub)
		for (int r = 0; r < 1000; r++) {
			final RandomCost rc = new RandomCost(N, N, new Random(), ub);

			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					blank.getUnicorns().get(0).pos = new V(i, j);
					assertTrue(rc.apply(node) > 0);
					assertTrue(rc.apply(node) < ub);
				}
			}
		}
	}
}
