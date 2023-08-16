package com.gmail.wil.myownvocabulary.model

class Meaning(val id_meaning: String,
              val id_item_vocabulary: String,
              val original_description: String,
              val secundary_description: String,
              val meaningType: String) {

    constructor(id_meaning: String,
                id_item_vocabulary: String,
                original_description: String,
                secundary_description: String) :
            this(id_meaning, id_item_vocabulary, original_description, secundary_description, "") {
    }

}