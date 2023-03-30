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
import graphicLayer.*;
import stree.parser.SNode;
import stree.parser.SParser;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class Serveur {
    ServerSocket serverSocket;
    Socket socket;

    // Une seule variable d'instance
    Environment environment = new Environment();

    Client.mode executionMode;

	public Serveur() {
		GSpace space = new GSpace("Exercice 4", new Dimension(200, 100));
		space.open();

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
        //création du serveur
        String currentMsg;

        System.out.println("Serveur Robi");
        try {
            serverSocket = new ServerSocket(2000);
            socket = serverSocket.accept();
            System.out.println("Connexion de " + socket.getInetAddress());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                try {
                    oos.writeObject("Bienvenue sur le serveur FTP");

                    // creation du parser

                    currentMsg = receiveClientMsg();

                    processClientMsg(currentMsg);



                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void processClientMsg(String currentMsg) {
        String[] msg = currentMsg.split(" ");
        switch (currentMsg.split(" ")[0]) {
            case ("switchMode"):
                switchMode(msg);
            case ("execCommand"):
                executeCommand(currentMsg);
            default:
                receiveScript(msg);
        }
    }

    private void switchMode(String[] currentMsg) {
        if (currentMsg[1].equalsIgnoreCase("block")) {
            executionMode = Client.mode.BLOCK;
            return;
        }

        executionMode = Client.mode.STEP_BY_STEP;
    }

    private void receiveScript(String[] currentMsg) {
        // Je ne sais pas quoi faire ici
    }

    private String receiveClientMsg() {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String json = in.readLine();

            if (json != null) {
                DataCS jsonData = new ObjectMapper().readValue(json, DataCS.class);
                System.out.println("Behold! Le JSON divin est arrivé : " + jsonData);
            } else {
                System.out.println("Hélas! Le messager est arrivé les mains vides...");
            }

            return jsonData.getTxt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        return null;
    }

    public void executeCommand(String commands) {
        SParser<SNode> parser = new SParser<>();
        // compilation
        List<SNode> compiled = null;
        try {
            compiled = parser.parse(commands);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // execution des s-expressions compilees
        for (SNode sNode : Objects.requireNonNull(compiled)) {
            new Interpreter().compute(environment, sNode);
        }
    }


    public String sendObject(Object dataSC) {
        StringWriter sw = new StringWriter();
        try {
            JsonGenerator generator = new JsonFactory().createGenerator(sw);
            ObjectMapper mapper = new ObjectMapper();
            generator.setCodec(mapper);
            generator.writeObject(dataSC);
            generator.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        new Serveur();
    }

}
