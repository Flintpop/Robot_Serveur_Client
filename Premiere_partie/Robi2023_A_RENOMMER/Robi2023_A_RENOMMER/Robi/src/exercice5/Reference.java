package exercice5;

import java.util.HashMap;
import java.util.Map;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;

public class Reference {

	Object receiver;
	Map<String, Command> primitives;
	
	public Reference(Object receiver) {
		this.receiver = receiver;
		primitives = new HashMap<String, Command>();
	}

	
	public Object getReceiver() {
		return this.receiver;
	}
	
	
	public Command getCommandByName(String name) {
		return primitives.get(name);
	}


    public void run(SNode method) {
    	//System.out.println(method.get(0).contents() + " " + method.get(1).contents());
    	Command c = this.getCommandByName(method.get(1).contents());

    	c.run(this, method);
    }

    public void addCommand(String selector, Command primitive) {
    	if(!primitives.containsKey(selector)) {
    		primitives.put(selector, primitive);
    	}
    }
}
