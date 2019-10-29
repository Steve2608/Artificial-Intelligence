import at.jku.cp.ai.rau.BlankLevel;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.LevelCost;
import at.jku.cp.ai.rau.functions.RandomCost;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TestRandomCost {
	@Test
	public void persist() throws Exception {
		final IBoard blank = new BlankLevel(11, 11);
		final RandomCost expected = new RandomCost(11, 11, new Random(23L), 10);

		final String temp = Files.createTempFile(null, null).toString();

		Files.write(
				Paths.get(temp),
				expected.render(blank).getBytes(),
				StandardOpenOption.CREATE);

		final List<String> costs = Files.readAllLines(Paths.get(temp), StandardCharsets.UTF_8);

		final LevelCost actual = new LevelCost(costs);

		blank.getUnicorns().add(new Unicorn(new V(1, 1), 0));
		for (final Path p : blank.getPaths()) {
			// put the unicorn on each walking path ...
			blank.getUnicorns().get(0).pos = p.pos;
			final IBoardNode blankNode = new IBoardNode(blank);
			assertEquals(expected.apply(blankNode), actual.apply(blankNode), 0.0);
		}
	}
}
