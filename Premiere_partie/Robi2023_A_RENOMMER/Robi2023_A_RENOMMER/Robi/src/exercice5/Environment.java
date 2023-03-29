package exercice5;

import java.util.HashMap;

public class Environment {
	
	HashMap<String, Reference> variables;
	
	public Environment() {
		variables = new HashMap<String, Reference>();
	}
	
    public void addReference(String refName, Reference ref) {
    	if(!variables.containsKey(refName)) {
    		variables.put(refName, ref);
    	}
    }

    public Reference getReferenceByName(String receiverName) {
        return variables.get(receiverName);
    }
}
