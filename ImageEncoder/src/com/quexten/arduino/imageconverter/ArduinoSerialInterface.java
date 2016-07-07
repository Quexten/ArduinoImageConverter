
package com.quexten.arduino.imageconverter;

import org.zu.ardulink.Link;

public class ArduinoSerialInterface {
		
	public static void init (String string) {		
		System.out.println("Connected:" + Link.getDefaultInstance().connect(string, 9600));		
	}

	public static void send (String text) {
		Link.getDefaultInstance().sendCustomMessage(text);
	}
	
	
}
