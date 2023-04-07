package exercice5;

import java.util.HashMap;
import java.util.Map;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;

public class Reference {
	public static int nbInstances = 1;
	public int id;
	Object receiver;
	Map<String, Command> primitives;
	
	public Reference(Object receiver) {
		this.receiver = receiver;
		primitives = new HashMap<String, Command>();
		id = nbInstances;
		nbInstances++;
	}

	
	public Object getReceiver() {
		return this.receiver;
	}
	
	
	public Command getCommandByName(String name) {
		return primitives.get(name);
	}


    public int run(SNode method) {
    	//System.out.println(method.get(0).contents() + " " + method.get(1).contents());
    	Command c = this.getCommandByName(method.get(1).contents());

		if(c == null) {
			System.err.println("ERREUR : Commande " + method.get(1).contents() + " inexistante, mauvais nom");
			return 2;
		}

    	c.run(this, method);
		return 0;
    }

    public void addCommand(String selector, Command primitive) {
    	if(!primitives.containsKey(selector)) {
    		primitives.put(selector, primitive);
    	}
    }
}
