package exercice4;


import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

public class AddElement implements Command {

	Environment envi;
	
	public AddElement(Environment environment) {
		envi = environment;
	}

	@Override
	public Reference run(Reference ref, SNode method) {
		
		GSpace space = (GSpace) ref.receiver;
		
		SNode el = method.get(3);
		
		Reference toCreate = envi.getReferenceByName(el.get(0).contents());
		Command com = toCreate.getCommandByName(el.get(1).contents());
		Reference created = com.run(toCreate, el);
		
		space.addElement( (GElement) created.receiver);
		
		envi.addReference(method.get(2).contents(), created);
		
		return null;
	}

import stree.parser.SNode;
import graphicLayer.GSpace;
public class AddElement implements Command {
    Environment env;
    public AddElement(Environment env) {
        this.env = env;
    }

    public void run(Reference receiver,SNode method) {

        Reference ref = env.getReferenceByName(method.get(2).contents());
        env.addReference(method.get(2).contents(),ref);

    }

}
