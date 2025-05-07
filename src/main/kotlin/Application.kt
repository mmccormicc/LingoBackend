package com.example

import com.example.network.QuizRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()


    val config = HikariConfig().apply {
        jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:mysql://metro.proxy.rlwy.net:13333/railway"
        username = System.getenv("DB_USER") ?: "root"
        password = System.getenv("DB_PASSWORD") ?: "errorpassword"
        driverClassName = "com.mysql.cj.jdbc.Driver"
    }

    val dataSource = HikariDataSource(config)
    val repository = QuizRepository(dataSource)

    configureRouting(repository)
}
