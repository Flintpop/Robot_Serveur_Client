package exercice4.Serveur.Interpreteur;

import java.awt.Dimension;

import exercice4.Serveur.Command;
import graphicLayer.GBounded;
import graphicLayer.GSpace;
import stree.parser.SNode;

public class SetDim implements Command {

	@Override
	public Reference run(Reference ref, SNode method) {

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

		return null;
	}

}
