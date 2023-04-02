package graphicLayer;

import java.awt.Point;

/**
 * Interface permettant de modifier les coordonées d'un objet dans l'espace.
 * 
 * @author Tanguy, Abdelaziz, Samir, Hippolyte
 *
 */
public interface Positionnable {

	Point getPosition();
	void setPosition(Point p);
	
}
