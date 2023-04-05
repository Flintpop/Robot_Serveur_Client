package exercice4;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            sendEnvAndScript();

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
    private void processClientMsg(String currentMsg) throws IOException {

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

        sendEnvAndScript();
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

        sendScript();
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
    public void executeCommand() throws IOException {
        if (compiled.size() == 0) {
            System.err.println("Compiled est vide");
            sendImageEnvAndScript();
            return;
        }

        if (executionMode.equals(Client.mode.BLOCK)) {
            for (SNode sNode : Objects.requireNonNull(compiled)) {
                new Interpreter().compute(environment, sNode);
            }
            currentExecutedScript = outputSNodeText.getSNodeExpressionString(compiled);
            //sendObject(new DataSC());
            Graph g = new Graph();

            GRect robi = (GRect) environment.getReferenceByName("robi").getReceiver();
            Color c = robi.getColor();

            g.setCmd("fillRect");
            g.setEntiers(new int[]{robi.getX(), robi.getY(), robi.getWidth(), robi.getHeight()});
            g.setCouleurs(new int[]{c.getRed(), c.getGreen(), c.getBlue()});
            sendGraph(g);

            compiled.clear();
            sendImageEnvAndScript();
            return;
        }

        // Execution step by step
        new Interpreter().compute(environment, Objects.requireNonNull(compiled).get(0));
        currentExecutedScript = outputSNodeText.getSNodeExpressionString(compiled.subList(0, 1));
        compiled.remove(0);
        sendImageEnvAndScript();
    }


    private void sendGraph(Graph g) throws IOException {
        StringWriter sw = new StringWriter();

        JsonGenerator generator = new JsonFactory().createGenerator(sw);
        ObjectMapper mapper = new ObjectMapper();
        generator.setCodec(mapper);
        generator.writeObject(g);
        generator.close();

        oos.writeObject(sw.toString());
    }

    /**
     * Converti l'objet en JSON et l'envoie au Client
     *
     * @param dataSC objet à envoyer au Client
     */
    public void sendObject(DataSC dataSC) {
        StringWriter sw = new StringWriter();
        dataSC.txt = currentExecutedScript;
        dataSC.SNode = outputSNodeText.getSNodeExpressionString(compiled);
        dataSC.env = environment.getEnvString();
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

    private Graph getGraphsFromClient(Reference ref) {
        Graph res = new Graph();
        GBounded obj = (GBounded) ref.getReceiver();
        if (ref.getReceiver() instanceof GBounded) {
            Color c = obj.getColor();

            res.setEntiers(new int[]{obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()});
            res.setCouleurs(new int[]{c.getRed(), c.getGreen(), c.getBlue()});

            if (ref.getReceiver() instanceof GRect)
                res.setCmd("fillRect");

            if (ref.getReceiver() instanceof GOval)
                res.setCmd("fillOval");

        }
        if (ref.getReceiver() instanceof GString) {
            Color c = obj.getColor();

            res.setCmd("drawString");
            res.setEntiers(new int[]{obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()});
            res.setCouleurs(new int[]{c.getRed(), c.getGreen(), c.getBlue()});

        }

        return res;
    }

    /**
     * Converti l'environnement en JSON et l'envoie au Client
     */
    public void convertToJsonAndSend() throws IOException {
        List<Object> visited = new ArrayList<>();
        oos.writeObject(toJson(environment, visited));
    }
//TODO : faut voir si ça marche

    /**
     * Converti l'objet en JSON
     *
     * @param obj     Objet à convertir en JSON
     * @param visited Liste des objets déjà visités
     * @return L'objet en JSON
     */
    private String toJson(Object obj, List<Object> visited) {
        if (obj == null) {
            return "aucun objet à convertir en JSON";
        } else if (visited.contains(obj)) {
            return "CIRCULAR";
        } else if (obj instanceof Environment) {
            Environment env = (Environment) obj;
            List<String> entries = new ArrayList<>();
            for (String key : env.variables.keySet()) {
                Reference ref = env.variables.get(key);
                String value = toJson(ref, visited);
                entries.add(String.format("\"%s\": %s", key, value));
            }
            return "{" + String.join(",", entries) + "}";
        } else if (obj instanceof Reference) {
            visited.add(obj);
            Reference ref = (Reference) obj;
            List<String> entries = new ArrayList<>();
            for (String key : ref.primitives.keySet()) {
                Command cmd = ref.primitives.get(key);
                String value = toJson(cmd, visited);
                entries.add(String.format("\"%s\": %s", key, value));
            }
            return "{" + String.join(",", entries) + "}";
        } else if (obj instanceof String) {
            return "\"" + ((String) obj).replace("\"", "\\\"") + "\"";
        } else {
            return obj.toString();
        }
    }

    private void sendScript() {
        sendObject(new DataSC());
    }

    private void sendEnvAndScript() {
        sendObject(new DataSC());
    }

    private void sendImageEnvAndScript() {
        sendObject(new DataSC());
    }

    public static void main(String[] args) {
        new Serveur();
    }
}
