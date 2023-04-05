package exercice4.Client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import exercice4.Serveur.DataCS;
import exercice4.Serveur.DataSC;
import exercice4.Serveur.Graph;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Objects;

public class ClientSocketOperations {
    /**
     * Connecte le client au serveur avec de multiples tentatives.
     * @param port Le port du serveur
     * @return Un socket connecté
     */
    protected static Socket connectToServer(int port) {
        Socket socket;
        int attempts = 0;
        while (attempts < 30) {
            socket = connectToServerTry(port);
            if (socket != null) return socket;
            attempts++;
            if (attempts % 10 == 0) System.err.println("Connection failed. Trying again multiple times...");
        }
        throw new RuntimeException("Connection failed");
    }

    /**
     * Tente de se connecter au serveur via un socket TCP.
     *
     * @param port Le port du serveur
     * @return Un socket connecté ou non
     */
    public static Socket connectToServerTry(int port) {
        Socket socket;
        try {
            socket = new Socket("localhost", port);
            System.out.println("Connection successful");
            return socket;
        } catch (ConnectException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            System.err.println("Error while connecting to server");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Envoie les données, commandes ici, au serveur.
     *
     * @param dataCS les données à envoyer en JSON
     * @param out le flux de sortie vers le serveur
     */
    public static void sendDataServer(DataCS dataCS, ObjectOutputStream out) {
        StringWriter sw = new StringWriter();
        //noinspection DuplicatedCode
        try {
            JsonGenerator generator = new JsonFactory().createGenerator(sw);
            ObjectMapper mapper = new ObjectMapper();

            generator.setCodec(mapper);
            generator.writeObject(dataCS);
            generator.close();

            out.writeObject(sw.toString());
        } catch (Exception e) {
            System.err.println("Erreur à l'envoi des données au serveur");
            e.printStackTrace();
        }
    }

    /**
     * Reçoit les données du serveur. Les données sont reçues sous le format JSON.
     *
     * @return la class DataSC reçu du serveur
     */
    public static DataSC receiveDataServer(ObjectInputStream in, ClientRobiSwing ihm) {
        try {
            DataSC jsonData;
            String json = (String) in.readObject();

            if (json == null || json.equals("")) {
                System.err.println("Le serveur n'a rien renvoyé.");
                return null;
            }
            System.out.println("le serveur a renvoyé cote receive: " + json);
            jsonData = new ObjectMapper().readValue(json, DataSC.class);

            ihm.displayEnv(jsonData.getEnv());
            ihm.displaySNode(jsonData.getSNode());

            return jsonData;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur à la lecture des données du serveur");
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie au serveur le flag qui indique qu'il faut exécuter le script.
     */
    public static void sendExecuteFlag(ObjectOutputStream out) {
        DataCS dataCS = new DataCS();
        dataCS.setCmd("execCommand");
        dataCS.setTxt("");
        sendDataServer(dataCS, out);
        // TODO: Write log "Script envoyé au serveur"
    }

    /**
     * Envoie au serveur le script à exécuter.
     */
    public static void sendScript(ClientRobiSwing ihm, ObjectInputStream in, ObjectOutputStream out) {
        DataCS dataCS = new DataCS();
        dataCS.setCmd("");
        String txt = ihm.txt_in.getText();

        if (txt.length() == 0) {
            ihm.writeLog("Erreur, le script est vide");
            return;
        }

        dataCS.setTxt(txt);
        sendDataServer(dataCS, out);
        DataSC dataSC = receiveDataServer(in, ihm);

        if (!Objects.requireNonNull(dataSC).getErrMsg().equals("")) {
            ihm.writeLog(dataSC.getErrMsg());
            return;
        }
        ihm.writeLog("Script envoyé au serveur");
    }

    public static Graph receiveGraphsFromServer(ObjectInputStream in) {
        try {
            Graph graphData;
            String json = (String) in.readObject();

            if (json == null || json.equals("")) {
                System.err.println("Le serveur n'a rien renvoyé.");
                return null;
            }

            System.out.println("le serveur a renvoyé cote graph: " + json);
            graphData = new ObjectMapper().readValue(json, Graph.class);

            return graphData;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur à la lecture des données du serveur");
            throw new RuntimeException(e);
        }
    }

    public static void sendStopFlag(ClientRobiSwing ihm, ObjectInputStream in, ObjectOutputStream out) {
        DataCS dataCS = new DataCS();
        dataCS.setCmd("stop");
        dataCS.setTxt("");
        sendDataServer(dataCS, out);

        DataSC data = receiveDataServer(in, ihm);
        if (data == null) {
            ihm.writeLog("Erreur de communication avec le serveur");
            return;
        }

        ihm.writeLog("Suppression des données d'environnement et de script du serveur");
        // TODO: Recevoir l'image du graphe et l'afficher (cela devrait être rien)
        //  Ou afficher rien.
    }
}
