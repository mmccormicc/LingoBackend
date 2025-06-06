package com.example.network

import com.example.data.QuizScore
import javax.sql.DataSource

class QuizRepository(private val dataSource: DataSource) {

    fun submitDeviceId(deviceId: String): Boolean {
        dataSource.connection.use { conn ->
            // Statement to insert device_id into devices
            // If device already exists just keep it the same. This is needed to prevent a crash.
            val stmt = conn.prepareStatement("""
                INSERT INTO devices (device_id, role) 
                VALUES (?, 'user') 
                ON DUPLICATE KEY UPDATE device_id = VALUES(device_id)
            """)
            // Assigning value for device_id
            stmt.setString(1, deviceId)
            // Returns true if any rows were affected by update
            return stmt.executeUpdate() > 0
        }
    }

    fun submitScore(score: QuizScore): Boolean {
        dataSource.connection.use { conn ->
            // SQL statement to insert new score into quiz table.
            // If entry with same device_id, language, and quiz_name already exists, only update if new score is greater
            val stmt = conn.prepareStatement("""
                INSERT INTO quiz_scores (device_id, language, quiz_name, score)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY 
                UPDATE score = IF(VALUES(score) > score, VALUES(score), score)
            """)
            // Assigning values to sql statement
            stmt.setString(1, score.deviceId)
            stmt.setString(2, score.language)
            stmt.setString(3, score.quizName)
            stmt.setInt(4, score.score)
            // Returns true if any rows were affected by update
            return stmt.executeUpdate() > 0
        }
    }

    fun getScore(deviceId: String, language: String, quizName: String): QuizScore? {
        dataSource.connection.use { conn ->
            // SQL statement to retrieve a row from quiz_scores that has matching score info
            val stmt = conn.prepareStatement("""
                SELECT * FROM quiz_scores 
                WHERE device_id = ? AND language = ? AND quiz_name = ?
            """)
            // Assigning values to statement
            stmt.setString(1, deviceId)
            stmt.setString(2, language)
            stmt.setString(3, quizName)

            val rs = stmt.executeQuery()
            // If result was found
            return if (rs.next()) {
                // Returning constructed quiz score object
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