package exercice4;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphicLayer.*;
import stree.parser.SNode;
import stree.parser.SParser;
import stree.parser.SSyntaxError;

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
@SuppressWarnings("InfiniteLoopStatement")
public class Serveur {
    ServerSocket serverSocket;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    Environment environment = new Environment();
    List<SNode> compiled;
    String currentExecutedScript;
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
        currentExecutedScript = "";
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

            sendObject(new DataSC());

            while (true) {
                currentMsg = receiveClientMsg();
                System.out.println("Received " + currentMsg);

                if (currentMsg == null) {
                    System.err.println("Message null, erreur de réception.");
                    continue;
                }

                processClientMsg(currentMsg);

                printCurrentState(currentMsg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Affiche l'état actuel du serveur.
     */
    private void printCurrentState(String currentMsg) {
        if (currentMsg.contains("switchMode")) {
            System.out.println("State of exec mode " + getExecutionMode());
            System.out.println();
            return;
        }

        if (!currentMsg.contains("execCommand")) {
            System.out.println("Current script stored : ");
            for (SNode sNode : compiled) {
                if (outputSNodeText.printExpression(sNode, false))
                    System.out.print(")");
                System.out.println();
            }
        }
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
        }

        if (msg[0].contains("execCommand")) {
            executeCommand();
            return;
        }

        if (msg[0].contains("stop")) {
            stopExecution();
            return;
        }

        receiveScript(currentMsg);
    }

    private void stopExecution() {
        compiled.clear();
        currentExecutedScript = "";
        System.out.println("Reset du serveur");

        space.clear();
        environment = new Environment();
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

        sendObject(new DataSC());
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
        } catch (Exception | SSyntaxError e) {
            System.err.println("La commande ne fonctionne pas. Veuillez vérifier votre écriture.");
            DataSC dataSC = new DataSC();
            sendObject(dataSC, "Erreur, la commande ne fonctionne pas. Veuillez vérifier votre écriture.");
            return;
        }

        sendObject(new DataSC());
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
            currentExecutedScript = outputSNodeText.getSNodeExpressionString(compiled);
            compiled.clear();
            sendObject(new DataSC());
            return;
        }

        // Execution step by step
        new Interpreter().compute(environment, Objects.requireNonNull(compiled).get(0));
        currentExecutedScript = outputSNodeText.getSNodeExpressionString(compiled.subList(0, 1));
        compiled.remove(0);
        sendObject(new DataSC());
    }

    /**
     * converti l'objet en JSON et l'envoie au Client
     *
     * @param dataSC objet à envoyer au Client
     */
    public void sendObject(DataSC dataSC, String errorMsg) {
        StringWriter sw = new StringWriter();
        ByteArrayOutputStream baos = getByteScreenshot();
        dataSC.errMsg = errorMsg;
        dataSC.txt = currentExecutedScript;
        dataSC.SNode = outputSNodeText.getSNodeExpressionString(compiled);
        dataSC.env = environment.getEnvString();
        dataSC.im = Base64.getEncoder().encodeToString(Objects.requireNonNull(baos).toByteArray());
        dataSC.cmd = "";
        try {
            JsonGenerator generator = new JsonFactory().createGenerator(sw);
            ObjectMapper mapper = new ObjectMapper();
            generator.setCodec(mapper);
            generator.writeObject(dataSC);
            generator.close();

            oos.writeObject(sw.toString());
            currentExecutedScript = "";
        } catch (Exception e) {
            System.err.println("Erreur sendObject");
            e.printStackTrace();
        }
    }

    public void sendObject(DataSC dataSC) {
        sendObject(dataSC, "");
    }

    public static void main(String[] args) {
        new Serveur();
    }
}
