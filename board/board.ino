#include "pinout.h"
#include "scanner.h"
#include "server.h"

#define RST_PIN   D4
#define SS_PIN    D3

const String NEW_GAME_UID = "35:74:d6:65";
const int PLAYER = 1;
Scanner scanner(SS_PIN, RST_PIN);
Cloud cloud;

void setup() {
  Serial.begin(9600);   // Initialize serial communications with the PC
  
  scanner.init();  
  cloud.init();
  Serial.println("Board initialized!");
  pinMode(D0, OUTPUT);
  pinMode(D1, OUTPUT);
}

void loop() {
  
  String card = scanner.readCard();  

  if(card.length() > 0) {

    Serial.println(card);    
    
    // new battle
    if(NEW_GAME_UID.equals(card)) {
      Serial.println("New game started!");
      cloud.beginBattle(card, PLAYER);
      digitalWrite(D1, HIGH);
      delay(2000);
      digitalWrite(D1, LOW);
    } else {
      cloud.writeTurn(card);
      digitalWrite(D0, HIGH);
      delay(2000);
      digitalWrite(D0, LOW);
    }        
  }
}

void onRfidFound() {
  
}

