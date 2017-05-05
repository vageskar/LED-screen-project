/*
 * This is a program designed to run on a Teensy at 48 MHz
 * The program will display content on a X*Y LED screen built with WS2812 LEDs
 * The program can display content form its own demo modes or get data over the serial port
 * A switch desides where the conten should come from
 * A potentiometer adjust the brightness of the screen
 */

int DATA = 2;   // Data pin
int CLOCK = 3;  // Clock pin
int DEMO = 4;   // Demo mode switch
int DEMO_MODE = 5;  // Demo mode chooser switch
int BRIGHTNESS_PIN = A0; // Potmeter pin
int demoState = 0;  // State of demo mode
int lastState = 0;  // Last demo mode state
int brightnessLevel = 0;  // The brightness level of the screen
int logoPosX = 0;   // The X position of the logo
int logoPosY = 0;   // The Y position of the logo
int speedX = 1;     // The X speed and direction of the logo
int speedY = 1;     // The Y speed and direction of the logo
int swipePixel = 0; // Position of the swipe color
int byteReceived = 0; // Number of byte received


const int BOUNCE_LOGO = 0;  // Bounce logo state
const int RANDOM_COLOR = 1; // Random color state
const int COLOR_SWIPE = 2;  // Color swipe state
const int FILL_RANDOM = 3;  // Fill random state
const int CHANGE_STATE = 4; // Change state state
const int PICTURE_Y = 40;   // Y size of the screen
const int PICTURE_X = 80;   // X size of the screen

boolean lastDemoMode = false;
boolean lastDemo = false;
boolean updateScreen = false;
boolean initColorSwipe = false;
boolean initFillRandom = false;
boolean initBounceLogo = false;

long pictureArray[3200]; // Picture array


long updateTime = 40; // Screen update time milli sec

unsigned long modeTime = 0; // Time for sending mode
unsigned long nextUpdateTime = 0;  // Time to next update
unsigned long nextRandomColor = 0; // Time to next random color

long swipeColors[768];  // Array of swipe colors
long pictureMatrix[80][40]; // Picture matrix
// Logo matrix
long NTNU[8][8] = {
  {0x000000, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x000000},
  {0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x000000, 0x000000, 0x000000, 0x000000, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x000000, 0x000000, 0x000000, 0x000000, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF},
  {0x000000, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x000000}
};

// Setup
void setup() {
  pinMode(DATA, OUTPUT);  // Sets the DATA pin as an output
  pinMode(CLOCK, OUTPUT); // Sets the CLOCK pin as an output
  pinMode(13, OUTPUT);    // Sets the 13 pin as an output
  pinMode(DEMO, INPUT);   // Sets the DEMO pin as an input
  pinMode(DEMO_MODE, INPUT);  // Sets the DEMO_MODE pin as an input
  pinMode(BRIGHTNESS_PIN, INPUT);  // Sets the BRIGHTNESS_PIN pin as an input
  Serial.begin(38400);  // Starts serial communication at 38400 baudrate
  clearMatrix();  // Clears the matrix
  randomSeed(analogRead(A1)); // Initialize the random function with a value from an unused analog input
  nextUpdateTime = millis();  // Set initial value to nextUpodateTime
  modeTime = millis();  // Set initial value to modeTime
}

// Loop. Runs over and over
void loop() {
  writeMode(); // Calls the writeMode function
  int level = analogRead(BRIGHTNESS_PIN); // Read the value of the potmeter
  brightnessLevel = map(level, 0, 1023, 0, 3); // Map the level to desired value
  if (millis() >= nextUpdateTime) {
    updateScreen = true;
    nextUpdateTime += updateTime;
    digitalWrite(13, HIGH); // Used for visual feedback
  }
  else {
    updateScreen = false;
    digitalWrite(13, LOW);  // Used for visual feedback
  }
  if (digitalRead(DEMO)) {
    demo(); // Calls the demo function
    if (updateScreen) {
      writeScreen(); // Calls the writeScreen function
    }
  }
  else {
    if (Serial.available() == 0) {
    }
    // If there is something available on the serial read it and write the data
    else {
      byte in = Serial.read();
      in = changeBrightnessByte(in);
      byteWriter(in); // Write the incomming byte
      byteReceived++;
      // If 9600 bytes (number of bytes on screen) is received, wait 1 millisec to set the screen
      if(byteReceived == 9600){
        digitalWrite(CLOCK, LOW);
        delay(1);
        byteReceived = 0;
      }
    }
  }
}

/* 
 * Demo mode function 
 * This function handels the choosen demo mode and calls the rigth function
 */
void demo() {
  // Change mode if demo mode chooser button goes high
  if (digitalRead(DEMO_MODE) & ! lastDemoMode) {
    lastState = demoState;
    demoState = CHANGE_STATE;
  }
  lastDemoMode = digitalRead(DEMO_MODE);
  switch (demoState) {
    case BOUNCE_LOGO:
      bounceLogo(); // Calls the bounceLogo function
      break;

    case RANDOM_COLOR:
      randomColor();  // Calls the randomColor function
      break;

    case COLOR_SWIPE:
      colorSwipe(); // Calls the colorSwipe function
      break;

    case FILL_RANDOM:
      fillRandom(); // Calls the fillRandom function
      break;

    case CHANGE_STATE:
      switch (lastState) {
        case BOUNCE_LOGO:
          clearMatrix();  // Calls the clearMatrix function
          demoState = RANDOM_COLOR;
          break;

        case RANDOM_COLOR:
          clearMatrix();  // Calls the clearMatrix function
          demoState = COLOR_SWIPE;
          break;

        case COLOR_SWIPE:
          initFillRandom = false;
          clearMatrix();  // Calls the clearMatrix function
          demoState = FILL_RANDOM;
          break;

        case FILL_RANDOM:
          clearMatrix();  // Calls the clearMatrix function
          initBounceLogo = false;
          speedX = 1;
          speedY = 1;
          demoState = BOUNCE_LOGO;
          break;
      }
      break;
  }
}

/*
 * This function takes a byte and breaks it down to bits and write this to the screen
 * The function follows the spesific protocol of the WS2812 LEDs
 */
void byteWriter(byte b) {
  byte in = changeBrightnessByte(b);
  for (byte colorBit = 7; colorBit != 255; colorBit--) {
    digitalWrite(CLOCK, LOW);
    byte mask = 1 << colorBit;
    if (in & mask) {
      digitalWrite(DATA, HIGH);
    }
    else {
      digitalWrite(DATA, LOW);
    }
    digitalWrite(CLOCK, HIGH);
  }
}

/*
 * This function takes a long and breaks it down to bits and write this to the screen
 * The function follows the spesific protocol of the WS2812 LEDs
 */
void longWriter(long l) {
  long rgb = changeBrightnessLong(l);
  for (byte colorBit = 23; colorBit != 255; colorBit--) {
    digitalWrite(CLOCK, LOW);
    long mask = 1L << colorBit;
    if (rgb & mask) {
      digitalWrite(DATA, HIGH);
    }
    else {
      digitalWrite(DATA, LOW);
    }
    digitalWrite(CLOCK, HIGH);
  }
}

/*
 * This function sets all the pixels in the matrix to black
 */
void clearMatrix() {
  for (int y = 0; y < 40; y++) {
    for (int x = 0; x < 80; x++) {
      pictureMatrix[x][y] = 0x000000;
    }
  }
}

/*
 * This function allows content of 8*8 size to be placed enywhere on the screen
 * It takes in the content, start pos x, start pos y and the size of the content.
 */
void addContent(long content[8][8], int startPosX, int startPosY, int contentSize) {
  int xPos = startPosX;
  int yPos = startPosY;
  if (outOfBound(startPosX, startPosY, contentSize)) {
    int xBound = startPosX + contentSize;
    int yBound = startPosY + contentSize;
    if ((xBound > PICTURE_X) && (yBound > PICTURE_Y)) {
      for (int y = 0; yPos < PICTURE_Y; y++) {
        for (int x = 0; xPos < PICTURE_X; x++) {
          pictureMatrix[xPos + x][yPos + y] = content[x][y];
        }
      }
    }
    else if (xBound > PICTURE_X) {
      for (int y = 0; y < contentSize; y++) {
        for (int x = 0; xPos < PICTURE_X; x++) {
          pictureMatrix[xPos + x][yPos + y] = content[x][y];
        }
      }
    }
    else if (yBound > PICTURE_Y) {
      for (int y = 0; yPos < PICTURE_Y; y++) {
        for (int x = 0; x < contentSize; x++) {
          pictureMatrix[xPos + x][yPos + y] = content[x][y];
        }
      }
    }
  }
  else {
    for (int y = 0; y < contentSize; y++) {
      for (int x = 0; x < contentSize; x++) {
        pictureMatrix[xPos + x][yPos + y] = content[x][y];
      }
    }
  }
}

/*
 * Check if the content goes out of the picture
 * Returns true if it does, false if not
 */
boolean outOfBound(int xPos, int yPos, int contentSize) {
  int endPosX = xPos + contentSize;
  int endPosY = yPos + contentSize;
  if ((endPosX > PICTURE_X) || (endPosY > PICTURE_Y)) {
    return true;
  }
  else {
    return false;
  }
}

/*
 * Transform the picture matrix to an array that fits to the screen
 */
void setPictureArray() {
  int pixels = 0;
  for (int y = 0; y < 40; y++) {
    if ((y % 2) == 0) {
      for (int x = 0; x < 80; x++, pixels++) {
        pictureArray[pixels] = pictureMatrix[x][y];
      }
    }
    else {
      for (int x = 79; x >= 0; x--, pixels++) {
        pictureArray[pixels] = pictureMatrix[x][y];
      }
    }
  }
}

/*
 * Function to make a logo bounce on the screen
 */
void bounceLogo() {
  if(!initBounceLogo){
    logoPosX = random(0, 70);
    logoPosY = random(0, 30);
    initBounceLogo = true;
  }
  if (updateScreen) {
    clearMatrix();
    addContent(NTNU, logoPosX, logoPosY, 8);
    logoPosX += speedX;
    logoPosY += speedY;
    if (logoPosX == 72) {
      speedX *= -1;
    }
    else if (logoPosX == 0) {
      speedX *= -1;
    }
    if (logoPosY == 32) {
      speedY *= -1;
    }
    else if (logoPosY == 0) {
      speedY *= -1;
    }
  }
}


/*
 * Fonction to create a random color an dirplay it at the screen
 */
void randomColor() {
  if (millis() > nextRandomColor) {
    nextRandomColor = millis() + 1000;
    byte r = random(0, 255);
    byte g = random(0, 255);
    byte b = random(0, 255);
    long rgb = r << 16 | g << 8 | b;
    for (int y = 0; y < 40; y++) {
      for (int x = 0; x < 80; x++) {
        pictureMatrix[x][y] = rgb;
      }
    }
  }
}

/*
 * Function to generate a color swipe and roll it over the screen
 */
void colorSwipe() {
  if (!initColorSwipe) {
    byte rgb[3] = {255, 0, 0};
    int pixel = 0;
    for (int decColor = 0; decColor < 3; decColor++) {
      int incColor = decColor == 2 ? 0 : decColor + 1;
      for (int i = 0; i < 255; i++, pixel++) {
        swipeColors[pixel] = rgb[0] << 16 | rgb[1] << 8 | rgb[2];
        rgb[decColor]--;
        rgb[incColor]++;
      }
    }
    initColorSwipe = true;
    swipePixel = 0;
  }
  if (updateScreen) {
    int swipe = swipePixel;
    for (int x = 0; x < 80; x++, swipe++) {
      if (swipe == 765) {
        swipe = 0;
      }
      for (int y = 0; y < 40; y++) {
        pictureMatrix[x][y] = swipeColors[swipe];
      }
    }
    swipePixel++;
    if(swipePixel > 765){
      swipePixel = 0;
    }
  }
}

/*
 * Function to randomly set the LEDs on the screen to a random color
 */
void fillRandom() {
  if (updateScreen) {
    byte r = random(0, 255);
    byte g = random(0, 255);
    byte b = random(0, 255);
    long rgb = r << 16 | g << 8 | b;

    int x = random(0, 80);
    int y = random(0, 40);

    pictureMatrix[x][y] = rgb;
  }
}

/*
 * Function to take the picture array and write it to the screen
 */
void writeScreen() {
  setPictureArray();
  for (int x = 0; x < 3200; x++) {
    longWriter(pictureArray[x]);
  }
  digitalWrite(CLOCK, LOW);
  delay(1);
}

/*
 * Function to controll the brigthness of the LEDs when long is input
 * Using bitshift to adjust brightness
 */
long changeBrightnessLong(long l) {
  byte r = l >> 16;
  byte g = l >> 8;
  byte b = l;
  r = r >> brightnessLevel;
  g = g >> brightnessLevel;
  b = b >> brightnessLevel;
  long rgb = r << 16 | g << 8 | b;
  return rgb;
}

/*
 * Function to controll the brigthness of the LEDs when byte is input
 * Using bitshift to adjust brightness
 */
byte changeBrightnessByte(byte b) {
  b = b >> brightnessLevel;
  return b;
}

/*
 * Function to write on the serial port the current mode at a given time
 */
void writeMode(){
  if(millis() > modeTime + 1000){
    modeTime += 1000;
    if(digitalRead(DEMO)){
      Serial.println("demo");
    }
    else{
      Serial.println("serial");
    }
  }
}

