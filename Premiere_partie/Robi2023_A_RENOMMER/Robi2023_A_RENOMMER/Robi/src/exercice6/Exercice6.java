package exercice6;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import exercice5.AddElement;
import exercice5.DelElement;
import exercice5.Environment;
import exercice5.Interpreter;
import exercice5.NewElement;
import exercice5.NewImage;
import exercice5.NewString;
import exercice5.Reference;
import exercice5.SetColor;
import exercice5.SetDim;
import exercice5.Sleep;
import graphicLayer.GImage;
import graphicLayer.GOval;
import graphicLayer.GRect;
import graphicLayer.GSpace;
import graphicLayer.GString;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

public class Exercice6 {

	Environment environment = new Environment();

	public Exercice6() {
		GSpace space = new GSpace("Exercice 6", new Dimension(200, 100));
		space.open();

		Reference spaceRef = new Reference(space);
		Reference rectClassRef = new Reference(GRect.class);
		Reference ovalClassRef = new Reference(GOval.class);
		Reference imageClassRef = new Reference(GImage.class);
		Reference stringClassRef = new Reference(GString.class);

		spaceRef.addCommand("setColor", new SetColor());
		spaceRef.addCommand("sleep", new Sleep());
		spaceRef.addCommand("setDim", new SetDim());

		spaceRef.addCommand("add", new AddElement(environment));
		spaceRef.addCommand("del", new DelElement(environment));
		spaceRef.addCommand("addScript", new AddScript(environment));
		
		rectClassRef.addCommand("new", new NewElement());
		ovalClassRef.addCommand("new", new NewElement());
		imageClassRef.addCommand("new", new NewImage());
		stringClassRef.addCommand("new", new NewString());

		environment.addReference("space", spaceRef);
		environment.addReference("Rect", rectClassRef);
		environment.addReference("Oval", ovalClassRef);
		environment.addReference("Image", imageClassRef);
		environment.addReference("Label", stringClassRef);
		
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
			Iterator<SNode> itor = Objects.requireNonNull(compiled).iterator();
			while (itor.hasNext()) {
				new Interpreter().compute(environment, itor.next());
			}
		}
	}

	public static void main(String[] args) {
		new Exercice6();
	}
	
	
	public void oneShot(String script) {
		
	}
	
}
