package ru.trinitydigital.cloudsanddroids

class Turn() {
    var card: String = ""
    var target: Int = 0

    constructor(card: String,
                target: Int) : this() {
        this.card = card
        this.target = target
    }
}