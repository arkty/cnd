#include "cloud.h"

Cloud::Cloud() {

}

void Cloud::init() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  pinMode(ERROR_PIN, OUTPUT);
  pinMode(NO_MANA_PIN, OUTPUT);
  Firebase.begin(FIREBASE_HOST);
}

void Cloud::beginBattle(String battleId, int player) {
  this->battleId = battleId;
  this->turnNumber = player;
  
  Firebase.setInt("battles/" + battleId + "/states/" + player + "/hp", 50);
  if(firebaseFailed()) return;  

  Firebase.setInt("battles/" + battleId + "/states/" + player + "/mana", 50);
  if(firebaseFailed()) return;     
  
  Firebase.setInt("battles/" + battleId + "/last_turn", -1);
  if(firebaseFailed()) return;     
}

int Cloud::writeTurn(String cardUid, int player) {
  Serial.println("NUMBER0");
  turnNumber = Firebase.getInt("battles/" + battleId + "/last_turn");
  turnNumber = turnNumber + 1;
  
  Serial.println("NUMBER1");
  int sourcePlayer = turnNumber % 2;
  Serial.println(sourcePlayer);

  Serial.println("NUMBER");

  int playerMana = Firebase.getInt("battles/" + battleId + "/states/" + sourcePlayer + "/mana");
  if(firebaseFailed()) return 1;     

  int cardMana = Firebase.getInt("cards/" + cardUid + "/mana");
  if(firebaseFailed()) return 1; 
    
  if(cardMana > playerMana) {
    digitalWrite(NO_MANA_PIN, HIGH);
    delay(1000);
    digitalWrite(NO_MANA_PIN, LOW);
    return 2;
  }
  
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& turn = jsonBuffer.createObject();  
  turn["card"] = cardUid;
  turn["target"] = player;
  
  Firebase.setInt("battles/" + battleId + "/last_turn/", turnNumber);  
  if(firebaseFailed()) return 1;     

  Firebase.set("battles/" + battleId + "/turns/" + turnNumber, turn);
  if(firebaseFailed()) return 1;     
  
  return 0;
}

int Cloud::firebaseFailed() {
  if (Firebase.failed()) {
     digitalWrite(ERROR_PIN, HIGH);
     Serial.print("setting or getting failed:");
     Serial.println(Firebase.error());
     delay(1000);
     digitalWrite(ERROR_PIN, LOW);
     return 1;
  }
  return 0;
}

