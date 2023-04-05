package exercice4.Client;

import exercice4.Serveur.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Client RobiSwing. IHM pour le client. Permet de saisir des expressions ROBI et de les envoyer au serveur.
 * Reçoit les résultats et les affiche.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ClientRobiSwing {
    protected Graph gra;

    private final JFrame frame;

    private final String title = "IHM Robi";

    @SuppressWarnings("unused")
    private final Font dialogFont = new Font("Dialog", Font.PLAIN, 12);
    private final Font courierFont = new Font("Courier", Font.PLAIN, 12);

    private JPanel panel_env_snode = null;
    private JPanel panel_edit = null;
    protected Button button_file = null;
    protected Button button_send_script = null;
    protected Button button_mode_exec = null;
    protected Button button_stop = null;
    protected Button button_exec = null;
    protected JTextPane txt_in = null; // saisies expressions ROBI
    private JScrollPane s_txt_in = null;
    private JTextPane txt_snode = null;
    private JScrollPane s_txt_snode = null;
    private JTextPane txt_env = null;
    private JScrollPane s_txt_env = null;
    protected JTextPane txt_out = null; // affichage des résultats
    private JScrollPane s_txt_out = null;

    protected JComponent graph = null; // affichage graphique

    private String currentDir = ".";
    protected Button button_clear = null;

    /**
     * Initialisation de l'IHM. Création des composants. Affichage de la fenêtre. Connexion au serveur.
     * Envoi du mode d'exécution au serveur (block par défaut).
     */
    public ClientRobiSwing() {
        frame = new JFrame(title);

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

        button_file = new Button();
        button_send_script = new Button();
        button_stop = new Button();
        button_clear = new Button();
        button_mode_exec = new Button();
        button_exec = new Button();

        disableButtons();

        panel_button.add(button_file);
        panel_button.add(button_send_script);
        panel_button.add(button_clear);
        panel_button.add(button_stop);
        panel_button.add(button_mode_exec);
        panel_button.add(button_exec);

        // zones d'affichage ou de saisie
        panel_edit = new JPanel();
        panel_edit.setLayout(new GridLayout(1, 4));

        panel_env_snode.setLayout(new GridLayout(2, 1));

        createTextAreas();

        graph = new JComponent() {
            /**
             * Endroit où repose l'affichage de l'image reçue du serveur.
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
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
     */
    protected String getFileContent(String f) throws IOException {
        String res;

        byte[] encoded = Files.readAllBytes(Paths.get(f));
        res = new String(encoded, StandardCharsets.UTF_8);

        return res;
    }

    protected void displayEnv(String env) {
        txt_env.setText("Environment variables\n\n" + env);
    }

    protected void displaySNode(String script) {
        txt_snode.setText("Script state\n\n" + script);
    }

    protected void writeLog(String s) {
        txt_out.setText(txt_out.getText() + s + " \n");
    }

    /**
     * Active tous les boutons de l'IHM.
     */
    protected void enableButtons() {
        button_file.setEnabled(true);
        button_send_script.setEnabled(true);
        button_mode_exec.setEnabled(true);
        button_stop.setEnabled(true);
        button_exec.setEnabled(true);
        button_clear.setEnabled(true);
    }

    private void disableButtons() {
        button_file.setEnabled(false);
        button_send_script.setEnabled(false);
        button_stop.setEnabled(false);
        button_mode_exec.setEnabled(false);
        button_exec.setEnabled(false);
        button_clear.setEnabled(false);
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

    protected void clear() {
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

    public String getInputText() {
        return txt_in.getText();
    }
}
