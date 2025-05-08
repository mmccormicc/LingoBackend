package com.example.network

import com.example.data.QuizScore
import javax.sql.DataSource

class QuizRepository(private val dataSource: DataSource) {

    fun submitDeviceId(deviceId: String): Boolean {
        dataSource.connection.use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO devices (device_id, role) 
                VALUES (?, 'user') 
                ON DUPLICATE KEY UPDATE device_id = VALUES(device_id)
            """)
            stmt.setString(1, deviceId)
            return stmt.executeUpdate() > 0
        }
    }

    fun submitScore(score: QuizScore): Boolean {
        dataSource.connection.use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO quiz_scores (device_id, language, quiz_name, score)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE score = VALUES(score)
            """)
            stmt.setString(1, score.deviceId)
            stmt.setString(2, score.language)
            stmt.setString(3, score.quizName)
            stmt.setInt(4, score.score)
            return stmt.executeUpdate() > 0
        }
    }

    fun getScore(deviceId: String, language: String, quizName: String): QuizScore? {
        dataSource.connection.use { conn ->
            val stmt = conn.prepareStatement("""
                SELECT * FROM quiz_scores 
                WHERE device_id = ? AND language = ? AND quiz_name = ?
            """)
            stmt.setString(1, deviceId)
            stmt.setString(2, language)
            stmt.setString(3, quizName)
            val rs = stmt.executeQuery()
            return if (rs.next()) {
                QuizScore(
                    deviceId = rs.getString("device_id"),
                    language = rs.getString("language"),
                    quizName = rs.getString("quiz_name"),
                    score = rs.getInt("score")
                )
            } else null
        }
    }
}