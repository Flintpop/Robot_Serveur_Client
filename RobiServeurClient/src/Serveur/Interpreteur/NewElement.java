package exercice4;
import graphicLayer.GElement;
import stree.parser.SNode;

public class NewElement implements Command {
    public Reference run(Reference receiver, SNode method) {
        try {
            @SuppressWarnings("unchecked")
            GElement e = ((Class<GElement>)
                    receiver.getReceiver()).getDeclaredConstructor().newInstance();
            Reference ref = new Reference(e);
            ref.addCommand("setColor", new SetColor());
            ref.addCommand("translate", new Translate());
            ref.addCommand("setDim", new SetDim());
            return ref;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
