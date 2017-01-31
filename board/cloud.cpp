#include "server.h"

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
  
  Firebase.begin(FIREBASE_HOST);
}

void Cloud::beginBattle(String battleId, int player) {
  this->battleId = battleId;
  this->turnNumber = player;
  
  Firebase.setInt("battles/" + battleId + "/states/" + player + "/hp/", 50);
  
  if (Firebase.failed()) {
     Serial.print("setting /number failed:");
     Serial.println(Firebase.error());  
     return;     
  }

  Firebase.setInt("battles/" + battleId + "/states/" + player + "/mana/", 50);

  if (Firebase.failed()) {
     Serial.print("setting /number failed:");
     Serial.println(Firebase.error());  
     return ;     
  }
}

int Cloud::writeTurn(String cardUid) {
  Firebase.setString("battles/" + battleId + "/turns/" + turnNumber + "/card", cardUid);
  if (Firebase.failed()) {
     Serial.print("setting /number failed:");
     Serial.println(Firebase.error());  
     return 1;     
  }
  turnNumber = turnNumber + 2;
  return 0;
}

