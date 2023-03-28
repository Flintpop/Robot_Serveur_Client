package client_robi_swing;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.swing.JComponent;

// fonctionne seulement pour toutes les fonctions graphiques qui ont des entiers en paramètre
// il faut gérer des cas particuliers pour les autres
// setColor, drawString, ...

public class Graph {

	String cmd = null;			// nom de la commande awt graphics
	int [] entiers = null;		// paramètres de type int
	String [] chaines = null;	// paramètres de type String
	int [] couleurs = null;		// couleurs RGB (3 entiers)
	
	public void draw(JComponent co) {
		Graphics g = co.getGraphics();
		
		try {
			Class c = Graphics.class;
			
			// si 4 params de type int
			// Method m1 = c.getDeclaredMethod(cmd,int.class,int.class,int.class,int.class);
			
			Method [] lm = c.getMethods();
			for (Method m : lm) {
				if (m.getName().equals(cmd)) {
					if (couleurs != null) {
						Color col = new Color(couleurs[0],couleurs[1],couleurs[2]);
						g.setColor(col);
					}
					
					if (m.getName().equals("drawString")) {
						System.out.println("Exec drawString");
						//g.setColor(Color.RED);
						System.out.println("param drawString : "+chaines[0]+" "+entiers[0]+" "+entiers[1]);
						g.drawString(chaines[0],entiers[0],entiers[1]);
					}
					else {
						Parameter [] lp = m.getParameters();
						System.out.println("Exec " + cmd + " nb params = "+lp.length);
						if (lp.length == 0) {
							m.invoke(g);
						}
						else if (lp.length == 1) {
							m.invoke(g,entiers[0]);
						}
						else if (lp.length == 2) {
							m.invoke(g,entiers[0],entiers[1]);
						}
						else if (lp.length == 3) {
							m.invoke(g,entiers[0],entiers[1],entiers[2]);
						}
						else if (lp.length == 4) {
							m.invoke(g,entiers[0],entiers[1],entiers[2],entiers[3]);
						}
						else {
							System.out.println("Erreur Graph.draw : trop de paramètres : "+lp.length);
						}
					}
					
					break;
				}
			}
		}
		catch (Exception e) {
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

	
	
	
	
}
