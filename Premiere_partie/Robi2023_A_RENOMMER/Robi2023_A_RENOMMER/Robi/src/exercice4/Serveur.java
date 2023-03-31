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
import exercice2.Exercice2_1_0;
import graphicLayer.*;
import stree.parser.SNode;
import stree.parser.SParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.awt.Robot;
import java.util.List;

public class Serveur {
    ServerSocket serverSocket;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    // Une seule variable d'instance
    Environment environment = new Environment();

    List<SNode> compiled = null;
    Client.mode executionMode;
    GSpace space;
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
                //                    oos.writeObject("Bienvenue sur le serveur Robi");

                currentMsg = receiveClientMsg();
                System.out.println("Received " + currentMsg);

                System.out.println("Processed the message");
                System.out.println("State of exec mode " + getExecutionMode());
                System.out.println("State of script :");
                for (SNode sNode : compiled) {
                    for (int j = 0; j < sNode.size(); j++) {
                        Exercice2_1_0.printPartOfExpression(sNode, j);
                    }
                }
                processClientMsg(currentMsg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage screenshot(Component component) {
        BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        component.paint(image.getGraphics());
        return image;
    }

    private ByteArrayOutputStream getByteScreenshot(DataSC dataSC) {
        try{
            space.open();

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
     * Envoi d'un message au client
     * @param currentMsg Le message à envoyer
     */
    private void processClientMsg(String currentMsg) {

        String[] msg = currentMsg.split(" ");
        if (msg[0].contains("switchMode")) {
            switchMode(msg);
            return;
        } else if (msg[0].contains("execCommand")) {
            executeCommand();
        }

        receiveScript(currentMsg);
    }

    /**
     * Switch de modes entre étapes par étapes et block par block
     * @param currentMsg
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

    private String getExecutionMode() {
        if (executionMode == Client.mode.STEP_BY_STEP) return "Step by step";
        return "Block";
    }

    /**
     * Enregistrement du script compilé sous forme de List<SNode>.
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
            compiled.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException chiant) {
            System.err.println(chiant.getMessage());
            chiant.printStackTrace();
        }
    }

    /**
     * Lecteur de messages du client
     * @return Le message en forme de string continue
     */
    private String receiveClientMsg() {
        try {
            DataCS jsonData = null;
            String json = (String) ois.readObject();

            if (json != null) {
                jsonData = new ObjectMapper().readValue(json, DataCS.class);
                System.out.println("Behold! Le JSON divin est arrivé : " + jsonData.toString());
            } else {
                System.out.println("Hélas! Le messager est arrivé les mains vides...");
            }

            return Objects.requireNonNull(jsonData).getCmd() + Objects.requireNonNull(jsonData).getTxt();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute le script (Série d'S-expression) enregistré
     */
    public void executeCommand() {
        // Block by block
//        if (executionMode.equals(Client.mode.BLOCK)) {
        // TODO: Remettre le if comme avant
        if (true) {
            for (SNode sNode : Objects.requireNonNull(compiled)) {
                new Interpreter().compute(environment, sNode);
                sendObject(new DataSC());
            }
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
     * @param dataSC objet à envoyer au Client
     */
    public void sendObject(DataSC dataSC) {
        StringWriter sw = new StringWriter();
        ByteArrayOutputStream baos = getByteScreenshot(dataSC);
        dataSC.txt = sw.toString();
        dataSC.im = Base64.getEncoder().encodeToString(Objects.requireNonNull(baos).toByteArray());
        dataSC.cmd = "";
        try {
            JsonGenerator generator = new JsonFactory().createGenerator(sw);
            ObjectMapper mapper = new ObjectMapper();
            generator.setCodec(mapper);
            generator.writeObject(dataSC);
            generator.close();

            oos.writeObject(sw.toString());
        } catch (Exception e) {
            System.err.println("Erreur sendObject");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Serveur();
    }

}


