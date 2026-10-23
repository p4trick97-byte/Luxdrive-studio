package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LuxDriveDatabase
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectOrigin
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VideoProjectRepositoryTest {

    private lateinit var database: LuxDriveDatabase
    private lateinit var repository: VideoProjectRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LuxDriveDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = VideoProjectRepository(database.videoProjectDao())
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun saveAndRetrieveAiGeneratedProject_persistsOriginAndAutomotiveFields() = runBlocking {
        val project = VideoProject(
            title = "Lamborghini Revuelto V12 Ibrida",
            category = AutomotiveCategory.SUPERCAR_AND_HYPERCAR.displayName,
            format = ProjectFormat.LONG_VIDEO.name,
            status = ProjectStatus.DRAFT.name,
            hook = "3 motori elettrici e un V12 aspirato da 1015 CV.",
            fullScript = "Oggi su LuxDrive analizziamo l'erede della Aventador...",
            origin = ProjectOrigin.AI_GENERATED.name,
            carModel = "Lamborghini Revuelto",
            contentGoal = "Analisi ingegneristica telaio e powertrain",
            narrativeTone = "Tecnico & Emozionale",
            userNotes = "Confrontare con Ferrari SF90 Stradale"
        )

        val id = repository.saveProject(project)
        assertTrue(id > 0)

        val retrieved = repository.getProjectById(id)
        assertNotNull(retrieved)
        assertEquals("Lamborghini Revuelto V12 Ibrida", retrieved?.title)
        assertEquals(ProjectOrigin.AI_GENERATED.name, retrieved?.origin)
        assertEquals("Lamborghini Revuelto", retrieved?.carModel)
        assertEquals("Tecnico & Emozionale", retrieved?.narrativeTone)

        val all = repository.allProjects.first()
        assertEquals(1, all.size)
        assertEquals("Lamborghini Revuelto V12 Ibrida", all[0].title)
    }

    @Test
    fun duplicateProject_setsOriginToUserCreatedAndCopiesContent() = runBlocking {
        val original = VideoProject(
            title = "Maserati MC20 Cielo",
            category = AutomotiveCategory.LEGENDARY_AND_RARE_CARS.displayName,
            format = ProjectFormat.LONG_VIDEO.name,
            status = ProjectStatus.READY.name,
            origin = ProjectOrigin.AI_GENERATED.name,
            hook = "V6 Nettuno a precamera di combustione.",
            fullScript = "Analisi completa...",
            carModel = "Maserati MC20"
        )

        val originalId = repository.saveProject(original)
        val copyId = repository.duplicateProject(originalId)

        assertNotNull(copyId)
        val duplicate = repository.getProjectById(copyId!!)
        assertNotNull(duplicate)
        assertEquals("Maserati MC20 Cielo (Copia)", duplicate?.title)
        assertEquals(ProjectOrigin.USER_CREATED.name, duplicate?.origin)
        assertEquals(ProjectStatus.DRAFT.name, duplicate?.status)
    }
}
