package exercice4.Serveur.Interpreteur;

import exercice4.Serveur.Command;
import graphicLayer.Positionnable;
import stree.parser.SNode;

import java.awt.*;

public class Translate implements Command {

	@Override
	public Reference run(Reference ref, SNode method) {

		if( ref.receiver instanceof Positionnable ) {
			Positionnable pos = (Positionnable) ref.receiver;
			Point p = pos.getPosition();
			p.translate( Integer.parseInt(method.get(2).contents()) , Integer.parseInt(method.get(3).contents()));
			pos.setPosition(p);
		}
		
		
		return null; 
	}
	
}
