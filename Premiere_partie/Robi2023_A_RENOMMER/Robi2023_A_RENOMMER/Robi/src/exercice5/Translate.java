package exercice5;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import graphicLayer.GBounded;
import graphicLayer.GImage;
import graphicLayer.GString;
import graphicLayer.Positionnable;
import stree.parser.SNode;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de déplacer sur les axes x et y un element.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( nom_el translate dx dy )}
 * </ul>
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class Translate implements Command {

	@Override
	public Reference run(Reference ref, SNode method) {

		if( ref.receiver instanceof Positionnable ) {
			Positionnable pos = (Positionnable) ref.receiver;
			Point p = pos.getPosition();
			
			try {
				p.translate( Integer.parseInt(method.get(2).contents()) , Integer.parseInt(method.get(3).contents()));
				pos.setPosition(p);
			} catch (IndexOutOfBoundsException e) {
				System.err.println("ERREUR : translation impossible, pas assez d'arguments");
			} catch(NumberFormatException e) {
				System.err.println("ERREUR : translation impossible, format des valeurs numeriques non valide");
			}
		
		}
		
		
		return null; 
	}
	
}
