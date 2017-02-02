#include "pinout.h"
#include "scanner.h"
#include "cloud.h"

#define RST_PIN   D4
#define SS_PIN    D3

#define PROGRESS_PIN D0

#define READY_PIN D1

const String NEW_GAME_UID = "35:74:d6:65";
const int PLAYER = 0;
Scanner scanner(SS_PIN, RST_PIN);
Cloud cloud;

void setup() {
  Serial.begin(9600);   // Initialize serial communications with the PC
  
  pinMode(PROGRESS_PIN, OUTPUT);
  pinMode(READY_PIN, OUTPUT);
  digitalWrite(PROGRESS_PIN, LOW);    
  digitalWrite(READY_PIN, LOW);    
  
  scanner.init();  
  cloud.init();
  Serial.println("Board initialized!");  
  digitalWrite(READY_PIN, HIGH);    
}

void loop() {
  
  String card = scanner.readCard();  

  if(card.length() > 0) {

    Serial.println(card);    
    
    digitalWrite(PROGRESS_PIN, HIGH);  
    digitalWrite(READY_PIN, LOW);    
  
    // new battle
    if(NEW_GAME_UID.equals(card)) {
      Serial.println("New game started!");      
      cloud.beginBattle(card, PLAYER);      
      
    } else {
      cloud.writeTurn(card, PLAYER);      
    } 

    delay(1000);
    digitalWrite(PROGRESS_PIN, LOW);
    digitalWrite(READY_PIN, HIGH);    
  }
}

