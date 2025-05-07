package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class QuizScore(
    val deviceId: String,
    val language: String,
    val quizName: String,
    val score: Int
)