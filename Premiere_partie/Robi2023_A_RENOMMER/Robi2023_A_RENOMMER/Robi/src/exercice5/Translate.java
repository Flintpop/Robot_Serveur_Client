package exercice5;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import graphicLayer.GBounded;
import graphicLayer.GImage;
import graphicLayer.GString;
import graphicLayer.Positionnable;
import stree.parser.SNode;

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
