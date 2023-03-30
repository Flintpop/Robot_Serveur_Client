package exercice4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
//import ../../../exemple_json.

public class ClientRobiSwing {
    Client client;
    private Socket socket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JFrame frame;

    private String title = "IHM Robi";

    private Font dialogFont = new Font("Dialog", Font.PLAIN, 12);
    private Font courierFont = new Font("Courier", Font.PLAIN, 12);

    private Button button_file = null;
    private Button button_start = null;
    private Button button_stop = null;
    private Button button_mode_exec = null;
    private Button button_exec = null;

    private JTextPane txt_in = null; // saisie expressions ROBI
    private JScrollPane s_txt_in = null;

    private JTextPane txt_out = null; // affichage des résultats
    private JScrollPane s_txt_out = null;

    private JComponent graph = null; // affichage graphique

    private String currentDir = ".";

    public ClientRobiSwing() {
        frame = new JFrame(title);
        client = new Client();
        Component contents = createComponents();
        frame.getContentPane().add(contents);
        // frame.setJMenuBar(bar);

        // Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        connectServer();

    }

//    private void send


    public Component createComponents() {
        JPanel panel = new JPanel();

        // boutons
        JPanel panel_button = new JPanel();
        panel_button.setLayout(new GridLayout(1, 5));

        button_file = new Button("Fichier");
        button_start = new Button("Start");
        button_stop = new Button("Stop");
        button_mode_exec = new Button(client.getExecutionModeString());
        button_exec = new Button("Execution");

        button_file.addActionListener(e -> {
            txt_out.setText(txt_out.getText() + "sélection d'un fichier\n");
            String f = selectionnerFichier();
            txt_out.setText(txt_out.getText() + "fichier sélectionné : " + f + "\n");
        });

        button_start.addActionListener(e -> txt_out.setText(txt_out.getText() + "clic bouton start\n"));

        button_stop.addActionListener(e -> txt_out.setText(txt_out.getText() + "clic bouton stop\n"));

        button_mode_exec.addActionListener(e -> {
            client.changeMode();
            button_mode_exec.setLabel(client.getExecutionModeString());
        });

        button_exec.addActionListener(e -> {
            txt_out.setText(txt_out.getText() + "clic bouton rect 2\n");
            txt_out.setText(txt_out.getText() + "attention : le scroll et le rafraichissement de l'image se sont pas gérés\n");

            Graph g;

            // couleurs
            int[] couleur1 = {
                    255, 0, 0
            };
            int[] couleur2 = {
                    0, 0, 255
            };

            int[] couleur3 = {
                    255, 0, 255
            };

            // affichage d'un rectangle
            g = new Graph();
            int[] positions = {
                    10, 150, 200, 100
            };
            g.setCmd("fillRect");
            g.setEntiers(positions);
            g.setCouleurs(couleur1);
            g.draw(graph);

            // affichage texte 1
            g = new Graph();
            int[] positions1 = {
                    10, 25
            };
            String[] textes1 = {
                    "texte 1"
            };
            g.setCmd("drawString");
            g.setEntiers(positions1);
            g.setChaines(textes1);
            g.setCouleurs(couleur2);
            g.draw(graph);

            // affichage texte 2
            g = new Graph();
            int[] positions2 = {
                    10, 50
            };
            String[] textes2 = {
                    "texte 2"
            };
            g.setCmd("drawString");
            g.setEntiers(positions2);
            g.setChaines(textes2);
            g.setCouleurs(couleur3);
            g.draw(graph);

            // affichage texte 3 (en noir parce que la couleur n'est pas fournie)
            g = new Graph();
            int[] positions3 = {
                    10, 75
            };
            String[] textes3 = {
                    "texte 3"
            };
            g.setCmd("drawString");
            g.setEntiers(positions3);
            g.setChaines(textes3);
            //g.setCouleurs(couleur3);
            g.draw(graph);


        });

        panel_button.add(button_file);
        panel_button.add(button_start);
        panel_button.add(button_stop);
        panel_button.add(button_mode_exec);
        panel_button.add(button_exec);

        // zones d'affichage ou de saisie
        JPanel panel_edit = new JPanel();
        panel_edit.setLayout(new GridLayout(1, 3));

        txt_in = new JTextPane();
        txt_in.setEditable(true);
        txt_in.setFont(courierFont);
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
        };

        panel_edit.add(s_txt_in);
        panel_edit.add(s_txt_out);
        panel_edit.add(graph);

        panel.setLayout(new BorderLayout());
        panel.add(panel_button, BorderLayout.NORTH);
        panel.add(panel_edit, BorderLayout.CENTER);

        return (panel);

    }


    private void connectServer() {
        try {
            socket = Client.connectToServer(2000);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String selectionnerFichier() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        File fdir = new File(currentDir);
        chooser.setCurrentDirectory(fdir);
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

