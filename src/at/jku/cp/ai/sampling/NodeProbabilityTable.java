package at.jku.cp.ai.sampling;

import java.util.HashMap;
import java.util.List;

public class NodeProbabilityTable {

	private List<String> conditionalVars = null;
	private String name;
	//private List<Double> probability;
	private HashMap<String, Double> probability;

	public NodeProbabilityTable(final String name, final List<String> conditionalVars) {
		this.name = name;
		this.conditionalVars = conditionalVars;
		probability = new HashMap<>();
		//this.probability = probability;
	}

	public String getName() {
		return name;
	}

	public void setProbability(final String conditions, final Double prob) {
		if (conditions == null) {
			probability.put("", prob);
		} else {
			probability.put(conditions, prob);
		}
	}

	public Double getProbability(final String conditions) {
		//if (conditions == null) {
		//	return probability.get(0);
		//}
		//int index = Integer.parseInt(conditions, 2);
		return probability.get(conditions);
	}

	public List<String> getConditionalVars() {
		return conditionalVars;
	}
}
