#include "scanner.h"

Scanner::Scanner(int ssPin, int rstPin) {
  rc522 = new MFRC522(ssPin, rstPin);
}

void Scanner::init() {
  SPI.begin();      // Init SPI bus
  rc522->PCD_Init();   // Init MFRC522
  rc522->PCD_SetAntennaGain(rc522->RxGain_max);
}

String Scanner::readCard() {  
  if(rc522->PICC_IsNewCardPresent() && rc522->PICC_ReadCardSerial()) {
    
    String uid = "";
    int uidSize = rc522->uid.size;    
    
    for (byte i = 0; i < uidSize; i++) {
    
      if(i > 0)
        uid = uid + ":";
      
      if(rc522->uid.uidByte[i] < 0x10)
        uid = uid + "0";
      
      uid = uid + String(rc522->uid.uidByte[i], HEX);       
    } 
    return uid;
  }
  return "";
}

