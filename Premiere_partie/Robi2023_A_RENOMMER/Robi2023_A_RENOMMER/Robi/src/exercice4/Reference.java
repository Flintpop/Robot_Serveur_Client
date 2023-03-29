package exercice4;

<<<<<<< Updated upstream
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
    	Command c = this.getCommandByName(method.get(1).contents());
    	
    	c.run(this, method);
    }

    public void addCommand(String selector, Command primitive) {
    	if(!primitives.containsKey(selector)) {
    		primitives.put(selector, primitive);
    	}
=======
import stree.parser.SNode;

import java.util.HashMap;
import java.util.Map;

public class Reference {
    Object obj;
    Map<String,Command> hm = new HashMap<String,Command>();
    public Reference(Object obj) {
        this.obj = obj;
    }

    public void run(SNode expr) {
        Command cmd = getCommandByName(expr.get(1).contents());
        if (cmd == null)
            throw new Error("unable to get command for: " + expr.contents());
        cmd.run(this, expr);
    }

    public void addCommand(String selector, Command primitive) {
        hm.put(selector, primitive);
    }

    public Command getCommandByName(String name) {
        return hm.get(name);
    }

    public Object getReceiver() {
        return obj;
>>>>>>> Stashed changes
    }

}
