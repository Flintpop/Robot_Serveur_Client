package exercice5;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de modifier la couleur d'un {@code GElement} ou {@code GSpace}.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( nom_el setColor nom_couleur )}
 * </ul>
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class SetColor implements Command {
	
	Map<String, Color> hm = new HashMap<>();
	
	@Override
	public Reference run(Reference ref, SNode method) {
		Field field;
		try {
			// Récupère la couleur à partir de la chaîne de caractères
			field = Class.forName("java.awt.Color").getField(method.get(2).contents());
		} catch (NoSuchFieldException e) {
			System.err.println("ERREUR : nom de couleur inexistant");
			return null;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}


		if( ref.receiver instanceof GSpace ) {
			GSpace space = (GSpace) ref.receiver;
			try {
				space.setColor((Color) field.get(null));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		else if (ref.receiver instanceof GElement) {
			GElement robi = (GElement) ref.receiver;
			try {
				robi.setColor((Color) field.get(null));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		return null;
	}
}
