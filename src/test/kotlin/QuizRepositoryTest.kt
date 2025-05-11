package com.example

import com.example.data.QuizScore
import com.example.network.QuizRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.sql.DataSource
import org.h2.jdbcx.JdbcDataSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuizRepositoryTest {

    private lateinit var dataSource: DataSource
    private lateinit var repository: QuizRepository

    @BeforeAll
    fun setupDatabase() {
        // Setting up test database with H2
        val ds = JdbcDataSource()
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
        ds.user = "sa"
        ds.password = ""
        dataSource = ds

        // Creating test tables
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("""
                    CREATE TABLE devices (
                        device_id VARCHAR(255) PRIMARY KEY,
                        role VARCHAR(50)
                    );
                """)
                stmt.execute("""
                    CREATE TABLE quiz_scores (
                        device_id VARCHAR(255),
                        language VARCHAR(50),
                        quiz_name VARCHAR(50),
                        score INT,
                        PRIMARY KEY (device_id, language, quiz_name)
                    );
                """)
            }
        }

        // Creating repository that connects to test dataSource
        repository = QuizRepository(dataSource)
    }

    @Test
    fun getScore_ExistingScore_ReturnsCorrectScore() {

        // Mimicking submitDeviceId function as ON DUPLICATE syntax isn't supported by H2

        // Device info
        val deviceId = "device123"

        // Set up H2 database and execute the query
        dataSource.connection.use { conn ->
            conn.prepareStatement("""
            INSERT INTO devices (device_id, role)
            VALUES (?, 'user');
        """).use { stmt ->
                stmt.setString(1, deviceId)  // Setting the device_id parameter
                stmt.executeUpdate()  // Execute the update/insert
            }
        }


        // Mimicking submitScore function as ON DUPLICATE syntax isn't supported by H2

        // Score info
        val language = "{spanish}"
        val quiz_name = "Noun Quiz"
        val score = 10

        // Set up H2 database and execute the query
        dataSource.connection.use { conn ->
            conn.prepareStatement("""
                INSERT INTO quiz_scores (device_id, language, quiz_name, score)
                VALUES (?, ?, ?, ?);
        """).use { stmt ->
                stmt.setString(1, deviceId)
                stmt.setString(2, language)
                stmt.setString(3, quiz_name)
                stmt.setInt(4, score)
                stmt.executeUpdate()
            }
        }

        // Calling getScore from repository
        val result = repository.getScore(deviceId, language, quiz_name)

        // Asserting result was found
        assertNotNull(result)
        // Asserting retrieved score matches what was submitted
        assertEquals(10, result?.score)

    }

    @Test
    fun getScore_NonexistentScore_ReturnsNull() {
        // Calling getScore on score that doesn't exist
        val result = repository.getScore("nonexistent user", "{french}", "test quiz")
        // Asserting result was not found
        assertEquals(null, result)
    }
}