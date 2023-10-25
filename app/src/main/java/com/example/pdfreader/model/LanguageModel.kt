package com.example.pdfreader.model

class LanguageModel (
    private val id: Int,
    private val name: String,
    private val code: String
) {
    fun getId() : Int {
        return id
    }
    fun getName(): String {
        return name
    }
    fun getCode(): String {
        return code
    }
}