package at.jku.cp.ai.sampling;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BayesNetSampler {

	// topological ordering used for sampling
	List<String> topologicalOrdering;
	// stores the conditional probability table for each node of the Bayes Net
	HashMap<String, NodeProbabilityTable> conditionalProbabilityTable;
	// stores for each variable on which other variables it depends on
	HashMap<String, List<String>> conditionedOn;

	public BayesNetSampler(final List<String> topologicalOrdering) {
		this.topologicalOrdering = topologicalOrdering;
		conditionalProbabilityTable = new HashMap<>();
		conditionedOn = new HashMap<>();
	}

	public List<String> getTopologicalOrdering() {
		return topologicalOrdering;
	}

	public void addProbabilities(final String event, final NodeProbabilityTable prob) {
		conditionalProbabilityTable.put(event, prob);
		conditionedOn.put(event, prob.getConditionalVars());
	}

	/**
	 * Gets a random sample from a Bayesian network.
	 *
	 * @return sample: a HashMap of variable names and true/false values.
	 * Example: Querying the Weather Network could return
	 * sample = {"Cloudy": true, "Sprinkler": false, "Rain": true, "Wet": true}
	 */
	public HashMap<String, Boolean> getPriorSample() {
		final Random random = new Random();
		final HashMap<String, Boolean> sample = new HashMap<>();
		for (final String s : topologicalOrdering) {
			final List<String> lookup = conditionedOn.get(s);
			String cond = "";
			if (lookup != null) {
				for (final String l : lookup) {
					final Boolean b = sample.get(l);
					cond += b ? "1" : "0";
				}
			}
			final Double p = conditionalProbabilityTable.get(s).getProbability(cond);
			sample.put(s, random.nextDouble() < p);
		}
		return sample;
	}
}
