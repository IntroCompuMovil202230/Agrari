package com.example.agrari.Model

data class Chat(
    var id: String = "",
    var name: String = "",
    var users: List<String> = emptyList()
)
