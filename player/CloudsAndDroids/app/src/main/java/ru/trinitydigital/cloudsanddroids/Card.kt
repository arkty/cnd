package ru.trinitydigital.cloudsanddroids

class Card() {
    var name: String = ""
    var mana: Int = 0

    constructor(name: String,
                mana: Int) : this() {
        this.name = name
        this.mana = mana
    }
}