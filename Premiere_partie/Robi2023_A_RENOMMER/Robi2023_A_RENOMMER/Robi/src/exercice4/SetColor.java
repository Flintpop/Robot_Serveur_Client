package exercice4;

<<<<<<< Updated upstream
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

public class SetColor implements Command {
	
	Map<String, Color> hm = new HashMap<>();
	
	@Override
	public Reference run(Reference ref, SNode method) {
		// TODO Auto-generated method stub
		hm.put("black", Color.black);
		hm.put("white", Color.white);
		hm.put("red", Color.red);
		hm.put("yellow", Color.yellow);
		hm.put("blue", Color.blue);
		
		if( ref.receiver instanceof GSpace ) {
			GSpace space = (GSpace) ref.receiver;
			space.setColor( hm.get(method.get(2).contents()) );
		}
		else if (ref.receiver instanceof GElement) {
			GElement el = (GElement) ref.receiver;
			el.setColor( hm.get(method.get(2).contents()) );			
		}
		
		return null;
	}
}
