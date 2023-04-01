package exercice4;

import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

import java.awt.*;
import java.lang.reflect.Field;

public class SetColor implements Command {
	@Override
	public Reference run(Reference ref, SNode method) {
		Field field;
		try {
			// Récupère la couleur à partir de la chaîne de caractères
			field = Class.forName("java.awt.Color").getField(method.get(2).contents());
		} catch (NoSuchFieldException | ClassNotFoundException e) {
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
