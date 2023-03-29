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

		space.addElement((GElement) created.receiver);

		envi.addReference(method.get(2).contents(), created);

		return null;
	}
}