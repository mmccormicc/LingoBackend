package com.example

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization() {
    // Using Gson for deserialization
    install(ContentNegotiation) {
        gson {
            }
    }
}
