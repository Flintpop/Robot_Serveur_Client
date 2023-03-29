package exercice4;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import graphicLayer.GImage;
import stree.parser.SNode;

public class NewImage implements Command {

	@Override
	public Reference run(Reference reference, SNode method) {
		try {
			
			
			File path = new File(method.get(2).contents());
			BufferedImage rawImage = null;
			try {
				rawImage = ImageIO.read(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			GImage i = new GImage(rawImage);
			Reference ref = new Reference(i);
			ref.addCommand("translate", new Translate());
			return ref;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
}
