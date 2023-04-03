package exercice5;

import java.awt.Dimension;

import graphicLayer.GBounded;
import graphicLayer.GSpace;
import stree.parser.SNode;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de mettre à jour les dimensions d'un {@code GBounded} ou {@code GSpace}.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( nom_el setDim x y )}
 * </ul>
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class SetDim implements Command {

	@Override
	public Reference run(Reference ref, SNode method) {
		try {
			
			if(ref.receiver instanceof GBounded) {
				GBounded b = (GBounded) ref.receiver;
				Dimension d = new Dimension(Integer.parseInt(method.get(2).contents()), Integer.parseInt(method.get(3).contents()));
				b.setDimension(d);
				
			}
			else if(ref.receiver instanceof GSpace) {
				GSpace s = (GSpace) ref.receiver;
				Dimension d = new Dimension(Integer.parseInt(method.get(2).contents()), Integer.parseInt(method.get(3).contents()));
				s.changeWindowSize(d);
			}
			
		} catch (IndexOutOfBoundsException e) {
			System.err.println("ERREUR : mise a jour des dimensions impossible, absence de la valeur y");
		} catch (NumberFormatException e) {
			System.err.println("ERREUR : mise a jour des dimensions impossible, format des valeur numerique non valide");
		}
		return null;
	}

}
