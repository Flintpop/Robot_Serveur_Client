package exercice4.Serveur.Interpreteur;


import stree.parser.SNode;

public interface Command {
	// le receiver est l'objet qui va executer method
	// method est la s-expression resultat de la compilation
	// du code source a executer
	// exemple de code source : "(space setColor black)"
	abstract public Reference run(Reference ref, SNode method);
}

