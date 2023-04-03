package exercice4;

import java.util.HashMap;

public class Environment {

	HashMap<String, Reference> variables;

	public Environment() {
		variables = new HashMap<>();
	}

	public void addReference(String refName, Reference ref) {
		if (!variables.containsKey(refName)) {
			variables.put(refName, ref);
		}
	}

	public Reference getReferenceByName(String receiverName) {
		return variables.get(receiverName);
	}

	public String getEnvString() {
		StringBuilder result = new StringBuilder();
		for (String key : variables.keySet()) {
			result.append(key).append(" = ").append(variables.get(key));
			result.append("\n");
		}
		return result.toString();
	}
}