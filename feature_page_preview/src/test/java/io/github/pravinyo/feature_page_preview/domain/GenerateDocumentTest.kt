package io.github.pravinyo.feature_page_preview.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class GenerateDocumentTest {
    lateinit var generateDocument: GenerateDocument

    @BeforeEach
    fun setUp() {
        generateDocument = GenerateDocument()
    }

    @Test
    fun `get same file when asked for picture` () {
        val path = ".\\test\\path"
        val file = File(path)

        generateDocument.setFile(file)

        assertNotNull(generateDocument.getPicture())
        assertEquals(path, generateDocument.getPicture()!!.path)
        assertEquals(file, generateDocument.getPicture())
    }
}