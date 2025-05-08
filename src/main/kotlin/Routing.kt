package com.example

import com.example.data.QuizScore
import com.example.network.QuizRepository
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repository: QuizRepository) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/deviceid") {
            val deviceid = call.receive<String>()
            val success = repository.submitDeviceId(deviceid)
            call.respond(mapOf("success" to success))
        }

        post("/score") {
            val score = call.receive<QuizScore>()
            val success = repository.submitScore(score)
            call.respond(mapOf("success" to success))
        }

        get("/score") {
            val deviceId = call.request.queryParameters["deviceId"] ?: return@get call.respondText("Missing deviceId", status = io.ktor.http.HttpStatusCode.BadRequest)
            val language = call.request.queryParameters["language"] ?: return@get call.respondText("Missing language", status = io.ktor.http.HttpStatusCode.BadRequest)
            val quizName = call.request.queryParameters["quizName"] ?: return@get call.respondText("Missing quizName", status = io.ktor.http.HttpStatusCode.BadRequest)

            val score = repository.getScore(deviceId, language, quizName)
            if (score != null) {
                call.respond(score)
            } else {
                call.respondText("Score not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
