package exercice5;

import graphicLayer.GString;
import stree.parser.SNode;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de créer une nouvelle référence de type {@code GString}.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( Label new "contenu du GString" )}
 * </ul>
 * Resultat : création d'une nouvelle référence de GString, pas de modifications graphiques ou de l'environnement
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class NewString implements Command {

	@Override
	public Reference run(Reference reference, SNode method) {
		try {
			GString s = new GString(method.get(2).contents());	
			Reference ref = new Reference(s);
			
			// Ajout du set de commande executable par une référence de GString
			ref.addCommand("setColor", new SetColor());
			ref.addCommand("translate", new Translate());
			
			return ref;
			
		} catch (IndexOutOfBoundsException e) {
			System.err.println("ERREUR : Création du Label impossible, absence de contenu");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
}
