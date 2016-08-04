int SDI = 2;
int CKI = 3;
int DEMO = 4;
int DEMO_MODE = 5;
int brightnessPin = A0;
int demoState = 0;
int lastState = 0;
int brightnessLevel = 1;
int logoPosX = 0;
int logoPosY = 0;
int speedX = 1;
int speedY = 1;
int swipePixel = 0;
int byteReceived = 0;


const int BOUNCE_LOGO = 0;
const int RANDOM_COLOR = 1;
const int COLOR_SWIPE = 2;
const int FILL_RANDOM = 3;
const int CHANGE_STATE = 4;
const int PICTURE_Y = 40;
const int PICTURE_X = 80;

boolean lastDemoMode = false;
boolean lastDemo = false;
boolean updateScreen = false;
boolean initColorSwipe = false;
boolean initFillRandom = false;
boolean initBounceLogo = false;

long pictureArray[3200];


long updateTime = 40;

long color = 0xFFFFFF;

unsigned long modeTime = 0;
unsigned long nextUpdateTime = 0;
unsigned long nextRandomColor = 0;

long swipeColors[768];
long pictureMatrix[80][40];
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

void setup() {
  pinMode(SDI, OUTPUT);
  pinMode(CKI, OUTPUT);
  pinMode(13, OUTPUT);
  pinMode(DEMO, INPUT);
  pinMode(DEMO_MODE, INPUT);
  pinMode(brightnessPin, INPUT);
  Serial.begin(38400);
  clearMatrix();
  randomSeed(analogRead(A1));
  nextUpdateTime = millis();
  modeTime = millis();
}

void loop() {
  writeMode();
  int level = analogRead(brightnessPin);
  brightnessLevel = map(level, 0, 1023, 0, 3);
  if (millis() >= nextUpdateTime) {
    updateScreen = true;
    nextUpdateTime += updateTime;
    digitalWrite(13, HIGH);
  }
  else {
    updateScreen = false;
    digitalWrite(13, LOW);
  }
  if (digitalRead(DEMO)) {
    demo();
    if (updateScreen) {
      writeScreen();
    }
  }
  else {
    if (Serial.available() == 0) {
    }
    else {
      byte in = Serial.read();
      in = changeBrightnessByte(in);
      byteWriter(in);
      byteReceived++;
      if(byteReceived == 9600){
        digitalWrite(CKI, LOW);
        delay(1);
        byteReceived = 0;
      }
    }
  }
}

void demo() {
  if (digitalRead(DEMO_MODE) & ! lastDemoMode) {
    lastState = demoState;
    demoState = CHANGE_STATE;
  }
  lastDemoMode = digitalRead(DEMO_MODE);
  switch (demoState) {
    case BOUNCE_LOGO:
      bounceLogo();
      break;

    case RANDOM_COLOR:
      randomColor();
      break;

    case COLOR_SWIPE:
      colorSwipe();
      break;

    case FILL_RANDOM:
      fillRandom();
      break;

    case CHANGE_STATE:
      switch (lastState) {
        case BOUNCE_LOGO:
          clearMatrix();
          demoState = RANDOM_COLOR;
          break;

        case RANDOM_COLOR:
          clearMatrix();
          demoState = COLOR_SWIPE;
          break;

        case COLOR_SWIPE:
          initFillRandom = false;
          clearMatrix();
          demoState = FILL_RANDOM;
          break;

        case FILL_RANDOM:
          clearMatrix();
          initBounceLogo = false;
          speedX = 1;
          speedY = 1;
          demoState = BOUNCE_LOGO;
          break;
      }
      break;
  }
}

void byteWriter(byte b) {
  byte in = changeBrightnessByte(b);
  for (byte colorBit = 7; colorBit != 255; colorBit--) {
    digitalWrite(CKI, LOW);
    byte mask = 1 << colorBit;
    if (in & mask) {
      digitalWrite(SDI, HIGH);
    }
    else {
      digitalWrite(SDI, LOW);
    }
    digitalWrite(CKI, HIGH);
  }
}

void longWriter(long l) {
  long rgb = changeBrightnessLong(l);
  for (byte colorBit = 23; colorBit != 255; colorBit--) {
    digitalWrite(CKI, LOW);
    long mask = 1L << colorBit;
    if (rgb & mask) {
      digitalWrite(SDI, HIGH);
    }
    else {
      digitalWrite(SDI, LOW);
    }
    digitalWrite(CKI, HIGH);
  }
}

void clearMatrix() {
  for (int y = 0; y < 40; y++) {
    for (int x = 0; x < 80; x++) {
      pictureMatrix[x][y] = 0x000000;
    }
  }
}

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

void colorSwipe() {
  if (!initColorSwipe) {
    byte rgb[3] = {255, 0, 0};
    int pixel = 0;
    for (int decColor = 0; decColor < 3; decColor++) {
      int incColor = decColor == 2 ? 0 : decColor++;
      for (int i = 0; i < 256; i++, pixel++) {
        swipeColors[pixel] = rgb[0] << 16 | rgb[1] << 8 | rgb[2];
        rgb[decColor]--;
        rgb[incColor]++;
      }
    }
    initColorSwipe = true;
  }
  if (updateScreen) {
    int swipe = swipePixel;
    for (int x = 0; x < 80; x++, swipe++) {
      if (swipe == 756) {
        swipe = 0;
      }
      for (int y = 0; y < 40; y++) {
        pictureMatrix[x][y] = swipeColors[swipe];
      }
    }
    swipePixel = swipePixel == 757 ? 0 : swipePixel++;
  }
}

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

void writeScreen() {
  setPictureArray();
  for (int x = 0; x < 3200; x++) {
    longWriter(pictureArray[x]);
  }
  digitalWrite(CKI, LOW);
  delay(1);
}

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

byte changeBrightnessByte(byte b) {
  b = b >> brightnessLevel;
  return b;
}

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

