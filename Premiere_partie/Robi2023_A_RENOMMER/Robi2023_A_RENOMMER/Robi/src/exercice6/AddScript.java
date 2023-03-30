package exercice6;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import exercice5.Command;
import exercice5.Environment;
import exercice5.Interpreter;
import exercice5.Reference;
import stree.parser.SNode;

public class AddScript implements Command {

	Environment envi;
	
	public AddScript(Environment environment) {
		envi = environment;
	}
	
	@Override
	public Reference run(Reference ref, SNode method) {
		// TODO Auto-generated method stub
		
		
		
		// Ajoute une nouvelle commande à la référence cible
		ref.addCommand(method.get(2).contents(), new Command (){ 
			@Override
			public Reference run(Reference ref, SNode meth) {

				
				// Récupération du code du script
				SNode script = method.get(3).clone();
				HashMap<String, String> argMap = new HashMap<String, String>();

				argMap.put("self", method.get(0).contents());
				
				for(int i=1; i<script.get(0).size(); i++) {
					argMap.put("self." + script.get(0).get(i).contents(), method.get(0).contents() + "." + meth.get(i+1).contents());
					
					argMap.put(script.get(0).get(i).contents(), meth.get(i+1).contents());
				}
				
				
				
				
				for(SNode scriptPart : script.children()) {
					
					// Ignore première s-expression du script (les paramètres)
					if(scriptPart.equals(script.get(0)))
						continue;
					
					
					
					for(SNode child : scriptPart.children()) {
						
						String replace;
						
						if( child.hasChildren() ) {
							
							for(SNode grandChild : child.children())
								if((replace = argMap.get(grandChild.contents())) != null) 
									grandChild.setContents(replace);
							
							continue;
						}
						
						if( (replace = argMap.get(child.contents())) != null ) 
							child.setContents(replace);
						
						
					}
					
										
					
				}
				
				Iterator<SNode> itor = Objects.requireNonNull(script.children()).iterator();
				if(itor.hasNext())
					itor.next();
				while (itor.hasNext()) {
					new Interpreter().compute(envi, itor.next());
				}
				
				
				return null;
			}
		});
		
		
		
		
		System.out.println("Script créé\nCommande " + method.get(2).contents() + " ajoutée à l'objet " + method.get(0).contents());
	
		
		return null;
	}

}
