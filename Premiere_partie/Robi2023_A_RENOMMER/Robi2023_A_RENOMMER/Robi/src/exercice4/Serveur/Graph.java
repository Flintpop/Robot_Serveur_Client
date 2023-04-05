package exercice4.Serveur;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Base64;
import javax.imageio.ImageIO;

// fonctionne seulement pour toutes les fonctions graphiques qui ont des entiers en paramètre
// il faut gérer des cas particuliers pour les autres
// setColor, drawString, ...

public class Graph {

	String cmd = null;			// nom de la commande awt graphics
	int [] entiers = null;		// paramètres de type int
	String [] chaines = null;	// paramètres de type String
	int [] couleurs = null;		// couleurs RGB (3 entiers)


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
