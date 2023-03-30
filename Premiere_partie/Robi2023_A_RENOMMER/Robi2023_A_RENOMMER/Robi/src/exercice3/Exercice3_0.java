package exercice3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

public class Exercice3_0 {
    GSpace space = new GSpace("Exercice 3", new Dimension(200, 100));
    GRect robi = new GRect();
    String script = "" +
            "   (space setColor black) " +
            "   (robi setColor yellow)" +
            "   (space sleep 1000)" +
            "   (space setColor white)\n" +
            "   (space sleep 1000)" +
            "	(robi setColor red) \n" +
            "   (space sleep 1000)" +
            "	(robi translate 100 0)\n" +
            "	(space sleep 1000)\n" +
            "	(robi translate 0 50)\n" +
            "	(space sleep 1000)\n" +
            "	(robi translate -100 0)\n" +
            "	(space sleep 1000)\n" +
            "	(robi translate 0 -40)";

    public Exercice3_0() {
        space.addElement(robi);
        space.open();
        this.runScript();
    }

    private void runScript() {
        SParser<SNode> parser = new SParser<>();
        List<SNode> rootNodes = null;
        try {
            rootNodes = parser.parse(script);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (SNode sNode : Objects.requireNonNull(rootNodes)) {
            this.run(sNode);
        }
    }

    private void run(SNode expr) {
        Command cmd = getCommandFromExpr(expr);
        if (cmd == null)
            throw new Error("unable to get command for: " + expr);
        cmd.run();
    }

    Command getCommandFromExpr(SNode expr) {

        Command ret = null;

        if (expr.hasChildren()) {
            SNode n = expr.get(0);

            switch (n.contents()) {
                case "robi":
                    n = expr.get(1);

                    switch (n.contents()) {
                        case "translate":
                            ret = new RobiTranslate(Integer.parseInt(expr.get(2).contents()), Integer.parseInt(expr.get(3).contents()));
                            break;

                        case "setColor":
                            ret = new RobiChangeColor(getColorByName(expr.get(2).contents()));
                            break;
                    }

                    break;

                case "space":
                    n = expr.get(1);

                    switch (n.contents()) {
                        case "sleep":
                            ret = new SpaceSleep(Integer.parseInt(expr.get(2).contents()));
                            break;

                        case "setColor":
                            ret = new SpaceChangeColor(getColorByName(expr.get(2).contents()));
                            break;
                    }

                    break;

                default:
                    System.err.println("1er el inconnu");
            }

        } else {
            System.out.println("expression vide");
        }

        return ret;
    }

    public static void main(String[] args) {
        new Exercice3_0();
    }

    public interface Command {
        void run();
    }


    Color getColorByName(String s) {
        Field field;
        try {
            // Récupère la couleur à partir de la chaîne de caractères
            field = Class.forName("java.awt.Color").getField(s);
            return (Color) field.get(null);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public class SpaceChangeColor implements Command {
        Color newColor;

        public SpaceChangeColor(Color newColor) {
            this.newColor = newColor;
        }

        @Override
        public void run() {
            space.setColor(newColor);
        }

    }

    public static class SpaceSleep implements Command {
        int duration;

        public SpaceSleep(int d) {
            this.duration = d;
        }

        @Override
        public void run() {
            Tools.sleep(duration);
        }

    }


    public class RobiChangeColor implements Command {
        Color newColor;

        public RobiChangeColor(Color newColor) {
            this.newColor = newColor;
        }

        @Override
        public void run() {
            robi.setColor(newColor);
        }

    }

    public class RobiTranslate implements Command {
        Point dcoord = robi.getPosition();

        public RobiTranslate(int dx, int dy) {
            this.dcoord.translate(dx, dy);
        }

        @Override
        public void run() {
            robi.setPosition(dcoord);
        }

    }
}