package exercice4;

import java.util.HashMap;
<<<<<<< Updated upstream

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
=======
import java.util.Map;

public class Environment {

    Map<String,Reference> hm = new HashMap<>();

    public void addReference(String name, Reference spaceRef) {
        hm.put(name, spaceRef);
    }

    public Reference getReferenceByName(String receiverName) {
        return hm.get(receiverName);
    }

    public void removeReference(String contents) {
        hm.remove(contents);
>>>>>>> Stashed changes
    }
}