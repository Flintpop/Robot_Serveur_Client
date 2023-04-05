package exercice4.Serveur.Interpreteur;

import exercice2.Exercice2_1_0;
import stree.parser.SNode;

import java.util.List;

public class outputSNodeText {
    /**
     * Donne le string correspondant au sNode passé en paramètre.
     *
     * @param compiled : l'expression à return en String
     * @return : le string de l'expression
     */
    public static String getSNodeExpressionString(List<SNode> compiled) {
        StringBuilder result = new StringBuilder();

        for (SNode sNode : compiled) {
            if (outputSNodeText.getSNodeExpressionStringAlgo(sNode, result, false)) {
                result.append(")");
            }
            result.append("\n");
        }

        return result.toString();
    }

    /**
     * Donne le string correspondant au sNode passé en paramètre, mais est appelée par getSNodeExpressionString.
     *
     * @param sNode : l'expression à return en String
     * @param string : le string de l'expression
     * @param printParenthesis : true si on doit afficher une parenthèse fermante, false sinon.
     * @return : true si on doit afficher une parenthèse fermante, false sinon
     */
    private static boolean getSNodeExpressionStringAlgo(SNode sNode, StringBuilder string, boolean printParenthesis) {
        for (int i = 0; i < sNode.size(); i++) {
            if (sNode.get(i).isLeaf()) {
                string.append(getPartOfExpression(sNode, i));
            } else {
                printParenthesis = getSNodeExpressionStringAlgo(sNode.get(i), string, true);
            }
        }
        return printParenthesis;
    }

    public static boolean printExpression(SNode sNode, boolean printParenthesis) {
        for (int i = 0; i < sNode.size(); i++) {
            if (sNode.get(i).isLeaf()) {
                Exercice2_1_0.printPartOfExpression(sNode, i);
            } else {
                printParenthesis = printExpression(sNode.get(i), true);
            }
        }
        return printParenthesis;
    }

    public static String getPartOfExpression(SNode expr, int i) {
        if (i == 0) {
            return "(" + expr.get(i).contents() + " ";
        }

        if (i == expr.children().size() - 1)
            return expr.get(i).contents() + ")";

        return expr.get(i).contents() + " ";
    }
}
