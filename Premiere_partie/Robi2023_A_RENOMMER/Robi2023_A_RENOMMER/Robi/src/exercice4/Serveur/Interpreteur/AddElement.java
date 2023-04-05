package exercice4.Serveur.Interpreteur;


import exercice4.Serveur.Command;
import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

import java.util.Objects;

public class AddElement implements Command {

	Environment envi;

	public AddElement(Environment environment) {
		envi = environment;
	}

	@Override
	public Reference run(Reference ref, SNode method) {

		GSpace space = (GSpace) ref.receiver;

		// Il prend l'élément après le "add"
		SNode el = method.get(3);

		Reference toCreate = envi.getReferenceByName(el.get(0).contents());

		if (toCreate == null) {
			System.err.println("toCreate est null, erreur de getReferenceByName. Vous avez mal tapé l'objet à ajouter.");
			System.err.println(el.contents());
			System.err.println(el.get(0).contents());
		}

		Command com = Objects.requireNonNull(toCreate).getCommandByName(el.get(1).contents());
		Reference created = com.run(toCreate, el);

		space.addElement((GElement) created.receiver);

		envi.addReference(method.get(2).contents(), created);

		space.repaint();
		return null;
	}
}
