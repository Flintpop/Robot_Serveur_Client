package exercice5;

import exercice6.AddScript;
import graphicLayer.GContainer;
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
		
		GContainer cont = (GContainer) ref.receiver;
		
		SNode el = method.get(3);
		
		Reference toCreate = envi.getReferenceByName(el.get(0).contents());
		Command com = toCreate.getCommandByName(el.get(1).contents());
		Reference created = com.run(toCreate, el);
		
		if(created.receiver instanceof GContainer) {
			created.addCommand("add", new AddElement(envi));
			created.addCommand("del", new DelElement(envi));
		}
		
		created.addCommand("addScript", new AddScript(envi));
		
		cont.addElement( (GElement) created.receiver);
		
		envi.addReference( method.get(0).contents() + "." + method.get(2).contents(), created);
		
		System.out.println(method.get(0).contents() + "." + method.get(2).contents());
		
		return null;
	}
}
