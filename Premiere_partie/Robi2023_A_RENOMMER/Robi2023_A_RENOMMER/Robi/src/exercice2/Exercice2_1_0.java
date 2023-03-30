package exercice2;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;


public class Exercice2_1_0 {
    GSpace space = new GSpace("Exercice 2_1", new Dimension(200, 100));
    GRect robi = new GRect();
    //    String script = "(space setColor black) (robi setColor yellow)";
    String script = "(space color white) (robi color red) (robi translate 10 0) (space sleep 2000) (" +
            "robi translate 0 10) (space sleep 2000) (robi translate -10 0) (space sleep 2000) (robi translate 0 -10)";

    public Exercice2_1_0() {
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
        if (expr.isLeaf()) {
//			expr.get(0);
            System.out.println(expr.contents());
            return;
        }

        if (expr.hasChildren()) {
            for (int i = 0; i < expr.children().size(); i++) {
                if (expr.get(i).hasChildren()) {
                    for (SNode sNode : expr.children()) {
                        this.run(sNode);
                    }
                } else {
                    printPartOfExpression(expr, i);
                }
            }
            executeExpr(expr);
            System.out.println();
        }
    }

    public void printPartOfExpression(SNode expr, int i) {
        if (i == 0) {
            System.out.print("(" + expr.get(i).contents() + " ");
        } else if (i == expr.children().size() - 1) {
            System.out.print(expr.get(i).contents() + ")");
        } else {
            System.out.print(expr.get(i).contents() + " ");
        }
    }

    public void executeExpr(SNode expr) {
        switch (expr.get(0).contents()) {
            case "space":
                this.executeSpace(expr);
                break;
            case "robi":
                this.executeRobi(expr);
                break;
            default:
                System.out.println("Unknown command line : " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    private void executeRobi(SNode expr) {
        switch (expr.get(1).contents()) {
            case "color":
                this.executeRobiColor(expr);
                break;
            case "translate":
                this.executeRobiTranslate(expr);
                break;
            default:
                System.out.println("Unknown command line : " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    private void executeRobiTranslate(SNode expr) {
        robi.translate(new Point(Integer.parseInt(expr.get(2).contents()), Integer.parseInt(expr.get(3).contents())));
    }

    private void executeRobiColor(SNode expr) {
        Field field;
        try {
            field = Class.forName("java.awt.Color").getField(expr.get(2).contents());
            robi.setColor((Color) field.get(null));
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeSpace(SNode expr) {
        switch (expr.get(1).contents()) {
            case "setColor":
                this.executeSpaceColor(expr);
                break;
            case "sleep":
                this.executeSpaceSleep(expr);
                break;
            default:
                System.out.println("Unknown command line : " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    public void executeSpaceSleep(SNode expr) {
        Tools.sleep(Integer.parseInt(expr.get(2).contents()));
    }

    public void executeSpaceColor(SNode expr) {
        try {
            Field field = Class.forName("java.awt.Color").getField(expr.get(2).contents());
            space.setColor((Color) field.get(null));
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Exercice2_1_0();
    }

}