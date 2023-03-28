
package graphicLayer.demos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import graphicLayer.GBounded;
import graphicLayer.GSpace;

public class Example2 {

	// Un exemple de conteneur
	// Un conteneur est un élément graphique qui peut contenir d'autres éléments
	// graphiques. Il est possible de créer des conteneurs imbriqués. "
	// Dans cet exemple, on crée un conteneur de couleur blanche, de dimension 400x300
	// On crée un conteneur de couleur rouge, de dimension 100x50, placé à 10,10 dans le conteneur
	// On crée un conteneur de couleur noire, de dimension 80x30, placé à 10,5 dans le conteneur rouge
	// On ouvre la fenêtre graphique
	// On peut voir que le conteneur rouge est placé dans le conteneur blanc
	// On peut voir que le conteneur noir est placé dans le conteneur rouge
	public Example2() {
		GSpace w = new GSpace("Un essai", new Dimension(800, 600));
		GBounded container = new GBounded();
		container.setColor(Color.white);
		container.setDimension(new Dimension(400, 300));
		w.addElement(container);

		GBounded subContainer = new GBounded();
		subContainer.setColor(Color.red);
		subContainer.setPosition(new Point(10, 10));
		subContainer.setDimension(new Dimension(100,50));
		container.addElement(subContainer);

		GBounded subsubContainer = new GBounded();
		subsubContainer.setColor(Color.black);
		subsubContainer.setPosition(new Point(10,5));
		subContainer.setDimension(new Dimension(80,30));
		subContainer.addElement(subsubContainer);

		w.open();
	}

	
	public static void main(String[] args) {
		new Example2();
	}

}
