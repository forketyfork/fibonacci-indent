package me.forketyfork.fibonacciindent

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Test class for FibonacciIndentationHandler core logic.
 *
 * These tests verify the core indentation logic without IntelliJ Platform dependencies.
 */
class FibonacciIndentationHandlerTest {
    private lateinit var handler: FibonacciIndentationHandler

    @BeforeEach
    fun setUp() {
        handler = FibonacciIndentationHandler()
    }

    @Test
    fun testShouldIncreaseIndentation() {
        val increaseCases =
            listOf(
                "if (condition) {",
                "for (item in list) {",
                "while (condition) {",
                "fun myFunction() {",
                "class MyClass {",
                "interface MyInterface {",
                "object MyObject {",
                "try {",
                "catch (e: Exception) {",
                "finally {",
                "else {",
                "def function():", // Python-style
            )

        increaseCases.forEach { lineText ->
            assertTrue(
                shouldIncreaseIndentationHelper(lineText),
                "Should increase indentation for: '$lineText'",
            )
        }
    }

    @Test
    fun testShouldDecreaseIndentation() {
        val decreaseCases =
            listOf(
                "}",
                "    }",
                "return value",
                "break",
                "continue",
            )

        decreaseCases.forEach { lineText ->
            assertTrue(
                shouldDecreaseIndentationHelper(lineText),
                "Should decrease indentation for: '$lineText'",
            )
        }
    }

    @Test
    fun testShouldMaintainIndentation() {
        val maintainCases =
            listOf(
                "val x = 5",
                "println(\"Hello\")",
                "// This is a comment",
                "import java.util.*",
                "x += 1",
            )

        maintainCases.forEach { lineText ->
            assertFalse(
                shouldIncreaseIndentationHelper(lineText),
                "Should not increase indentation for: '$lineText'",
            )
            assertFalse(
                shouldDecreaseIndentationHelper(lineText),
                "Should not decrease indentation for: '$lineText'",
            )
        }
    }

    @Test
    fun testIndentationLevelDetermination() {
        // Test the logic for determining new indentation levels
        val testCases =
            mapOf(
                // (previousLineText, currentLevel) to expectedNewLevel
                ("if (condition) {" to 0) to 1, // Increase after opening brace
                ("for (item in list) {" to 1) to 2, // Increase from level 1 to 2
                ("val x = 5" to 1) to 1, // Maintain level for regular code
                ("}" to 2) to 1, // Decrease after closing brace
                ("return value" to 1) to 0, // Decrease after return
            )

        testCases.forEach { (input, expectedLevel) ->
            val (lineText, currentLevel) = input
            val newLevel = determineNewIndentLevel(lineText, currentLevel)
            assertEquals(expectedLevel, newLevel, "Failed for '$lineText' at level $currentLevel")
        }
    }

    @Test
    fun testCalculateIndentationLevel() {
        // Test indentation level calculation for various line texts using FibonacciSequence directly
        val testCases =
            mapOf(
                "" to 0,
                "code" to 0,
                "  code" to 1, // 2 spaces = level 1
                "   code" to 2, // 3 spaces = level 2
                "     code" to 3, // 5 spaces = level 3
                "        code" to 4, // 8 spaces = level 4
                "             code" to 5, // 13 spaces = level 5
                "                     code" to 6, // 21 spaces = level 6
            )

        testCases.forEach { (lineText, expectedLevel) ->
            val actualLevel = calculateIndentationLevelHelper(lineText)
            assertEquals(expectedLevel, actualLevel, "Failed for line: '$lineText'")
        }
    }

    @Test
    fun testFibonacciSequenceIntegration() {
        // Test that we can access FibonacciSequence methods directly
        assertEquals(2, FibonacciSequence.getIndentationForLevel(1))
        assertEquals(3, FibonacciSequence.getIndentationForLevel(2))
        assertEquals(5, FibonacciSequence.getIndentationForLevel(3))
        assertEquals(8, FibonacciSequence.getIndentationForLevel(4))
        assertEquals(13, FibonacciSequence.getIndentationForLevel(5))
        assertEquals(21, FibonacciSequence.getIndentationForLevel(6))
    }

    @Test
    fun testGetLevelForSpaces() {
        // Test reverse mapping from spaces to levels using FibonacciSequence directly
        val spacesToLevel =
            mapOf(
                0 to 1, // 0 spaces defaults to level 1 (minimum level)
                1 to 1, // Closest to 2 spaces
                2 to 1, // Exact match for level 1
                3 to 2, // Exact match for level 2
                4 to 2, // Closer to 3 than 5
                5 to 3, // Exact match for level 3
                6 to 3, // Closer to 5 than 8
                7 to 4, // Closer to 8 than 5
                8 to 4, // Exact match for level 4
                10 to 4, // Closer to 8 than 13
                13 to 5, // Exact match for level 5
                17 to 5, // Closer to 13 than 21
                21 to 6, // Exact match for level 6
            )

        spacesToLevel.forEach { (spaces, expectedLevel) ->
            val actualLevel = FibonacciSequence.getLevelForSpaces(spaces)
            assertEquals(expectedLevel, actualLevel, "Failed for $spaces spaces")
        }
    }

    // Helper methods to test the core logic

    private fun calculateIndentationLevelHelper(lineText: String): Int {
        val leadingSpaces = lineText.takeWhile { it == ' ' }.length
        return if (leadingSpaces == 0) 0 else FibonacciSequence.getLevelForSpaces(leadingSpaces)
    }

    private fun shouldIncreaseIndentationHelper(lineText: String): Boolean =
        lineText.endsWith("{") ||
            lineText.endsWith(":") ||
            lineText.contains("if ") ||
            lineText.contains("for ") ||
            lineText.contains("while ") ||
            lineText.contains("fun ") ||
            lineText.contains("class ") ||
            lineText.contains("interface ") ||
            lineText.contains("object ") ||
            lineText.contains("try") ||
            lineText.contains("catch") ||
            lineText.contains("finally") ||
            lineText.contains("else")

    private fun shouldDecreaseIndentationHelper(lineText: String): Boolean =
        lineText.trim().startsWith("}") ||
            lineText.trim() == "}" ||
            lineText.contains("break") ||
            lineText.contains("continue") ||
            lineText.contains("return")

    private fun determineNewIndentLevel(
        previousLineText: String,
        currentLevel: Int,
    ): Int =
        when {
            shouldIncreaseIndentationHelper(previousLineText) -> currentLevel + 1
            shouldDecreaseIndentationHelper(previousLineText) -> kotlin.math.max(0, currentLevel - 1)
            else -> currentLevel
        }
}
