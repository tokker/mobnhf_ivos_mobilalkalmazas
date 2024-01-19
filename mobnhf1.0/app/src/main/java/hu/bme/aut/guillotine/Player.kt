package hu.bme.aut.guillotine

import java.io.Serializable

data class Player (
    var name: String = "",
    var cards : ArrayList<Card> = ArrayList(5),
    var drinks : Int = 0,
    var drinksGiven : Int = 0,
    var turn : Int = 0
) : Serializable