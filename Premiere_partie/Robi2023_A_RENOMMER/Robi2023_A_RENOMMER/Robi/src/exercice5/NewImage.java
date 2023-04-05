package exercice5;

import graphicLayer.GImage;
import stree.parser.SNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <p>Mise en oeuvre de {@code Command} permettant de créer une nouvelle référence de type {@code GImage}.</p>
 * Utilisation en S-expression :
 * <ul>
 * 		{@code ( Image new pathname )}
 * </ul>
 * Resultat : création d'une nouvelle référence de GImage, pas de modifications graphiques ou de l'environnement
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public class NewImage implements Command {

	@Override
	public Reference run(Reference reference, SNode method) {
		try {
			
			// Récupération du fichier image à partir du pathname passé en paramètre
			File path = new File(method.get(2).contents());			
			BufferedImage rawImage;
			try {
				rawImage = ImageIO.read(path);
			} catch (IOException e) {
				// Fichier non valide
				System.err.println("ERREUR : Fichier inexistant");
				//e.printStackTrace();
				return null;
			}
			
			// Création du GImage et de sa référence
			GImage i = new GImage(rawImage);
			Reference ref = new Reference(i);
			
			// Ajout du set de commande executable par une référence de GImage
			ref.addCommand("translate", new Translate());
			
			return ref;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
}
