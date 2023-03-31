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

public class ClientRobiSwing {
    Client client;
    private Socket socket;

    private BufferedImage image;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JFrame frame;

    private String title = "IHM Robi";

    private Font dialogFont = new Font("Dialog", Font.PLAIN, 12);
    private Font courierFont = new Font("Courier", Font.PLAIN, 12);

    private JPanel panel_edit = null;
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
        DataCS initSwitchMode = new DataCS();
        initSwitchMode.cmd = "switchMode";
        initSwitchMode.txt = client.getExecutionModeString();
        sendDataServer(initSwitchMode);
    }

    /**
     * Création des composants de l'IHM
     *
     * @return
     */
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

            displayScreeshot(lireImage(receiveDataServer()));
        });

        panel_button.add(button_file);
        panel_button.add(button_start);
        panel_button.add(button_stop);
        panel_button.add(button_mode_exec);
        panel_button.add(button_exec);

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

    private void sendExecuteFlag() {
        DataCS dataCS = new DataCS();
        dataCS.cmd = "execCommand";
        dataCS.txt = "";
        sendDataServer(dataCS);
    }

    private void sendScript() {
        DataCS dataCS = new DataCS();
        dataCS.cmd = "";
        dataCS.txt = txt_in.getText();
        sendDataServer(dataCS);
    }

    private void displayScreeshot(BufferedImage img) {
        image = img;
        graph.repaint();
        graph.revalidate();
    }


    private String receiveDataServer() {
        try {
            // TODO: Bug il faut 2 appels pour recevoir le message suivant au tout premier appel
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
