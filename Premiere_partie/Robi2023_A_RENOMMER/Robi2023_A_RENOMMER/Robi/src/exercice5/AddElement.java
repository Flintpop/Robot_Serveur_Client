package exercice5;

import exercice6.AddScript;
import graphicLayer.GContainer;
import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

/**
 * <p>Mise en oeuvre de {@code Command} permettant d'ajouter un élément à un objet {@code GContainer}.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( nom_ref add nom_nv_el ( type new [ paramètre ] ) )}
 * </ul>
 * Resultat : nouvelle référence nom_ref.nom_nv_el
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class AddElement implements Command {

	Environment envi;
	
	public AddElement(Environment environment) {
		envi = environment;
	}

	@Override
	public Reference run(Reference ref, SNode method) {

		// si nom référence existe deja -> ne fait rien
		if(envi.getReferenceByName(method.get(0).contents() + "." + method.get(2).contents()) != null)
			return null;

		GContainer cont = (GContainer) ref.receiver;
		
		SNode el = method.get(3);
		
		// Récupération de la référence de type dont le nouvel élément doit hériter
		Reference toCreate = envi.getReferenceByName(el.get(0).contents());
		
		// Execution de la commande new associée au type, et récupération de la référence créée
		Command com = toCreate.getCommandByName(el.get(1).contents());
		Reference created = com.run(toCreate, el);
		
		if(created == null) {
			System.err.println("ERREUR : Echec ajout élément, aucune référence crée");
			return null;
		}
		
		// Ajout des commandes add et del si la nouvel référence est un GContainer
		if(created.receiver instanceof GContainer) {
			created.addCommand("add", new AddElement(envi));
			created.addCommand("del", new DelElement(envi));
		}
		
		// Ajout de la commande addScript à toute nouvelle référence
		created.addCommand("addScript", new AddScript(envi));
		
		// Nouvel objet ajouté à l'environnement et au GContainer 
		cont.addElement( (GElement) created.receiver);
		envi.addReference( method.get(0).contents() + "." + method.get(2).contents(), created);
		
		//System.out.println(method.get(0).contents() + "." + method.get(2).contents());
		
		return null;
	}
}
