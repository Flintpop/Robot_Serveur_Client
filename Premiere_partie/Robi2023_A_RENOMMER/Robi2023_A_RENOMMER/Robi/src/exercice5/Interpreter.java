package exercice5;

import stree.parser.SNode;

public class Interpreter {
	
    public int compute(Environment environment, SNode next) {

		Reference ref = environment.getReferenceByName(next.get(0).contents());

		if(ref == null) {
			System.err.println("ERREUR : reference " + next.get(0).contents() + " inexistante, mauvais nom d'objet");
			return 1;
		}

		return ref.run(next);
    	
    }
    
}
