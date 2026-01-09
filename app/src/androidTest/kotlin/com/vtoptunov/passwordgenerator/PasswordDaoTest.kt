package com.vtoptunov.passwordgenerator

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vtoptunov.passwordgenerator.data.local.dao.PasswordDao
import com.vtoptunov.passwordgenerator.data.local.database.AppDatabase
import com.vtoptunov.passwordgenerator.data.local.entity.PasswordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PasswordDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var passwordDao: PasswordDao

    @Before
    fun setup() {
        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        passwordDao = database.passwordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPassword_and_retrieve() = runBlocking {
        val password = PasswordEntity(
            password = "SecurePass123!",
            website = "example.com",
            username = "testuser",
            category = "Social",
            createdAt = System.currentTimeMillis(),
            entropy = 65.5
        )

        val id = passwordDao.insertPassword(password)
        val retrieved = passwordDao.getPasswordById(id)

        assertNotNull(retrieved)
        assertEquals(password.password, retrieved?.password)
        assertEquals(password.website, retrieved?.website)
        assertEquals(password.username, retrieved?.username)
    }

    @Test
    fun getAllPasswords_returnsAllInserted() = runBlocking {
        val password1 = PasswordEntity(
            password = "Pass1",
            website = "site1.com",
            username = "user1",
            category = "Work",
            createdAt = System.currentTimeMillis(),
            entropy = 50.0
        )

        val password2 = PasswordEntity(
            password = "Pass2",
            website = "site2.com",
            username = "user2",
            category = "Personal",
            createdAt = System.currentTimeMillis(),
            entropy = 60.0
        )

        passwordDao.insertPassword(password1)
        passwordDao.insertPassword(password2)

        val allPasswords = passwordDao.getAllPasswords().first()

        assertEquals(2, allPasswords.size)
        assertTrue(allPasswords.any { it.website == "site1.com" })
        assertTrue(allPasswords.any { it.website == "site2.com" })
    }

    @Test
    fun deletePassword_removesFromDatabase() = runBlocking {
        val password = PasswordEntity(
            password = "TempPass",
            website = "temp.com",
            username = "temp",
            category = "Other",
            createdAt = System.currentTimeMillis(),
            entropy = 45.0
        )

        val id = passwordDao.insertPassword(password)
        val inserted = passwordDao.getPasswordById(id)
        assertNotNull(inserted)

        passwordDao.deletePassword(inserted!!)

        val afterDelete = passwordDao.getPasswordById(id)
        assertNull(afterDelete)
    }

    @Test
    fun searchPasswords_findsMatchingWebsite() = runBlocking {
        passwordDao.insertPassword(
            PasswordEntity(
                password = "Pass1",
                website = "github.com",
                username = "dev1",
                category = "Work",
                createdAt = System.currentTimeMillis(),
                entropy = 55.0
            )
        )

        passwordDao.insertPassword(
            PasswordEntity(
                password = "Pass2",
                website = "gitlab.com",
                username = "dev2",
                category = "Work",
                createdAt = System.currentTimeMillis(),
                entropy = 58.0
            )
        )

        val searchResults = passwordDao.searchPasswords("git").first()

        assertEquals(2, searchResults.size)
        assertTrue(searchResults.all { it.website.contains("git") })
    }
}

