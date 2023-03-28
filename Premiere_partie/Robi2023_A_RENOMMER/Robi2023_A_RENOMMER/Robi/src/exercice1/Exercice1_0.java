package exercice1;

import java.awt.*;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import tools.Tools;

public class Exercice1_0 {
	GSpace space = new GSpace("Exercice 1", new Dimension(200, 150));
	GRect robi = new GRect();

	public Exercice1_0() {
		space.addElement(robi);
		space.open();

		int currentX = 0;
		int currentY = 0;

		int wait_time = 2;
		while (true) {
			for (int i = 0; i < space.getWidth() - robi.getWidth(); i++) {
				currentX = i;
				robi.setPosition(new Point(currentX, currentY));
				Tools.sleep(wait_time);
			}
			for (int i = 0; i < space.getHeight() - robi.getHeight(); i++) {
				currentY = i;
				robi.setPosition(new Point(currentX, currentY));
				Tools.sleep(wait_time);
			}
			for (int i = 0; i < space.getWidth() - robi.getWidth(); i++) {
				currentX--;
				robi.setPosition(new Point(currentX, currentY));
				Tools.sleep(wait_time);
			}
			for (int i = 0; i < space.getHeight() - robi.getHeight(); i++) {
				currentY--;
				robi.setPosition(new Point(currentX, currentY));
				Tools.sleep(wait_time);
			}
		}
	}

	public static void main(String[] args) {
		new Exercice1_0();
	}

}