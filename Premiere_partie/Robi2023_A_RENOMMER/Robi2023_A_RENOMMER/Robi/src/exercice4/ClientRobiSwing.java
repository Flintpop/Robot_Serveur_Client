package exercice4;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
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

    private final JFrame frame;

    private final String title = "IHM Robi";

    @SuppressWarnings("unused")
    private final Font dialogFont = new Font("Dialog", Font.PLAIN, 12);
    private final Font courierFont = new Font("Courier", Font.PLAIN, 12);

    private JPanel panel_edit = null;
    private Button button_file = null;
    private Button button_start = null;
    private Button button_stop = null;
    private Button button_mode_exec = null;
    private Button button_exec = null;
    private JTextPane txt_in = null; // saisies expressions ROBI
    private JScrollPane s_txt_in = null;

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
        frame.setSize(800, 600);
        frame.setVisible(true);

        connectServer();

        enableButtons();

        // Init mode d'exécution au serveur
        sendCurrentSwitchMode();
    }

    private void sendCurrentSwitchMode() {
        DataCS initSwitchMode = new DataCS();
        initSwitchMode.cmd = "switchMode";
        initSwitchMode.txt = client.getExecutionModeString();
        sendDataServer(initSwitchMode);
        String oldExecutionMode;
        oldExecutionMode = "Block";
        if (client.getExecutionModeString().equals("Block")) {
            oldExecutionMode = "Step by Step";
        }
        writeLog("Envoi du changement de mode d'exécution : " + client.getExecutionModeString() + " -> " + oldExecutionMode);
    }

    /**
     * Création des composants de l'IHM, et ajout des listeners.
     *
     * @return le composant principal de l'IHM
     */
    public Component createComponents() {
        JPanel panel = new JPanel();

        // boutons
        JPanel panel_button = new JPanel();
        panel_button.setLayout(new GridLayout(1, 5));

        button_file = new Button("Fichier");
        button_start = new Button("Envoi du script");
        button_stop = new Button("Stop");
        button_clear = new Button("Clear");
        button_mode_exec = new Button(client.getExecutionModeString());
        button_exec = new Button("Execution");

        disableButtons();

        button_file.addActionListener(e -> {
            txt_out.setText(txt_out.getText() + "sélection d'un fichier\n");
            String f = selectionnerFichier();
            txt_out.setText(txt_out.getText() + "fichier sélectionné : " + f + "\n");

            try {
                String contentFile = getFileContent(f);
                System.out.println("contentFile = " + contentFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        button_start.addActionListener(e -> sendScript());

        button_stop.addActionListener(e -> txt_out.setText(txt_out.getText() + "clic bouton stop\n"));

        button_clear.addActionListener(e -> {
            txt_out.setText("");
            writeLog("Effacement de la zone de log");
        });

        button_mode_exec.addActionListener(e -> {
            client.changeMode();
            button_mode_exec.setLabel(client.getExecutionModeString());
            sendCurrentSwitchMode();
        });

        button_exec.addActionListener(e -> {
            sendExecuteFlag();

            displayScreenshot(lireImage(receiveDataServer()));
        });

        panel_button.add(button_file);
        panel_button.add(button_start);
        panel_button.add(button_stop);
        panel_button.add(button_mode_exec);
        panel_button.add(button_exec);
        panel_button.add(button_clear);

        // zones d'affichage ou de saisie
        panel_edit = new JPanel();
        panel_edit.setLayout(new GridLayout(1, 3));

        txt_in = new JTextPane();
        txt_in.setEditable(true);
        txt_in.setFont(courierFont);
        txt_in.setText("(space add robi (Rect new))\n" +
                "(robi translate 130 50)\n" +
                "(robi setColor yellow)\n" +
                "(space add momo (Oval new))\n" +
                "(momo setColor red)\n" +
                "(momo translate 80 80)\n" +
                "(space add pif (Image new alien.gif))\n" +
                "(pif translate 100 0)\n" +
                "(space add hello (Label new \"Hello world\"))\n" +
                "(hello translate 10 10)\n" +
                "(hello setColor black)\n");
        s_txt_in = new JScrollPane();
        s_txt_in.setPreferredSize(new Dimension(640, 480));
        s_txt_in.getViewport().add(txt_in);

        txt_out = new JTextPane();
        txt_out.setEditable(true);
        txt_out.setFont(courierFont);
        s_txt_out = new JScrollPane();
        s_txt_out.setPreferredSize(new Dimension(640, 480));
        s_txt_out.getViewport().add(txt_out);

        graph = new JComponent() {
            /**
             * Endroit où repose l'affichage de l'image reçue du serveur.
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, this);
                }
            }
        };

        panel_edit.add(s_txt_in);
        panel_edit.add(s_txt_out);
        panel_edit.add(graph);

        panel.setLayout(new BorderLayout());
        panel.add(panel_button, BorderLayout.NORTH);
        panel.add(panel_edit, BorderLayout.CENTER);

        return (panel);
    }


    private String getFileContent(String f) throws IOException {
        String res;

        //byte[] encoded = Files.readAllBytes(Paths.get(f));
        //res = new String(encoded, StandardCharsets.UTF_8);

        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            res = sb.toString();
        }

        return res;
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
        dataCS.txt = txt_in.getText();
        sendDataServer(dataCS);
        writeLog("Script envoyé au serveur");
    }

    /**
     * Affiche l'image reçue du serveur.
     *
     * @param img l'image à afficher
     */
    private void displayScreenshot(BufferedImage img) {
        image = img;
        graph.repaint();
        graph.revalidate();
    }

    /**
     * Reçoit les données du serveur. Les données sont reçues sous forme de chaîne de caractères.
     * En l'occurrence le serveur envoie forcément une image encodée en base64.
     *
     * @return l'image reçue du serveur
     */
    private String receiveDataServer() {
        try {
            DataSC jsonData;
            String json = (String) in.readObject();

            if (json == null) {
                System.out.println("Hélas! Le messager est arrivé les mains vides...");
                return null;
            }

            jsonData = new ObjectMapper().readValue(json, DataSC.class);
            System.out.println("Behold! Le JSON divin est arrivé : " + jsonData.toString());

            return jsonData.getIm();
        } catch (IOException | ClassNotFoundException e) {
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
            System.err.println("Erreur sendObject");
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
        button_start.setEnabled(true);
        button_stop.setEnabled(true);
        button_mode_exec.setEnabled(true);
        button_exec.setEnabled(true);
        button_clear.setEnabled(true);
    }

    private void disableButtons() {
        button_file.setEnabled(false);
        button_start.setEnabled(false);
        button_stop.setEnabled(false);
        button_mode_exec.setEnabled(false);
        button_exec.setEnabled(false);
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

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        new ClientRobiSwing();
    }

}
