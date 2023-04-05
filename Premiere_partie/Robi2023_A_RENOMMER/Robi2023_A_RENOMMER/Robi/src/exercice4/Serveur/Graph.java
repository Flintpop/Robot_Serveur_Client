package exercice4.Serveur;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.*;

// fonctionne seulement pour toutes les fonctions graphiques qui ont des entiers en paramètre
// il faut gérer des cas particuliers pour les autres
// setColor, drawString, ...

public class Graph {

	public static int nGraphs = 0;
	String cmd = null;            // nom de la commande awt graphics
	int [] entiers = null;        // paramètres de type int
	String [] chaines = null;    // paramètres de type String
	int [] couleurs = null;        // couleurs RGB (3 entiers)

	public void draw(JComponent co) {
		Graphics graphics = co.getGraphics();

		try {
			Class<Graphics> c = Graphics.class;

			// si 4 params de type int
			// Method m1 = c.getDeclaredMethod(cmd,int.class,int.class,int.class,int.class);

			Method [] lm = c.getMethods();
			for (Method m : lm) {
				if (m.getName().equals(cmd)) {
					if (couleurs != null) {
						Color col = new Color(couleurs[0],couleurs[1],couleurs[2]);
						graphics.setColor(col);
					}

					switch (m.getName()) {
						case "drawSpace":
							co.setBackground(graphics.getColor());
							break;
						case "drawString":
							System.out.println("Exec drawString");
							System.out.println("param drawString : " + chaines[0] + " " + entiers[0] + " " + entiers[1]);
							graphics.drawString(chaines[0], entiers[0], entiers[1]);
							break;
						case "drawRect":
							System.out.println("Exec drawRect");
							graphics.fillRect(entiers[0], entiers[1], entiers[2], entiers[3]);
							graphics.drawRect(entiers[0], entiers[1], entiers[2], entiers[3]);
							break;
						case "drawOval":
							System.out.println("Exec drawOval");
							graphics.fillOval(entiers[0], entiers[1], entiers[2], entiers[3]);
							graphics.drawOval(entiers[0], entiers[1], entiers[2], entiers[3]);
							break;
						case "drawImage":
							System.out.println("Exec drawImage");
							Image img = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(chaines[0])));
							graphics.drawImage(img, entiers[0], entiers[1], null);
							break;
						default:
							Parameter[] lp = m.getParameters();
							System.out.println("Exec " + cmd + " nb params = " + lp.length);

							switch(lp.length) {
								case 0:
								    m.invoke(graphics);
								    break;
								case 1:
								    m.invoke(graphics, entiers[0]);
								    break;
								case 2:
								    m.invoke(graphics, entiers[0], entiers[1]);
								    break;
								case 3:
								    m.invoke(graphics, entiers[0], entiers[1], entiers[2]);
								    break;
								case 4:
								    m.invoke(graphics, entiers[0], entiers[1], entiers[2], entiers[3]);
								    break;
								default:
								    System.out.println("Erreur Graph.draw : trop de paramètres : " + lp.length);
							}

							if (lp.length == 0) {

							} else if (lp.length == 1) {

							} else if (lp.length == 2) {

							} else if (lp.length == 3) {

							} else if (lp.length == 4) {

							} else {

							}
							break;
					}

					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Erreur Graph.draw : "+e.getMessage());
		}
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int[] getEntiers() {
		return entiers;
	}

	public void setEntiers(int[] entiers) {
		this.entiers = entiers;
	}

	public int[] getCouleurs() {
		return couleurs;
	}

	public void setCouleurs(int[] couleurs) {
		this.couleurs = couleurs;
	}

	public String[] getChaines() {
		return chaines;
	}

	public void setChaines(String[] chaines) {
		this.chaines = chaines;
	}

	public int getnGraphs() {
		return this.nGraphs;
	}

	public void setnGraphs(int nGraphs) {
		this.nGraphs = nGraphs;
	}
}
