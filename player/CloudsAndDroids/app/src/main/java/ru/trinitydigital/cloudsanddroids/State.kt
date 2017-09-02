package ru.trinitydigital.cloudsanddroids

class State() {
    var name: String = ""
    var hp: Int = 0
    var mana: Int = 0

    constructor(name: String,
                hp: Int,
                mana: Int) : this() {
        this.name = name
        this.hp = hp
        this.mana = mana
    }
}