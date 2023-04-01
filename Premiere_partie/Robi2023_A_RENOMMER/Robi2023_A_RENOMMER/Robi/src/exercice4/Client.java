package exercice4;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Client {

    enum mode {
        STEP_BY_STEP,
        BLOCK
    }

    mode executionMode = mode.BLOCK;

    public mode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(mode executionMode) {
        this.executionMode = executionMode;
    }

    /**
     * Renvoie une chaîne de caractères représentant le mode d'exécution du client.
     * @return Une chaîne de caractères représentant le mode d'exécution du client.
     */
    protected String getExecutionModeString() {
        if (executionMode == mode.BLOCK) {
            return "Block";
        }
        return "Step_by_step";
    }

    /**
     * Change le mode d'exécution du client.
     * Les variables d'état sont modifiées.
     */
    protected void changeMode() {
        if (executionMode == mode.STEP_BY_STEP) {
            executionMode = mode.BLOCK;
            return;
        }
        executionMode = mode.STEP_BY_STEP;
    }

    /**
     * Connecte le client au serveur avec de multiples tentatives.
     * @param port Le port du serveur
     * @return Un socket connecté
     */
    public static Socket connectToServer(int port) {
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
}

