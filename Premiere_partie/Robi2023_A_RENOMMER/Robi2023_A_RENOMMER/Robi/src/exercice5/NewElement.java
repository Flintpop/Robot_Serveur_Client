package exercice5;

import graphicLayer.GElement;
import stree.parser.SNode;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de créer une nouvelle référence de type {@code GElement}.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( type_el new )}
 * </ul>
 * Resultat : création d'une nouvelle référence de GElement, pas de modifications graphiques ou de l'environnement
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class NewElement implements Command {
	
	public Reference run(Reference reference, SNode method) {
		try {
			@SuppressWarnings("unchecked")
			GElement e = ((Class<GElement>) reference.getReceiver() ).getDeclaredConstructor().newInstance();
			Reference ref = new Reference(e);
			
			// Ajout du set de commande executable par une référence de GElement
			ref.addCommand("setColor", new SetColor());
			ref.addCommand("translate", new Translate());
			ref.addCommand("setDim", new SetDim());
			
			return ref;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


}
