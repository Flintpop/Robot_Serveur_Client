package exercice4.Serveur.Interpreteur;

import stree.parser.SNode;
import tools.Tools;

public class Sleep implements Command {

	@Override
	public Reference run(Reference ref, SNode method) {
		int sTime = Integer.parseInt(method.get(2).contents());
		Tools.sleep(sTime);
		return null;
	}
}
