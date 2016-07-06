
package com.quexten.arduino.imageconverter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

	private BufferedImage image;

	public ImagePanel () {
	}

	@Override
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		if (image != null)
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
	}

	/** Sets the Image
	 * @param path - the path to the image */
	public void setImage (String path) {
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException ex) {
		}
	}

	/** Gets the Image
	 * @return the Image */
	public BufferedImage getImage () {
		return image;
	}

}
