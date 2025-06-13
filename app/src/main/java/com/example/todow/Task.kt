package com.example.todow

data class Task(
    var id: String = "",
    var titre: String = "",
    var date: String = "",
    var time: String = "",
    var is_done: Boolean = false
)

