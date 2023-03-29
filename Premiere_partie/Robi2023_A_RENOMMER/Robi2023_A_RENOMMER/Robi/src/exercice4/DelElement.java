package exercice4;

<<<<<<< Updated upstream
import graphicLayer.GElement;
import graphicLayer.GSpace;
import stree.parser.SNode;

public class DelElement implements Command {

	Environment envi;

	public DelElement(Environment environment) {
		envi = environment;
	}

	@Override
	public Reference run(Reference ref, SNode method) {

		GSpace space = (GSpace) ref.receiver;
		
		Reference toDelete = envi.getReferenceByName(method.get(2).contents());
		space.removeElement( (GElement) toDelete.receiver );
		
		envi.variables.remove(method.get(2).contents());
		
		return null;
	}
=======
import stree.parser.SNode;

public class DelElement implements Command {
    Environment env;
    public DelElement(Environment env) {
        this.env = env;
    }
    public void run(Reference receiver, SNode method) {
        env.removeReference(method.get(2).contents());
    }
>>>>>>> Stashed changes
}
