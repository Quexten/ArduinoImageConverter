
package com.quexten.arduino.imageconverter;

import org.zu.ardulink.Link;

public class ArduinoSerialInterface {
		
	public static void init () {		
		System.out.println("Connected:" + Link.getDefaultInstance().connect("COM4", 9600));		
	}

	public static void send (String text) {
		Link.getDefaultInstance().sendCustomMessage(text);
	}
	
	

}
