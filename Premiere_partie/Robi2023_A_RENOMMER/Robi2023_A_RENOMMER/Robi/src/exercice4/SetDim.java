package exercice4;

import java.awt.Dimension;

import graphicLayer.GBounded;
import stree.parser.SNode;

public class SetDim implements Command {

	@Override
	public Reference run(Reference ref, SNode method) {

		if(ref.receiver instanceof GBounded) {
			GBounded b = (GBounded) ref.receiver;
			Dimension d = new Dimension(Integer.parseInt(method.get(2).contents()), Integer.parseInt(method.get(3).contents()));
			b.setDimension(d);
			
		}

		return null;
	}

}
