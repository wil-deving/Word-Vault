package com.gmail.wil.myownvocabulary.managers

import com.gmail.wil.myownvocabulary.model.Meaning

fun randomAlphanumericString(): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val outputStrLength = (1..36).shuffled().first()
    return (1..outputStrLength)
        .map{ kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}

fun compareMeanings(textOne: String = "", textTwo: String = "") : Boolean {
    val toCaseTextOne = textOne.trim().toUpperCase()
    val toCaseTextTwo = textTwo.trim().toUpperCase()
    if (toCaseTextOne == toCaseTextTwo) return true
    else {
        val sptTextOne = toCaseTextOne.split(" ")
        val sptTextTwo = toCaseTextTwo.split(" ")
        val calculateWeightByWord = ((1.00 / sptTextOne.size.toDouble()) * 100.00 )
        var sumWeightTotal = 0.00
        for (wordInTwo in sptTextTwo) {
            if (wordInTwo in sptTextOne) sumWeightTotal += calculateWeightByWord
            else sumWeightTotal -= (calculateWeightByWord / 25.00)
        }
        println("------->" + sumWeightTotal)
        return (sumWeightTotal > 25.00)
    }
}

fun messArrayToList(corrects: ArrayList<Meaning>, wrongs: ArrayList<Meaning>) : ArrayList<Meaning> {
    var finalArrayList = ArrayList<Meaning>()
    finalArrayList.addAll(corrects)
    // TODO selccionar todas las malas elegir 3 aleatoriamente y
    //  adicionar al array de correctos
    // y retornar

    finalArrayList.addAll(wrongs)

    // Desordenar todo el array

    return finalArrayList
}