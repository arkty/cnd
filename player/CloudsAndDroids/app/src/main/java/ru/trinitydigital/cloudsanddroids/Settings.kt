package ru.trinitydigital.cloudsanddroids

class Settings() {
    var max_hp: Int = 0
    var max_mana: Int = 0
    
    constructor(max_hp: Int,
                max_mana: Int) : this() {
        this.max_hp = max_hp
        this.max_mana = max_mana
    }
}