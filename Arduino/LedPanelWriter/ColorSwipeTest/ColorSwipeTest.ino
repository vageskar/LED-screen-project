void setup() {
  Serial.begin(9600);
  byte rgb[3] = {255, 0, 0};
    int pixel = 0;
    for (int decColor = 0; decColor < 3; decColor++) {
      int incColor = decColor == 2 ? 0 : decColor + 1;
      for (int i = 0; i < 255; i++, pixel++) {
        
      }
    }
    Serial.println(pixel);
}

void loop() {
  
}
