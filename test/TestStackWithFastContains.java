import at.jku.cp.ai.search.datastructures.StackWithFastContains;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class TestStackWithFastContains {
	@Test
	public void works()
	{
		StackWithFastContains<Integer> swfc = new StackWithFastContains<Integer>();
		Random random = new Random();
		
		swfc.push(0);
		for(int i = 0; i < 100; i++)
		{
			swfc.push(random.nextInt(1000) + 1);
		}
		
		assertTrue(swfc.contains(0));
	}
}
