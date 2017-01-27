#ifndef SCANNER_H
#define SCANNER_H

#include <SPI.h>
#include <MFRC522.h>
#include "pinout.h"

class Scanner {
  
  private:
    MFRC522* rc522;
    
  public: 
    Scanner(int ssPin, int rstPin);
    void init();
    String readCard();
};

#endif
