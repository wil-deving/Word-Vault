package com.gmail.wil.myownvocabulary.db

object Constants {

    // TABLE itemsvocabulary
    val ITEMS_VOCABULARY_TABLE = "itemsvocabulary"
    // COLUMNS FOR ITEMS VOCABULARY
    val VOCABULARY_ID_ITEM = "_iditemvocabulary"
    val VOCABULARY_NAME_ITEM = "name"
    val VOCABULARY_LEARNED_ITEM = "learned"

    // TABLE meanings
    val MEANINGS_TABLE = "meanings"
    // COLUMNS FOR MEANINGS
    val MEANING_ID = "_idmeaning"
    val MEANING_VOCABULARY_ID_ITEM = "iditemvocabulary"
    val MEANING_DESC_ONE = "descriptionone"
    val MEANING_DESC_TWO = "descriptiontwo"

}