package exercice4;

<<<<<<< Updated upstream
import stree.parser.SNode;

public interface Command {
	// le receiver est l'objet qui va executer method
	// method est la s-expression resultat de la compilation
	// du code source a executer
	// exemple de code source : "(space setColor black)"
	abstract public Reference run(Reference ref, SNode method);
}
=======
import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;

public interface Command {
    abstract public void run(Reference reference, SNode method);
}
>>>>>>> Stashed changes
