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

		Point point = new Point();

		int wait_time = 2;
		while (true) {
			for (int i = 0; i < space.getWidth() - robi.getWidth(); i++) {
				point.x++;
				robi.setPosition(point);
				Tools.sleep(wait_time);
			}
			for (int i = 0; i < space.getHeight() - robi.getHeight(); i++) {
				point.y++;
				robi.setPosition(point);
				Tools.sleep(wait_time);
			}
			for (int i = 0; i < space.getWidth() - robi.getWidth(); i++) {
				point.x--;
				robi.setPosition(point);
				Tools.sleep(wait_time);
			}
			for (int i = 0; i < space.getHeight() - robi.getHeight(); i++) {
				point.y--;
				robi.setPosition(point);
				Tools.sleep(wait_time);
			}
			// Set random color
			// Q: Why is there is 0x1000000 ?
			// A: 0x1000000 is the maximum value of a color in hexadecimal
			robi.setColor(new Color((int) (Math.random() * 0x1000000)));
		}
	}

	public static void main(String[] args) {
		new Exercice1_0();
	}

}