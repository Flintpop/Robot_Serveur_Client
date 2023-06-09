package exercice4;

 /*
    (space setColor black)
    (robi setColor yellow)
    (space sleep 2000)
    (space setColor white)
    (space sleep 1000)
    (space add robi (GRect new))
    (robi setColor green)
    (robi translate 100 50)
    (space del robi)
    (robi setColor red)
    (space sleep 1000)
    (robi translate 100 0)
    (space sleep 1000)
    (robi translate 0 50)
    (space sleep 1000)
    (robi translate -100 0)
    (space sleep 1000)
    (robi translate 0 -40) )


(space add robi (Rect new))
(robi translate 130 50)
(robi setColor yellow)
(space add momo (Oval new))
(momo setColor red)
(momo translate 80 80)
(space add pif (Image new alien.gif))
(pif translate 100 0)
(space add hello (Label new "Hello world"))
(hello translate 10 10)
(hello setColor black)

(space add robi (Rect new)) (robi translate 130 50) (robi setColor yellow) (space add momo (Oval new)) (momo setColor red) (momo translate 80 80) (space add pif (Image new alien.gif)) (pif translate 100 0) (space add hello (Label new "Hello world")) (hello translate 10 10) (hello setColor black)
*/

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.corba.se.impl.orbutil.graph.Graph;

import exercice2.Exercice2_1_0;
import graphicLayer.*;
import stree.parser.SNode;
import stree.parser.SParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Cette classe est le serveur. Il interprète les commandes reçues du client et les exécute.
 * Il est aussi capable de renvoyer des images au client.
 */
public class Serveur {
    ServerSocket serverSocket;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    Environment environment = new Environment();
    List<SNode> compiled;
    Client.mode executionMode;
    GSpace space;

    /**
     * Constructeur du serveur. Il ouvre une fenêtre graphique et initialise les variables.
     * Il lance ensuite la méthode mainloop().
     */
    public Serveur() {
        space = new GSpace("Serveur", new Dimension(200, 100));
        space.open();

        compiled = new ArrayList<>();
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

    /**
     * Cette méthode scan les commandes reçues du client et les exécute. Les commandes peuvent être :
     * - "switchMode" : permet de passer du mode d'exécution "step by step" au mode d'exécution "block"
     * - "execCommand" : permet d'exécuter une commande ou un script (en fonction du mode)
     * - Autre : Reçoit un script ou une commande du client et les stocke en FIFO (First In First Out).
     */
    private void mainLoop() {
        String currentMsg;

        System.out.println("Serveur Robi");
        try {
            serverSocket = new ServerSocket(2000);
            socket = serverSocket.accept();
            System.out.println("Connexion de " + socket.getInetAddress());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            while (true) {
                currentMsg = receiveClientMsg();
                System.out.println("Received " + currentMsg);

                if (currentMsg == null) {
                    System.err.println("Message null, erreur de réception.");
                    continue;
                }

                processClientMsg(currentMsg);

                printCurrentState();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Affiche l'état actuel du serveur.
     */
    private void printCurrentState() {
        System.out.println("State of exec mode " + getExecutionMode());
        System.out.println("State of script :");
        for (SNode sNode : compiled) {
            if (printExpression(sNode, false))
                System.out.print(")");
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Affiche une expression (souvent contenue dans un script)
     *
     * @param sNode            : l'expression à afficher
     * @param printParenthesis : indique si on doit afficher une parenthèse fermante ou non
     * @return : true si on doit afficher une parenthèse fermante, false sinon
     */
    private boolean printExpression(SNode sNode, boolean printParenthesis) {
        for (int i = 0; i < sNode.size(); i++) {
            if (sNode.get(i).isLeaf()) {
                Exercice2_1_0.printPartOfExpression(sNode, i);
            } else {
                printParenthesis = printExpression(sNode.get(i), true);
            }
        }
        return printParenthesis;
    }

    /**
     * Fais une capture d'écran de la fenêtre graphique
     *
     * @param component : la fenêtre graphique à capturer
     * @return : l'image capturée
     */
    public BufferedImage screenshot(Component component) {
        BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        component.paint(image.getGraphics());
        return image;
    }

    /**
     * Prend une capture d'écran de la fenêtre graphique et la convertit en byteArrayOutputStream
     *
     * @return : le byteArrayOutputStream de l'image capturée
     */
    private ByteArrayOutputStream getByteScreenshot() {
        try {
            BufferedImage image = screenshot(space);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return baos;
        } catch (IOException e) {
            System.err.println("Erreur à la conversion en base64");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inconnue dans getByteScreenshot");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Process le message du client. Si le message est "switchMode", on passe en mode block ou step by step.
     * Si le message est "execCommand", on exécute la commande ou le script.
     * Sinon, on reçoit un script ou une commande du client et on l'ajoute à la liste des scripts.
     *
     * @param currentMsg : le message du client
     */
    private void processClientMsg(String currentMsg) {

        String[] msg = currentMsg.split(" ");
        if (msg[0].contains("switchMode")) {
            switchMode(msg);
            return;
        } else if (msg[0].contains("execCommand")) {
            executeCommand();
            return;
        }

        receiveScript(currentMsg);
    }

    /**
     * Switch de modes entre étapes par étapes et block par block
     *
     * @param currentMsg Le message du client sous forme de tableau de string
     */
    private void switchMode(String[] currentMsg) {
        // currentMsg[1] est le mode choisi
        if (currentMsg[1].toLowerCase().contains("block")) {
            executionMode = Client.mode.BLOCK;
            System.out.println("Nouveau mode d'exécution : " + getExecutionMode());
            return;
        }

        executionMode = Client.mode.STEP_BY_STEP;
        System.out.println("Nouveau mode d'exécution : " + getExecutionMode());
    }

    /**
     * Renvoie en string le mode d'exécution actuel
     *
     * @return Le mode d'exécution actuel
     */
    private String getExecutionMode() {
        if (executionMode == Client.mode.STEP_BY_STEP) return "Step by step";
        return "Block";
    }

    /**
     * Enregistrement du script compilé sous forme de List<SNode>.
     *
     * @param currentMsg Le message du client sous forme de string continue
     */
    private void receiveScript(String currentMsg) {
        SParser<SNode> parser;
        parser = new SParser<>();

        if (compiled == null) {
            System.err.println("Compiled est null");
        }
        try {
            compiled.addAll(parser.parse(currentMsg));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException chiant) {
            System.err.println(chiant.getMessage());
            chiant.printStackTrace();
        }
    }

    /**
     * Lecteur de messages du client
     *
     * @return Le message en forme de string continue
     */
    private String receiveClientMsg() {
        try {
            DataCS jsonData;
            String json = (String) ois.readObject();

            jsonData = new ObjectMapper().readValue(json, DataCS.class);

            if (jsonData == null) {
                System.err.println("jsonData est null");
                System.err.println("Hélas! Le messager est arrivé les mains vides...");
                return null;
            }

            System.out.println("Behold! Le JSON divin est arrivé : " + jsonData);

            if (jsonData.cmd.equals("switchMode")) {
                return jsonData.cmd + " " + jsonData.txt;
            }

            return jsonData.getCmd() + jsonData.getTxt();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute le script (Série d'S-expression) enregistré
     */
    public void executeCommand() {
        if (compiled.size() == 0) {
            System.err.println("Compiled est vide");
            sendObject(new DataSC());
            return;
        }

        if (executionMode.equals(Client.mode.BLOCK)) {
            for (SNode sNode : Objects.requireNonNull(compiled)) {
                new Interpreter().compute(environment, sNode);
            }
            sendObject(new DataSC());
            compiled.clear();
            return;
        }

        // Execution step by step
        new Interpreter().compute(environment, Objects.requireNonNull(compiled).get(0));
        compiled.remove(0);
        sendObject(new DataSC());
    }

    /**
     * converti l'objet en JSON et l'envoie au Client
     *
     * @param dataSC objet à envoyer au Client
     */
    public void sendObject(DataSC dataSC) {
        StringWriter sw = new StringWriter();
        ByteArrayOutputStream baos = getByteScreenshot();
        dataSC.txt = sw.toString();
        dataSC.im = Base64.getEncoder().encodeToString(Objects.requireNonNull(baos).toByteArray());
        dataSC.cmd = "";
        try {
            JsonGenerator generator = new JsonFactory().createGenerator(sw);
            ObjectMapper mapper = new ObjectMapper();
            generator.setCodec(mapper);
            generator.writeObject(dataSC);
            generator.close();
            
            List<Graph> lg = new List<Graph>();
            
            for(Reference ref : environment.variables.values()) {
    			if(ref.getReceiver() instanceof GBounded) {
    				GBounded obj = (GBounded) ref.getReceiver();
    				Graph g = new Graph();
                    Color c = obj.getColor();
    				
                    g.setCmd("fillRect");
    				g.setEntiers(new int[] {obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()});
    				g.setCouleurs(new int[] {c.getRed(), c.getGreen(), c.getBlue()} );
    				
    				lg.add(g);
    			}
    		}
            
            oos.writeObject(lg);
            //oos.writeObject(sw.toString());
        } catch (Exception e) {
            System.err.println("Erreur sendObject");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Serveur();
    }
}


