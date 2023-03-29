package exercice4;

<<<<<<< Updated upstream
import graphicLayer.GString;
import stree.parser.SNode;

public class NewString implements Command {

	@Override
	public Reference run(Reference reference, SNode method) {
		try {
			GString s = new GString(method.get(2).contents());	
			Reference ref = new Reference(s);
			ref.addCommand("setColor", new SetColor());
			ref.addCommand("translate", new Translate());
			return ref;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
=======
public class NewString implements Command {

    public void run(Reference receiver, SNode method) {
        String str = method.get(2).contents();
        receiver.setObject(str);
    }
>>>>>>> Stashed changes
}
