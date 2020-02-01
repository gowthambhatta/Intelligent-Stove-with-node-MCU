#include <ESP8266WiFi.h>
int EnA = 5;
int In1 = 4;
int In2 = 0;

void setup() {
  // put your setup code here, to run once:
  pinMode(EnA, OUTPUT);
  pinMode(In1, OUTPUT);
  pinMode(In2, OUTPUT); 
  Serial.begin(115200); 
}

void loop() {
  // put your main code here, to run repeatedly:
    digitalWrite(EnA,HIGH);
   digitalWrite(In1, HIGH);
   digitalWrite(In2, LOW);
   Serial.println("Motor ON");
   delay(1000);
   
   digitalWrite(In1, LOW);
   digitalWrite(In2, LOW);
}
