#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <FirebaseESP8266.h>
#include <ArduinoJson.h>
#include <SoftwareSerial.h>
#include <Servo.h>
#include <ESP8266WiFi.h>
#define FIREBASE_HOST "intelligentstove.firebaseio.com"
#define FIREBASE_AUTH "JQ8u5xU5bbIUuEL719xXWoKSqE612s6lLGNNZeTJ"
#define WIFI_SSID "OnePlus 5T"
#define WIFI_PASSWORD "87654321"
//#define WIFI_SSID "GBK"
//#define WIFI_PASSWORD "8ha77a1997"
//#define WIFI_SSID "GBKSTUDIOS"
//#define WIFI_PASSWORD "gbk12345"


Servo myservo;  // create servo object to control a servo
FirebaseData firebaseData;
int dq1=5;
int dq2=4;
int dq5=14;
int In1 = 13;
int In2 = 15;
 
#define DHTTYPE DHT11
DHT dht(dq1, DHTTYPE);

//int dq2val=0;
//SoftwareSerial mySerial(12,13); 
void setup() {
  //mySerial.begin(115200);
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID,WIFI_PASSWORD);
 Firebase.begin(FIREBASE_HOST,FIREBASE_AUTH);
 Firebase.reconnectWiFi(true);
  dht.begin();
  pinMode(dq1, INPUT);
  pinMode(dq2, INPUT);// Initialize the LED_BUILTIN pin as an output
  pinMode(D3,OUTPUT);
  pinMode(D6,OUTPUT);
  pinMode(dq5, OUTPUT);
  pinMode(In1, OUTPUT);
  pinMode(In2, OUTPUT);
myservo.attach(2);

}

// the loop function runs over and over again forever
void loop() {
int dq1val = 0;
dq1val = digitalRead(dq2);
float h = dht.readHumidity();
float t = dht.readTemperature();
float f= dht.readTemperature(true);
delay(1000);
Serial.println("Digital D0 value of MQ6 Sensor");
Serial.println(dq1val);
  String jsonStr;
  int pos;
pos = myservo.read();
//if (Firebase.setString(firebaseData,"/servo", String(pos)))
// {
//      Serial.println("Servo value updated");
//      
// }
 if(!isnan(t) && !isnan(h)){
  Serial.println("Surrounding Temp");
  Serial.println(t);
  Serial.println("Surrounding Temp");
  Serial.println(f);
  Serial.println("Humidity in the system");
  Serial.println(h);
  if (Firebase.setFloat(firebaseData,"/temperature", t))
 {
      Serial.println("Temperature value updated"); 
 }
  if (Firebase.setFloat(firebaseData,"/humidity", h))
 {
      Serial.println("Humidity value updated"); 
 }
 }

if(pos == 0){  //Stove Off
  Serial.println("-----------------------------------");
  Serial.println("----------Stove Status-----------");
  Serial.println("-----------------------------------");
  Serial.println();
  //Updating Stove Status To OFF as servo pos=0
    if (Firebase.setString(firebaseData,"/stoveStatus","OFF"))
    {
      Serial.println("----------Set result-----------");
      Serial.println("PATH: " + firebaseData.dataPath());
      Serial.println("TYPE: " + firebaseData.dataType());
      Serial.print("VALUE: ");
      Serial.println(firebaseData.stringData());
      Serial.println("--------------------------------");
      Serial.println();
      
    }
    else
    {
      Serial.println("----------Can't set data--------");
      Serial.println("REASON: " + firebaseData.errorReason());
      Serial.println("--------------------------------");
      Serial.println();
    }
    //Check gas leakage
      dq1val = digitalRead(dq2);
      delay(1000);
      if(dq1val == 0){
          mqDetector(dq1val);
        }
       else{
         mqDetector(dq1val);
        }  
  }
 else  //Stove ON
 {
   if (Firebase.setString(firebaseData,"/stoveStatus","ON"))
    {
      Serial.println("----------Set result-----------");
      Serial.println("PATH: " + firebaseData.dataPath());
      Serial.println("TYPE: " + firebaseData.dataType());
      Serial.print("VALUE: ");
      Serial.println(firebaseData.stringData());
      Serial.println("--------------------------------");
      Serial.println();
      
    }
    else
    {
      Serial.println("----------Can't set data--------");
      Serial.println("REASON: " + firebaseData.errorReason());
      Serial.println("--------------------------------");
      Serial.println();
    }
    //Check gas leakage
      dq1val = digitalRead(dq2);
      delay(1000);
      if(dq1val == 0){
          mqDetector(dq1val);
        }
       else{
        if (Firebase.getString(firebaseData,"/servo"))
         {
          Serial.println("----------Get Servo value-----------");
          Serial.println("PATH: " + firebaseData.dataPath());
          Serial.println("TYPE: " + firebaseData.dataType());
          Serial.print("VALUE: ");
          Serial.println(firebaseData.stringData());
          pos = firebaseData.stringData().toInt();
          Serial.println(pos);
          myservo.write(pos);     
         }
         mqDetector(dq1val);

         
        }  
  }

if (Firebase.getString(firebaseData,"/servo")){
  Serial.println("----------Get result-----------");
      Serial.println("PATH: " + firebaseData.dataPath());
      Serial.println("TYPE: " + firebaseData.dataType());
      Serial.print("VALUE: ");
      Serial.println(firebaseData.stringData());
      pos = firebaseData.stringData().toInt();
      Serial.println( pos);
      myservo.write(pos);
    }
}

void mqDetector(int val)
{
  int pos = 0;
  //1-No gas leakage
  //0- gas leakage detected
  // turn on motor A
   digitalWrite(In1,HIGH);
//   digitalWrite(In1, HIGH);
//   digitalWrite(In2, LOW);
  // set speed to 150 out 255
  // analogWrite(EnA, 200);
  if(val == 0)
 {
   digitalWrite(D3,HIGH);//buzzer
   digitalWrite(D6,HIGH);
//   // turn on motor A
//   digitalWrite(EnA,HIGH);
//   digitalWrite(In1, HIGH);
//   digitalWrite(In2, LOW);
//  // set speed to 150 out 255
//   analogWrite(EnA, 200);
   myservo.write(20);
   if(Firebase.setString(firebaseData,"/leakage" , "detected"))
   {
       Serial.println("Gas leakage detected");
   }
   if (Firebase.setInt(firebaseData,"/MQ6",val))
   {
      Serial.println("MQ6 value updated in firebase");
   }
   if (Firebase.setString(firebaseData,"/servo", String(0))){
    Serial.println("servo value updated in firebase");
   }
   pos =  myservo.read();
   Serial.println("-----------------------------------");
   Serial.print("----------Servo Motor Position:");
   Serial.println(pos);
   Serial.println("-----------------------------------");
   Serial.println();
 }
 else 
 {
  digitalWrite(D3,LOW);
  digitalWrite(D6,LOW);
  if(Firebase.setString(firebaseData,"/leakage" , "notDetected"))
   {
       Serial.println("NO Gas leakage detected");
   }
   if (Firebase.setInt(firebaseData,"/MQ6",val))
   {
      Serial.println("MQ6 value updated in firebase");
   }
  pos =  myservo.read();
  Serial.println("-----------------------------------");
  Serial.print("----------Servo Motor Position:");
  Serial.println(pos);
  Serial.println("-----------------------------------");
  Serial.println();
 } 
}
