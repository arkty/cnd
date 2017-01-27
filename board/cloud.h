#ifndef SERVER_H
#define SERVER_H

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include "properties.h"

class Cloud {
  public:
  Cloud();
  void init();
  void beginBattle(String battleId, int player);
  int writeTurn(String cardUid);
  
  private:
    String battleId;
    int turnNumber = 0;
};

#endif
