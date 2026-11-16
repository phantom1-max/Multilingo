package com.multilingo.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonDataTest {
    @Test
    fun everySupportedLanguageHasLessons() {
        listOf("Python", "Java", "JavaScript", "HTML & CSS", "SQL", "C++").forEach { language ->
            assertTrue("$language should have at least one lesson", lessonsFor(language).isNotEmpty())
        }
    }

    @Test
    fun everyLessonHasAValidAnswerAndChoices() {
        listOf("Python", "Java", "JavaScript", "HTML & CSS", "SQL", "C++").forEach { language ->
            lessonsFor(language).forEach { lesson ->
                assertFalse("Lesson title should not be blank", lesson.title.isBlank())
                assertTrue("Lesson needs at least two choices", lesson.choices.size >= 2)
                assertTrue("Answer index must point to a choice", lesson.answer in lesson.choices.indices)
                assertFalse("Prompt should not be blank", lesson.prompt.isBlank())
            }
        }
    }

    @Test
    fun eachLanguageHasThreeStarterLessons() {
        listOf("Python", "Java", "JavaScript", "HTML & CSS", "SQL", "C++").forEach { language ->
            assertEquals("$language should have three starter lessons", 3, lessonsFor(language).size)
        }
    }
}
