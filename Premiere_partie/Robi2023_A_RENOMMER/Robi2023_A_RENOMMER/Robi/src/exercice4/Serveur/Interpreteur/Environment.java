package exercice4.Serveur.Interpreteur;

import java.util.HashMap;

public class Environment {

	public HashMap<String, Reference> variables;

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

	public Reference getReferenceById(int id) {
		for (String key : variables.keySet()) {
			if (variables.get(key).id == id) {
				return variables.get(key);
			}
		}
		return null;
	}

	public String getEnvString() {
		StringBuilder result = new StringBuilder();
		for (String key : variables.keySet()) {
			result.append(key).append(" = ").append(variables.get(key));
			result.append("\n");
		}
		return result.toString();
	}

	public HashMap<String, Reference> getVariables() {
		return this.variables;
	}
}