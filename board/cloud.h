#ifndef SERVER_H
#define SERVER_H

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include "properties.h"
#include "pinout.h"

#define ERROR_PIN D2
#define NO_MANA_PIN D8

class Cloud {
  public:
  Cloud();
  void init();
  void beginBattle(String battleId, int player);
  int writeTurn(String cardUid, int player);
  
  private:
    String battleId;
    int turnNumber = 0;
    int firebaseFailed();
};

#endif
