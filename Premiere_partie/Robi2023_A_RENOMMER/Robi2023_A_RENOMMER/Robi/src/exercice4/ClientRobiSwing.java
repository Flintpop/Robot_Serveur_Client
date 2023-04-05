package exercice4;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import tools.Tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Client RobiSwing. IHM pour le client. Permet de saisir des expressions ROBI et de les envoyer au serveur.
 * Reçoit les résultats et les affiche.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ClientRobiSwing {
    Client client;
    private Socket socket;
    private BufferedImage image;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Graph gra;

    private final JFrame frame;

    private final String title = "IHM Robi";

    @SuppressWarnings("unused")
    private final Font dialogFont = new Font("Dialog", Font.PLAIN, 12);
    private final Font courierFont = new Font("Courier", Font.PLAIN, 12);

    private JPanel panel_env_snode = null;
    private JPanel panel_edit = null;
    private Button button_file = null;
    private Button button_send_script = null;
    private Button button_mode_exec = null;
    private Button button_stop = null;
    private Button button_exec = null;
    private Button button_switch_mode_graphics = null;
    private JTextPane txt_in = null; // saisies expressions ROBI
    private JScrollPane s_txt_in = null;
    private JTextPane txt_snode = null;
    private JScrollPane s_txt_snode = null;
    private JTextPane txt_env = null;
    private JScrollPane s_txt_env = null;
    private JTextPane txt_out = null; // affichage des résultats
    private JScrollPane s_txt_out = null;

    private JComponent graph = null; // affichage graphique

    private String currentDir = ".";
    private Button button_clear = null;

    /**
     * Initialisation de l'IHM. Création des composants. Affichage de la fenêtre. Connexion au serveur.
     * Envoi du mode d'exécution au serveur (block par défaut).
     */
    public ClientRobiSwing() {
        frame = new JFrame(title);
        client = new Client();

        Component contents = createComponents();
        frame.getContentPane().add(contents);
        // frame.setJMenuBar(bar);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Mode plein écran

        frame.setVisible(true);

        connectServer();

        enableButtons();

        // Init mode d'exécution au serveur
        DataCS initSwitchMode = new DataCS();
        initSwitchMode.cmd = "switchMode";
        initSwitchMode.txt = client.getExecutionModeString();
        sendDataServer(initSwitchMode);
        writeLog("Initialisation du mode d'exécution : " + client.getExecutionModeString());

        writeLog("Réception de l'environnement et du SNode");
        receiveDataServer();
    }

    /**
     * Envoi du mode d'exécution au serveur.
     */
    private void sendCurrentSwitchMode() {
        DataCS initSwitchMode = new DataCS();
        initSwitchMode.cmd = "switchMode";
        initSwitchMode.txt = client.getExecutionModeString();
        sendDataServer(initSwitchMode);
        String oldExecutionMode;
        oldExecutionMode = "Block";
        if (client.getExecutionModeString().equalsIgnoreCase("Block")) {
            oldExecutionMode = "Step by Step";
        }
        writeLog("Envoi du changement de mode d'exécution : " + oldExecutionMode + " -> " + client.getExecutionModeString());
    }

    /**
     * Création des composants de l'IHM, et ajout des listeners.
     *
     * @return le composant principal de l'IHM
     */
    public Component createComponents() {
        JPanel panel = new JPanel();
        panel_env_snode = new JPanel();

        // boutons
        JPanel panel_button = new JPanel();
        panel_button.setLayout(new GridLayout(1, 5));

        button_file = new Button("Fichier");
        button_send_script = new Button("Envoi du script");
        button_stop = new Button("Reset environnement et SNode");
        button_clear = new Button("Clear Log");
        button_mode_exec = new Button(client.getExecutionModeString());
        button_exec = new Button("Execution");
        button_switch_mode_graphics = new Button("Switch graphics mode");

        disableButtons();

        panel_button.add(button_file);
        panel_button.add(button_send_script);
        panel_button.add(button_clear);
        panel_button.add(button_stop);
        panel_button.add(button_mode_exec);
        panel_button.add(button_exec);
        panel_button.add(button_switch_mode_graphics);

        setActionListeners();

        // zones d'affichage ou de saisie
        panel_edit = new JPanel();
        panel_edit.setLayout(new GridLayout(1, 4));

        panel_env_snode.setLayout(new GridLayout(2, 1));

        createTextAreas();

        // graphique
        graph = new JComponent() {
        };

        panel_env_snode.add(s_txt_env);
        panel_env_snode.add(s_txt_snode);

        panel_edit.add(s_txt_in);
        panel_edit.add(s_txt_out);
        panel_edit.add(graph);
        panel_edit.add(panel_env_snode);

        panel.setLayout(new BorderLayout());
        panel.add(panel_button, BorderLayout.NORTH);
        panel.add(panel_edit, BorderLayout.CENTER);

        displayEnv("");
        displaySNode("");

        return (panel);
    }

    /**
     * Récupère le contenu d'un fichier.
     *
     * @param f le path du fichier à lire
     * @return le contenu du fichier
     * @throws IOException Erreur d'input output
     */
    private String getFileContent(String f) throws IOException {
        String res;

        byte[] encoded = Files.readAllBytes(Paths.get(f));
        res = new String(encoded, StandardCharsets.UTF_8);

        res = res.replace("\"\\", "\"");
        return res.replace("\\\"","\"");
    }

    /**
     * Envoie au serveur le flag qui indique qu'il faut exécuter le script.
     */
    private void sendExecuteFlag() {
        DataCS dataCS = new DataCS();
        dataCS.cmd = "execCommand";
        dataCS.txt = "";
        sendDataServer(dataCS);

        if (client.getExecutionModeString().equals("Block")) {
            writeLog("Exécution du script");
        }
    }

    /**
     * Envoie au serveur le script à exécuter.
     */
    private void sendScript() {
        DataCS dataCS = new DataCS();
        dataCS.cmd = "";
        String txt = txt_in.getText();

        if (txt.length() == 0) {
            writeLog("Erreur, le script est vide");
            return;
        }

        dataCS.txt = txt;
        sendDataServer(dataCS);
        DataSC dataSC = receiveDataServer();
        if (dataSC == null) {
            System.err.println("Erreur, dataSC est null dans la réception des informations de compilation, dans sendScript");
        }

        if (!dataSC.getErrMsg().equals("")) {
            writeLog(dataSC.getErrMsg());
            return;
        }
        writeLog("Script envoyé au serveur");
    }

    private void displayEnv(String env) {
        txt_env.setText("Environment variables\n\n" + env);
    }

    private void displaySNode(String script) {
        txt_snode.setText("Script state\n\n" + script);
    }

    /**
     * Reçoit les données du serveur. Les données sont reçues sous le format JSON.
     *
     * @return la class DataSC reçu du serveur
     */
    private DataSC receiveDataServer() {
        try {
            DataSC jsonData;
            String json = (String) in.readObject();

            if (json == null || json.equals("")) {
                System.err.println("Le serveur n'a rien renvoyé.");
                return null;
            }
            System.out.println("le serveur a renvoyé cote receive: " + json);
            jsonData = new ObjectMapper().readValue(json, DataSC.class);

            displayEnv(jsonData.env);
            displaySNode(jsonData.SNode);

            return jsonData;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur à la lecture des données du serveur");
            throw new RuntimeException(e);
        }

    }

    private Graph receiveGraphsFromServer() {
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

    /**
     * Lit une image encodée en base64.
     *
     * @param img l'image encodée en base64 en string
     * @return l'image en BufferedImage
     */
    public BufferedImage lireImage(String img) {
        // Convertissez la chaîne en tableau d'octets
        byte[] imageEnOctets = Base64.getDecoder().decode(img);

        // Créez un ByteArrayInputStream à partir du tableau d'octets
        ByteArrayInputStream bais = new ByteArrayInputStream(imageEnOctets);

        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            System.err.println("Erreur à la lecture de l'image");
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie les données, commandes ici, au serveur.
     *
     * @param dataCS les données à envoyer en JSON
     */
    private void sendDataServer(DataCS dataCS) {
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
     * Connecte le client au serveur.
     */
    private void connectServer() {
        try {
            socket = Client.connectToServer(2000);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Erreur à la connexion au serveur");
            e.printStackTrace();
        }
        writeLog("Connexion au serveur réussie.");
    }

    private void writeLog(String s) {
        txt_out.setText(txt_out.getText() + s + " \n");
    }

    /**
     * Active tous les boutons de l'IHM.
     */
    private void enableButtons() {
        button_file.setEnabled(true);
        button_send_script.setEnabled(true);
        button_mode_exec.setEnabled(true);
        button_stop.setEnabled(true);
        button_exec.setEnabled(true);
        button_clear.setEnabled(true);
        button_switch_mode_graphics.setEnabled(true);
    }

    private void disableButtons() {
        button_file.setEnabled(false);
        button_send_script.setEnabled(false);
        button_stop.setEnabled(false);
        button_mode_exec.setEnabled(false);
        button_exec.setEnabled(false);
        button_clear.setEnabled(false);
        button_switch_mode_graphics.setEnabled(false);
    }

    /**
     * Sélectionne un fichier.
     *
     * @return le chemin du fichier sélectionné
     */
    public String selectionnerFichier() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        File fileDirectory = new File(currentDir);
        chooser.setCurrentDirectory(fileDirectory);
        if (chooser.showDialog(frame, "Sélection d'un fichier") == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            String destination = selected.getParent() + File.separatorChar + selected.getName();
            currentDir = selected.getParent();
            return (destination);
        }
        return ("");
    }

    private void setActionListeners() {
        button_file.addActionListener(e -> {
            txt_out.setText(txt_out.getText() + "sélection d'un fichier\n");
            String f = selectionnerFichier();
            txt_out.setText(txt_out.getText() + "fichier sélectionné : " + f + "\n");

            try {
                String contentFile = getFileContent(f);
                txt_in.setText(contentFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        button_send_script.addActionListener(e -> sendScript());

        button_stop.addActionListener(e -> {
            sendStopFlag();
            writeLog("Arrêt du script");
        });
        button_clear.addActionListener(e -> txt_out.setText(""));

        button_mode_exec.addActionListener(e -> {
            client.changeMode();
            button_mode_exec.setLabel(client.getExecutionModeString());
            sendCurrentSwitchMode();
        });

        button_exec.addActionListener(e -> {
            sendExecuteFlag();
            clear();


            //graph.removeAll();

            // Recoit le nombre de loop qu'il doit faire
            DataSC dataSC = receiveDataServer();

            if (dataSC == null) {
                System.err.println("Erreur, pas de dataSC d'envoyé dans l'action listener du bouton exec");
                return;
            }

            for (int i = 0; i < dataSC.getnLoops(); i++) {
                gra = receiveGraphsFromServer();

                if (gra == null) {
                    System.err.println("Erreur, le graph envoyé par le serveur est null dans l'action listener du bouton exec");
                    return;
                }

                gra.draw(graph);
            }

            // C'est pour avoir la ligne exécutée quand on est en mode step by step. Quand on est en mode bloc l'objet est envoyé quand même.
            DataSC data = receiveDataServer();
            if (data == null) {
                writeLog("Erreur de communication avec le serveur");
                return;
            }

            if (client.getExecutionMode() == Client.mode.STEP_BY_STEP) {
                writeLog("Ligne : " + data.getTxt() + " exécutée");
            }
        });
    }

    private void clear() {
        // Obtenez la largeur et la hauteur de la zone graphique
        int width = graph.getWidth();
        int height = graph.getHeight();

        // Définissez la couleur de fond
        Color c = graph.getBackground();

        Graph g2 = new Graph();
        g2.setCmd("drawRect");
        g2.setCouleurs(new int[]{c.getRed(), c.getGreen(), c.getBlue()});
        g2.setEntiers(new int[]{0, 0, width, height});

        // Dessinez un rectangle rempli de la couleur de fond sur toute la zone graphique
        g2.draw(graph);
    }

    private void sendStopFlag() {
        DataCS dataCS = new DataCS();
        dataCS.cmd = "stop";
        dataCS.txt = "";
        sendDataServer(dataCS);

        DataSC data = receiveDataServer();
        if (data == null) {
            writeLog("Erreur de communication avec le serveur");
            return;
        }

        writeLog("Suppression des données d'environnement et de script du serveur");
    }

    private void createTextAreas() {
        txt_snode = new JTextPane();
        txt_snode.setEditable(false);
        txt_snode.setFont(courierFont);
        s_txt_snode = new JScrollPane();
        txt_env = new JTextPane();
        txt_env.setEditable(false);
        txt_env.setFont(courierFont);
        s_txt_env = new JScrollPane();

        s_txt_snode.setPreferredSize(new Dimension(320, 240));
        s_txt_snode.getViewport().add(txt_snode);
        s_txt_env.setPreferredSize(new Dimension(320, 240));
        s_txt_env.getViewport().add(txt_env);

        txt_in = new JTextPane();
        txt_in.setEditable(true);
        txt_in.setFont(courierFont);
        txt_in.setText("(space add robi (Rect new))\n");
        s_txt_in = new JScrollPane();
        s_txt_in.setPreferredSize(new Dimension(640, 480));
        s_txt_in.getViewport().add(txt_in);

        txt_out = new JTextPane();
        txt_out.setEditable(false);
        txt_out.setFont(courierFont);
        s_txt_out = new JScrollPane();
        s_txt_out.setPreferredSize(new Dimension(640, 480));
        s_txt_out.getViewport().add(txt_out);
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        new ClientRobiSwing();
    }
}
