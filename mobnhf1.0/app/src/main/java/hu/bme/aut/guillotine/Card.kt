package hu.bme.aut.guillotine

import java.io.Serializable

data class Card (
    var colour: Int = 0,
    var number: Int = 0
) : Serializable