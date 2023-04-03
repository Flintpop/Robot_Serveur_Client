package exercice6;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import exercice5.Command;
import exercice5.Environment;
import exercice5.Interpreter;
import exercice5.Reference;
import stree.parser.SNode;


/**
 * <p>Mise en oeuvre de {@code Command} permettant de créer des scripts associés à un objet {@code Reference}.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( nom_ref addScript nom_nv_script ( ( self [ param ]* ) [ s-expressions ]+ ) )}
 * </ul>
 * 
 * Appel du script en S-expression :
 * <ul>
 * 		{@code ( nom_ref nom_nv_script [ param ]* )}
 * </ul>
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class AddScript implements Command {

	Environment envi;
	
	public AddScript(Environment environment) {
		envi = environment;
	}
	
	@Override
	public Reference run(Reference ref, SNode method) {
		
		// Création et ajout d'une nouvelle commande à la référence cible
		ref.addCommand(method.get(2).contents(), new Command (){ 
			
			@Override
			public Reference run(Reference ref, SNode meth) {

				
				// Récupération du script à ajouter à ref
				// Clonage du SNode, sinon cause des erreurs lors du remplacements
				// des mots clés des paramètres dans le script
				SNode script = method.get(3).clone();
				
				// Map servant à identifier les noms des paramètres par les arguments attendus
				HashMap<String, String> argMap = new HashMap<String, String>();

				
				// Nom temporaire (donné dans le script) de chaque paramètre ajouté comme clé au hashmap
				// Seront associées aux valeurs données lors de l'appel de la commande
				argMap.put("self", method.get(0).contents());
		
				for(int i=1; i<script.get(0).size(); i++) {
					
					// Génère toutes les combinaison possible de nomination pointée avec self 
					// comme on ne peut pas savoir à l'avance quels paramètres des noms d'objets à créer et à associer à self
					argMap.put("self." + script.get(0).get(i).contents(), method.get(0).contents() + "." + meth.get(i+1).contents());
					
					argMap.put(script.get(0).get(i).contents(), meth.get(i+1).contents());
				}
				
				
				
				// Parcours chaque s-expression du script pour les executer
				for(SNode scriptPart : script.children()) {
					
					// Ignore première s-expression du script (les paramètres)
					if(scriptPart.equals(script.get(0)))
						continue;
					
					
					// Parcours les SNodes constituants les expressions
					// en remplacant les noms temporaires par leur valeur attendue
					for(SNode child : scriptPart.children()) {
						
						String replace;
						
						if( child.hasChildren() ) {
							
							// Idem, dans le cas d'une s-expression imbriquée 
							for(SNode grandChild : child.children())
								if((replace = argMap.get(grandChild.contents())) != null) 
									grandChild.setContents(replace);
							
							continue;
						}
						
						if( (replace = argMap.get(child.contents())) != null ) 
							child.setContents(replace);
						
						
					}
					
										
					
				}
				
				// Execution du script s-expression par s-expression (en ignorant la première, les paramètres)
				Iterator<SNode> itor = Objects.requireNonNull(script.children()).iterator();
				if(itor.hasNext())
					itor.next();
				while (itor.hasNext())
					new Interpreter().compute(envi, itor.next());
				
				return null;
			}
		});
		
		//System.out.println("Script créé\nCommande " + method.get(2).contents() + " ajoutée à l'objet " + method.get(0).contents());
	
		
		return null;
	}

}
