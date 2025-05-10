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
        post("/deviceid") {
            // Receiving deviceId
            var deviceId = call.receive<String>()
            // Removing quotes that are added in transit
            deviceId = deviceId.replace("\"", "")

            // Submitting deviceId with repository
            val success = repository.submitDeviceId(deviceId)
            // Responding with mapped success boolean
            call.respond(mapOf("success" to success))
        }

        post("/score") {
            // Receiving QuizScore
            val score = call.receive<QuizScore>()

            // Submitting score with repository
            val success = repository.submitScore(score)
            // Responding with mapped success boolean
            call.respond(mapOf("success" to success))
        }

        get("/score") {
            // Receiving parameters used to query for quiz score
            val deviceId = call.request.queryParameters["deviceId"] ?: return@get call.respondText("Missing deviceId", status = io.ktor.http.HttpStatusCode.BadRequest)
            val language = call.request.queryParameters["language"] ?: return@get call.respondText("Missing language", status = io.ktor.http.HttpStatusCode.BadRequest)
            val quizName = call.request.queryParameters["quizName"] ?: return@get call.respondText("Missing quizName", status = io.ktor.http.HttpStatusCode.BadRequest)

            // Getting score
            val score = repository.getScore(deviceId, language, quizName)

            // If score exists
            if (score != null) {
                // Respond with score
                call.respond(score)
            } else {
                // Respond with error message
                call.respondText("Score not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
