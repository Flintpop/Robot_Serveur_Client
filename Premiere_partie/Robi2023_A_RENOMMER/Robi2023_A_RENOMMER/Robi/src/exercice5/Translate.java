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

		// Verifie que objet référencé possède bien des coordonées (x,y) et est déplacable
		if( ref.receiver instanceof Positionnable ) {
			Positionnable pos = (Positionnable) ref.receiver;
			
			try {
				
				Point p = pos.getPosition();
				p.translate( Integer.parseInt(method.get(2).contents()) , Integer.parseInt(method.get(3).contents()));
				pos.setPosition(p);
				
			} catch (IndexOutOfBoundsException e) {
				System.err.println("ERREUR : translation impossible, pas assez d'arguments");
			} catch(NumberFormatException e) {
				System.err.println("ERREUR : translation impossible, format des valeurs numeriques non valide");
			}
		
		} else {
			System.err.println("ERREUR : l'objet référencé " + method.get(0).contents() + "n'est pas déplacable");
		}
		
		
		return null; 
	}
	
}
