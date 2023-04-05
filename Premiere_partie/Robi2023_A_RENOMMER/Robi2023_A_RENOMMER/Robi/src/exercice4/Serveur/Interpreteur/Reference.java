package exercice4.Serveur.Interpreteur;


import stree.parser.SNode;

import java.util.HashMap;
import java.util.Map;

public class Reference {

	Object receiver;
	public Map<String, Command> primitives;
	
	public Reference(Object receiver) {
		this.receiver = receiver;
		primitives = new HashMap<>();
	}

	
	public Object getReceiver() {
		return this.receiver;
	}
	
	
	public Command getCommandByName(String name) {
		return primitives.get(name);
	}


    public void run(SNode method) {
    	Command c = this.getCommandByName(method.get(1).contents());
    	
    	c.run(this, method);
    }

    public void addCommand(String selector, Command primitive) {
    	if(!primitives.containsKey(selector)) {
    		primitives.put(selector, primitive);
    	}
    }

	@Override
	public String toString() {
		return receiver.getClass().getName();
	}
}
