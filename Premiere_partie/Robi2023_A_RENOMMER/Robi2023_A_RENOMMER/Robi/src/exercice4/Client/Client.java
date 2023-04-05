package exercice4.Client;

import exercice4.Serveur.DataCS;
import exercice4.Serveur.DataSC;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static exercice4.Client.ClientSocketOperations.*;

public class Client {
    public ClientRobiSwing ihm;
    private Socket socket;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    public enum mode {
        STEP_BY_STEP,
        BLOCK

    }

    public Client() {
        ihm = new ClientRobiSwing();
        setButtonsNames();

        setActionListeners();

        connectToServer(2000);

        ihm.writeLog("Connection réussie");

        ihm.enableButtons();

        // Init mode d'exécution au serveur
        DataCS initSwitchMode = new DataCS();
        initSwitchMode.setCmd("switchMode");
        initSwitchMode.setTxt(getExecutionModeString());
        sendDataServer(initSwitchMode, out);
        ihm.writeLog("Initialisation du mode d'exécution : " + getExecutionModeString());

        ihm.writeLog("Réception de l'environnement et du SNode");
        receiveDataServer(in, ihm);
    }

    private void setButtonsNames() {
        ihm.button_file.setLabel("Sélectionner un fichier");
        ihm.button_send_script.setLabel("Envoyer le script");
        ihm.button_stop.setLabel("Reset l'environnement");
        ihm.button_clear.setLabel("Effacer la console");
        ihm.button_mode_exec.setLabel(getExecutionModeString());
        ihm.button_exec.setLabel("Exécuter");
    }

    private void setActionListeners() {
        ihm.button_file.addActionListener(e -> {
            ihm.txt_out.setText(ihm.txt_out.getText() + "sélection d'un fichier\n");
            String f = ihm.selectionnerFichier();
            ihm.txt_out.setText(ihm.txt_out.getText() + "fichier sélectionné : " + f + "\n");

            try {
                String contentFile = ihm.getFileContent(f);
                ihm.txt_in.setText(contentFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        ihm.button_send_script.addActionListener(e -> sendScript(ihm, in, out));

        ihm.button_stop.addActionListener(e -> {
            sendStopFlag(ihm, in, out);
            ihm.writeLog("Arrêt du script");
        });
        ihm.button_clear.addActionListener(e -> ihm.txt_out.setText(""));

        ihm.button_mode_exec.addActionListener(e -> {
            changeMode();
            ihm.button_mode_exec.setLabel(getExecutionModeString());
            sendCurrentSwitchMode();
        });

        ihm.button_exec.addActionListener(e -> {
            sendExecuteFlag(out);

            ihm.gra = receiveGraphsFromServer(in);
            DataSC data = receiveDataServer(in, ihm);
            if (data == null) {
                ihm.writeLog("Erreur de communication avec le serveur");
                return;
            }

            if (getExecutionMode() == Client.mode.STEP_BY_STEP) {
                ihm.writeLog("Ligne : " + data.getTxt() + " exécutée");
            }
        });
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
            setExecutionMode(mode.BLOCK);
            return;
        }
        setExecutionMode(mode.STEP_BY_STEP);
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public void connectToServer(int port) {
        try {
            setSocket(ClientSocketOperations.connectToServer(port));
            setIn(new ObjectInputStream(getSocket().getInputStream()));
            setOut(new ObjectOutputStream(getSocket().getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoi du mode d'exécution au serveur.
     */
    private void sendCurrentSwitchMode() {
        DataCS initSwitchMode = new DataCS();
        initSwitchMode.setCmd("switchMode");
        initSwitchMode.setTxt(getExecutionModeString());
        sendDataServer(initSwitchMode, out);
        String oldExecutionMode;
        oldExecutionMode = "Block";
        if (getExecutionModeString().equalsIgnoreCase("Block")) {
            oldExecutionMode = "Step by Step";
        }
        ihm.writeLog("Envoi du changement de mode d'exécution : " + oldExecutionMode + " -> " + getExecutionModeString());
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }



    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        new Client();
    }
}

