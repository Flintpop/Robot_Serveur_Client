package exercice4;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.print.attribute.standard.NumberOfInterveningJobs;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
        button_start = new Button("Envoi du script");
        button_stop = new Button("Stop");
        button_mode_exec = new Button(client.getExecutionModeString());
        button_exec = new Button("Execution");

        button_file.addActionListener(e -> {
            txt_out.setText(txt_out.getText() + "sélection d'un fichier\n");
            String f = selectionnerFichier();
            txt_out.setText(txt_out.getText() + "fichier sélectionné : " + f + "\n");
        });

        button_start.addActionListener(e -> {
            sendScript();
        });

        button_stop.addActionListener(e -> txt_out.setText(txt_out.getText() + "clic bouton stop\n"));

        button_mode_exec.addActionListener(e -> {
            client.changeMode();
            button_mode_exec.setLabel(client.getExecutionModeString());
            DataCS dataCS = new DataCS();
            dataCS.cmd = "switchMode";
            dataCS.txt = client.getExecutionModeString();
            sendDataServer(dataCS);
        });

        button_exec.addActionListener(e -> {
            sendExecuteFlag();
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

    private void sendExecuteFlag() {
        DataCS dataCS = new DataCS();
        dataCS.cmd = "execCommand";
        dataCS.txt = "";
        sendDataServer(dataCS);
    }

    private void sendScript() {
        // TODO: Il faut transformer le input du champ s_txt_in en json avec cmd = machin et txt = suite de la commande.
        //  Le json est très relou. Ça pourrait marcher si on envoie directement le string 🤷🏻‍
        //  On reçoit un truc comme ça
        //      (space setColor blue)
        //      (robi setColor red)
        //  ou comme ça
        //      (space setColor blue) (robi setColor red)
        //  Ensuite on a les trucs relou comme ça :
        //      (space add (GRect img))

        // Et il faut les mettre dans les dataCS

        DataCS dataCS = new DataCS();
        dataCS.cmd = "";
        dataCS.txt = txt_in.getText();
        sendDataServer(dataCS);
    }

    private void sendDataServer(DataCS dataCS) {
        System.out.println("Envoi des données :" + dataCS.toString());
        StringWriter sw = new StringWriter();
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

