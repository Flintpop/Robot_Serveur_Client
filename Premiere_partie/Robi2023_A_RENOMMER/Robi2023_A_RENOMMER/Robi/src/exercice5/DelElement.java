package exercice5;

import java.util.HashMap;

import graphicLayer.GContainer;
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

		GContainer cont = (GContainer) ref.receiver;
		
		String fullName = method.get(0).contents() + "." + method.get(2).contents();
		
		Reference toDelete = envi.getReferenceByName(fullName);
		cont.removeElement( (GElement) toDelete.receiver );
		
		
		@SuppressWarnings("unchecked")
		HashMap<String, Reference> hmCopy = (HashMap<String, Reference>) envi.variables.clone();
		
		for( String enviVar : hmCopy.keySet() ) {
			if( enviVar.length() > fullName.length() && enviVar.substring(0, fullName.length()).equals(fullName) ) {
				//System.out.println("\tsupprime " + enviVar);
				envi.variables.remove(enviVar);
			}
		}
		
		envi.variables.remove(fullName);
		
		return null;
	}
}
