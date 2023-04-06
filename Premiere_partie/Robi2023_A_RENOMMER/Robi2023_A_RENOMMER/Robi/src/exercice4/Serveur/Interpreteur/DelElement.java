package exercice4.Serveur.Interpreteur;

import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

public class DelElement implements Command {

	Environment envi;

	public DelElement(Environment environment) {
		envi = environment;
	}

	@Override
	public Reference run(Reference ref, SNode method) {

		GSpace space = (GSpace) ref.receiver;
		
		Reference toDelete = envi.getReferenceByName(method.get(2).contents());
		space.removeElement( (GElement) toDelete.getReceiver());
		
		envi.variables.remove(method.get(2).contents());
		
		return null;
	}
}
