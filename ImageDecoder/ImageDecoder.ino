#include <FastLED.h>

#define LED_PIN     2
#define NUM_LEDS    64
#define BRIGHTNESS  4
#define LED_TYPE    WS2812B
#define COLOR_ORDER GRB
CRGB leds[NUM_LEDS];

#define UPDATES_PER_SECOND 60

CRGBPalette16 currentPalette;
TBlendType    currentBlending;

String imageString = "00000000000000000000FF0000FF0000000000000000000000000000000000000000FF0000FF0000000000000000000000000000000000000000FF0000FF0000000000000000000000000000000000000000FF0000FF0000000000000000000000FF0000FF0000FF0000FF0000FF0000FF0000FF0000FF0000000000FF0000FF0000FF0000FF0000FF0000FF0000000000000000000000FF0000FF0000FF0000FF0000000000000000000000000000000000FF0000FF00000000000000000000";
String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete


void setup() {
    delay( 3000 ); // power-up safety delay
    FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
    FastLED.setBrightness(  BRIGHTNESS );
    
    currentPalette = RainbowColors_p;
    currentBlending = LINEARBLEND;
    
    Serial.begin(9600);
    inputString.reserve(200);
}


void loop()
{
    if (stringComplete) {
        inputString.replace("\n","");
        inputString.replace("\r","");
        inputString.replace("alp://cust/", "");
        Serial.println("Recived:" + inputString);
        imageString = inputString;
        // clear the string:
        inputString = "";
        stringComplete = false;
    }
  
   for(int x = 0; x < 8; x++) {
        for(int y = 0; y < 8; y++) {
            int ledIndex = x * 8 + y;
  
            String hexString = imageString.substring(ledIndex * 6, ledIndex * 6 + 6);
  
            long number = strtol( &hexString[0], NULL, 16);
                  
            leds[ledIndex].red =  number >> 16;
            leds[ledIndex].green = number >> 8 & 0xFF;
            leds[ledIndex].blue = number & 0xFF;
        }
    }
    FastLED.show();
    FastLED.delay(1000 / UPDATES_PER_SECOND);
}

void serialEvent() {
    while (Serial.available()) {
        // get the new byte:
        char inChar = (char)Serial.read();
        // add it to the inputString:
        inputString += inChar;
        // if the incoming character is a newline, set a flag
        // so the main loop can do something about it:
        if (inChar == '\n') {
            stringComplete = true;
        }
    }
}

