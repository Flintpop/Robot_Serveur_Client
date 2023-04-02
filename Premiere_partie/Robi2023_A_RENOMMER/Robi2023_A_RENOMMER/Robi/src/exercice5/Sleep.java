package exercice5;

import stree.parser.SNode;
import tools.Tools;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de mettre en sommeil le un script pendant un temps donné.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( nom_el sleep duree )}
 * </ul>
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class Sleep implements Command {

	@Override
	public Reference run(Reference ref, SNode method) {
		try {
			int sTime = Integer.parseInt(method.get(2).contents());
			Tools.sleep(sTime);
		} catch (IndexOutOfBoundsException e) {
			System.err.println("ERREUR : mise en sommeil impossible, absence d'argument");
		} catch(NumberFormatException e) {
			System.err.println("ERREUR : mise en sommeil impossible, format de la valeur numerique non valide");
		}
		
		return null;
	}
}
