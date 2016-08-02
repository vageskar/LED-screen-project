int SDI = 2;
int CKI = 3;
int brightnessPin = A0;
long timeout;
int brightnessLevel = 1;
long pictureMatrix[80][40];
long NTNU[8][8] ={
  {0x000000, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF}, 
  {0x0000FF, 0x0000FF, 0x000000, 0x000000, 0x000000, 0x000000, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF}, 
  {0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF, 0x000000, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x000000, 0x000000, 0x000000, 0x000000, 0x0000FF, 0x0000FF},
  {0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF},
  {0x000000, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF}
};

void setup() {
  pinMode(SDI, OUTPUT);
  pinMode(CKI, OUTPUT);
  pinMode(13, OUTPUT);
  pinMode(brightnessPin, INPUT);
  Serial.begin(19200);
  timeout = micros();
  clearMatrix();
  
}

void loop() {
  int level = analogRead(brightnessPin);
  brightnessLevel = map(level, 0, 1024, 1, 10);
  if(Serial.available() == 0){
  }
  else {
    byte in = Serial.read();
    in *= brightnessLevel;
    byteWriter(in);
  }
}

void demo(){
  
}

void byteWriter(byte b){
  for(byte colorBit = 7; colorBit != 255; colorBit--){
      digitalWrite(CKI, LOW);
      byte mask = 1 << colorBit;
      if(b & mask){
        digitalWrite(SDI, HIGH);
      }
      else{
        digitalWrite(SDI, LOW);
      }
      digitalWrite(CKI, HIGH);
    }
}

void longWriter(long l){
  
}

void clearMatrix(){
  for(int y = 0; y < 40; y++){
    for(int x = 0; x < 80; x++){
      pictureMatrix[x][y] = 0x000000;
    }
  }
}

