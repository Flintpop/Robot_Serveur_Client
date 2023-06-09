package exercice4;

import stree.parser.SNode;

public class Interpreter {
	
    public void compute(Environment environment, SNode next) {
    	
    	if( next.hasChildren() ) {
    		
    		Reference ref = environment.getReferenceByName(next.get(0).contents());
    		
    		ref.run(next);
    		
    	}
    	
    }
    
}
