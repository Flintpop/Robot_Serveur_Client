package exercice5;

import java.util.HashMap;

import graphicLayer.GContainer;
import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de supprimer un objet {@code Reference}
 * existant du {@code GContainer} où il se trouve et de l'environnement.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( nom_container del nom_ref )}
 * </ul>
 * Resultat : suppression de nom_ref et de toutes les références qu'elle contient
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class DelElement implements Command {

	Environment envi;

	public DelElement(Environment environment) {
		envi = environment;
	}

	@Override
	public Reference run(Reference ref, SNode method) {

		GContainer cont = (GContainer) ref.receiver;
		
		// Récupération de la notation pointée de la référence à supprimer
		String fullName = method.get(0).contents() + "." + method.get(2).contents();
		
		// Récupération de la référence et suppression de son GContainer et de l'environnement
		Reference toDelete = envi.getReferenceByName(fullName);
		cont.removeElement( (GElement) toDelete.receiver );
		envi.variables.remove(fullName);
		
		@SuppressWarnings("unchecked")
		HashMap<String, Reference> hmCopy = (HashMap<String, Reference>) envi.variables.clone();
		
		// Suppression de toutes les références dépendant directement de la la ref supprimée
		// noms tel que : fullName.ref_a_supprimer
		for( String enviVar : hmCopy.keySet() ) {
			if( enviVar.length() > fullName.length() && enviVar.substring(0, fullName.length() + 1).equals(fullName + ".") ) {
				//System.out.println("\tsupprime " + enviVar);
				envi.variables.remove(enviVar);
			}
		}
				
		return null;
	}
}
