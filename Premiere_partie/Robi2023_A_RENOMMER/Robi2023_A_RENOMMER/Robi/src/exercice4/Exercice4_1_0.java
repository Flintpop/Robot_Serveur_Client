package exercice4;

// 
//	(space setColor black)  
//	(robi setColor yellow) 
//	(space sleep 2000) 
//	(space setColor white)  
//	(space sleep 1000) 	
//	(robi setColor red)		  
//	(space sleep 1000)
//	(robi translate 100 0)
//	(space sleep 1000)
//	(robi translate 0 50)
//	(space sleep 1000)
//	(robi translate -100 0)
//	(space sleep 1000)
//	(robi translate 0 -40)
//	(space setColor black) (robi setColor yellow) (space sleep 2000) (space setColor white) (space sleep 1000) (robi setColor red) (space sleep 1000) (robi translate 100 0) (space sleep 1000) (robi translate 0 50) (space sleep 1000) (robi translate -100 0) (space sleep 1000) (robi translate 0 -40)

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

public class Exercice4_1_0 {
	// Une seule variable d'instance
	Environment environment = new Environment();

	public Exercice4_1_0() {
		// space et robi sont temporaires ici
		GSpace space = new GSpace("Exercice 4", new Dimension(200, 100));
		GRect robi = new GRect();

		space.addElement(robi);
		space.open();

		Reference spaceRef = new Reference(space);
		Reference robiRef = new Reference(robi);

		// Initialisation des references : on leur ajoute les primitives qu'elles
		// comprenent
<<<<<<< Updated upstream
		
		spaceRef.addCommand("setColor", new SetColor());
		spaceRef.addCommand("sleep", new Sleep());
		
		robiRef.addCommand("setColor", new SetColor());
		robiRef.addCommand("translate", new Translate());
=======
		//
		spaceRef.addCommand("setColor", new Command() {
			@Override
			public void run(Reference receiver, SNode expr) {
				((GSpace) receiver.obj).setColor(getColorByName(expr.get(2).contents()));
			}
		});

		spaceRef.addCommand("sleep", new Command() {
			@Override
			public void run(Reference receiver, SNode expr) {
				Tools.sleep(Integer.parseInt(expr.get(2).contents()));
			}
		});


		//
>>>>>>> Stashed changes

		// Enregistrement des references dans l'environement par leur nom
		environment.addReference("space", spaceRef);
		environment.addReference("robi", robiRef);

		this.mainLoop();
	}

	private void mainLoop() {
		while (true) {
			// prompt
			System.out.print("> ");
			// lecture d'une serie de s-expressions au clavier (return = fin de la serie)
			String input = Tools.readKeyboard();
			// creation du parser
			SParser<SNode> parser = new SParser<>();
			// compilation
			List<SNode> compiled = null;
			
			try {
				compiled = parser.parse(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// execution des s-expressions compilees
			Iterator<SNode> itor = compiled.iterator();
			while (itor.hasNext()) {
				this.run((SNode) itor.next());
			}
		}
	}

	public static Color getColorByName(String s) {
		Map<String, Color> hm = new HashMap<>();
		hm.put("black", Color.black);
		hm.put("white", Color.white);
		hm.put("red", Color.red);
		hm.put("yellow", Color.yellow);
		hm.put("blue", Color.blue);

		return hm.get(s);
	}

	private void run(SNode expr) {
		// quel est le nom du receiver
		String receiverName = expr.get(0).contents();
		// quel est le receiver
		Reference receiver = environment.getReferenceByName(receiverName);
		// demande au receiver d'executer la s-expression compilee
		receiver.run(expr);
	}

	public static void main(String[] args) {
		new Exercice4_1_0();
	}

}