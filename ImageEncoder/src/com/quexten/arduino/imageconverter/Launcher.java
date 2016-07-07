
package com.quexten.arduino.imageconverter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.zu.ardulink.Link;
import org.zu.ardulink.gui.SerialConnectionPanel;

public class Launcher {

	final static String[] IMAGE_TYPES = new String[] { "png", "jpg", "jpeg", "bmp", "gif" };
	final static int IMAGE_HEIGHT = 8;
	final static int IMAGE_WIDTH = 8;

	final static JTextField textField = new JTextField();
	final static JButton button = new JButton("Convert");

	public static void main(String[] args) {
		JFrame frame = new JFrame("Image Converter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final ImagePanel panel = new ImagePanel();
		panel.setPreferredSize(new Dimension(400, 400));

		DropTargetListener dropListener = new DropTargetListener() {
			String currentDragPath;

			@SuppressWarnings("unchecked")
			@Override
			public void dragEnter(DropTargetDragEvent event) {
				try {
					if (event.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						if (isImageList(
								(List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))) {
							currentDragPath = ((List<File>) event.getTransferable()
									.getTransferData(DataFlavor.javaFileListFlavor)).get(0).getPath();
							panel.setBackground(Color.GREEN);
							event.acceptDrag(DnDConstants.ACTION_COPY);
						} else {
							panel.setBackground(Color.RED);
							event.rejectDrag();
						}
					} else {
						panel.setBackground(Color.RED);
						event.rejectDrag();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void dragExit(DropTargetEvent event) {
				panel.setBackground(Color.LIGHT_GRAY.brighter());
			}

			@Override
			public void dragOver(DropTargetDragEvent event) {
			}

			@Override
			public void drop(DropTargetDropEvent event) {
				panel.setBackground(Color.LIGHT_GRAY.brighter());
				panel.setImage(currentDragPath);
				String convertedImage = convertImage(panel.getImage()).toUpperCase();
				System.out.println(convertedImage);
				textField.setText(convertedImage);
				ArduinoSerialInterface.send(convertedImage);
			}

			@Override
			public void dropActionChanged(DropTargetDragEvent event) {
			}
		};
		new DropTarget(panel, dropListener);

		frame.add(panel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		bottomPanel.add(textField, constraints);

		constraints.gridx++;
		constraints.weightx = 0.2;
		bottomPanel.add(button, constraints);

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weighty = 1.0;
		SerialConnectionPanel serialConnectionPanel = new SerialConnectionPanel();
		serialConnectionPanel.setLink(Link.getDefaultInstance());
		bottomPanel.add(serialConnectionPanel, constraints);

		constraints.gridx++;
		constraints.weighty = 0.2;
		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ArduinoSerialInterface.init(serialConnectionPanel.getConnectionPort());
				button.setText("Convert and Send");
			}
		});
		bottomPanel.add(connectButton, constraints);

		frame.add(bottomPanel, BorderLayout.SOUTH);

		frame.pack();
		frame.setVisible(true);
	}

	public static boolean isImageList(List<File> files) {
		boolean isImage = true;

		// Check whether files are all images
		for (int i = 0; i < files.size(); i++) {
			boolean contains = false;
			for (int n = 0; n < IMAGE_TYPES.length; n++) {
				if (files.get(i).getPath().endsWith(IMAGE_TYPES[n])) {
					contains = true;
					break;
				}
			}
			if (!contains)
				isImage = false;
		}
		return isImage;
	}

	public static String convertImage(BufferedImage image) {
		StringBuilder builder = new StringBuilder();

		// Currently only takes the first N Pixels and doesn't downsample if the
		// picture is to big / upsample if the picture is too
		// small
		for (int y = 0; y < IMAGE_HEIGHT; y++) {
			for (int x = 0; x < IMAGE_WIDTH; x++) {
				Color color = Color.decode(String.valueOf(image.getRGB(x, y)));

				String redString = Integer.toHexString(color.getRed());
				builder.append(redString.length() > 1 ? redString : "0" + redString);

				String greenString = Integer.toHexString(color.getGreen());
				builder.append(greenString.length() > 1 ? greenString : "0" + greenString);

				String blueString = Integer.toHexString(color.getBlue());
				builder.append(blueString.length() > 1 ? blueString : "0" + blueString);
			}
		}
		return builder.toString();
	}

}
