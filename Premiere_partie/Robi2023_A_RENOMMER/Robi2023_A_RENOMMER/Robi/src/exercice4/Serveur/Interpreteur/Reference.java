package exercice4;


import stree.parser.SNode;

import java.util.HashMap;
import java.util.Map;

public class Reference {

	Object receiver;
	Map<String, Command> primitives;

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

		try {
			c.run(this, method);

		} catch (Exception e) {
			System.err.println("Erreur d'execution de la commande : " + method.contents());
		}
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
