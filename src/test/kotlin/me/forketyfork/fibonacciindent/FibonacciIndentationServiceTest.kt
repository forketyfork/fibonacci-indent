package me.forketyfork.fibonacciindent

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for FibonacciIndentationService implementation.
 *
 * Tests cover:
 * - Service state management (enabled/disabled)
 * - Configuration persistence and validation
 * - Indentation calculation functionality
 * - Edge cases and error handling
 */
class FibonacciIndentationServiceTest {
    private fun createService(): FibonacciIndentationServiceImpl = FibonacciIndentationServiceImpl()

    @Test
    fun `test default state initialization`() {
        val service = createService()

        assertTrue(service.isEnabled(), "Service should be enabled by default")
        assertEquals(
            FibonacciSequence.MAX_INDENTATION_LEVEL,
            service.getMaxIndentationLevel(),
            "Default max indentation level should match FibonacciSequence.MAX_INDENTATION_LEVEL",
        )
        assertEquals(
            FibonacciSequence.FALLBACK_INDENTATION,
            service.getFallbackIndentation(),
            "Default fallback indentation should match FibonacciSequence.FALLBACK_INDENTATION",
        )
    }

    @Test
    fun `test enable and disable functionality`() {
        val service = createService()

        // Test disabling
        service.setEnabled(false)
        assertFalse(service.isEnabled(), "Service should be disabled after setEnabled(false)")

        // Test re-enabling
        service.setEnabled(true)
        assertTrue(service.isEnabled(), "Service should be enabled after setEnabled(true)")
    }

    @Test
    fun `test max indentation level configuration`() {
        val service = createService()

        // Test valid values
        service.setMaxIndentationLevel(10)
        assertEquals(10, service.getMaxIndentationLevel(), "Max indentation level should be set to 10")

        service.setMaxIndentationLevel(1)
        assertEquals(1, service.getMaxIndentationLevel(), "Max indentation level should be set to 1")

        service.setMaxIndentationLevel(FibonacciSequence.MAX_INDENTATION_LEVEL)
        assertEquals(
            FibonacciSequence.MAX_INDENTATION_LEVEL,
            service.getMaxIndentationLevel(),
            "Max indentation level should be set to maximum allowed value",
        )
    }

    @Test
    fun `test max indentation level validation`() {
        val service = createService()

        // Test invalid values
        assertThrows(IllegalArgumentException::class.java) {
            service.setMaxIndentationLevel(0)
        }

        assertThrows(IllegalArgumentException::class.java) {
            service.setMaxIndentationLevel(-1)
        }

        assertThrows(IllegalArgumentException::class.java) {
            service.setMaxIndentationLevel(FibonacciSequence.MAX_INDENTATION_LEVEL + 1)
        }
    }

    @Test
    fun `test fallback indentation configuration`() {
        val service = createService()

        // Test valid values
        service.setFallbackIndentation(2)
        assertEquals(2, service.getFallbackIndentation(), "Fallback indentation should be set to 2")

        service.setFallbackIndentation(8)
        assertEquals(8, service.getFallbackIndentation(), "Fallback indentation should be set to 8")
    }

    @Test
    fun `test fallback indentation validation`() {
        val service = createService()

        // Test invalid values
        assertThrows(IllegalArgumentException::class.java) {
            service.setFallbackIndentation(0)
        }

        assertThrows(IllegalArgumentException::class.java) {
            service.setFallbackIndentation(-1)
        }
    }

    @Test
    fun `test indentation calculation within max level`() {
        val service = createService()
        service.setMaxIndentationLevel(5)

        // Test calculations within the limit
        assertEquals(2, service.calculateIndentation(1), "Level 1 should return 2 spaces")
        assertEquals(3, service.calculateIndentation(2), "Level 2 should return 3 spaces")
        assertEquals(5, service.calculateIndentation(3), "Level 3 should return 5 spaces")
        assertEquals(8, service.calculateIndentation(4), "Level 4 should return 8 spaces")
        assertEquals(13, service.calculateIndentation(5), "Level 5 should return 13 spaces")
    }

    @Test
    fun `test indentation calculation exceeding max level`() {
        val service = createService()
        service.setMaxIndentationLevel(3)
        service.setFallbackIndentation(4)

        // Test calculations exceeding the limit
        assertEquals(2, service.calculateIndentation(1), "Level 1 should return Fibonacci value")
        assertEquals(3, service.calculateIndentation(2), "Level 2 should return Fibonacci value")
        assertEquals(5, service.calculateIndentation(3), "Level 3 should return Fibonacci value")
        assertEquals(16, service.calculateIndentation(4), "Level 4 should return fallback * level (4 * 4)")
        assertEquals(20, service.calculateIndentation(5), "Level 5 should return fallback * level (4 * 5)")
    }

    @Test
    fun `test level calculation from spaces`() {
        val service = createService()

        // Test exact matches
        assertEquals(1, service.getLevelForSpaces(2), "2 spaces should map to level 1")
        assertEquals(2, service.getLevelForSpaces(3), "3 spaces should map to level 2")
        assertEquals(3, service.getLevelForSpaces(5), "5 spaces should map to level 3")
        assertEquals(4, service.getLevelForSpaces(8), "8 spaces should map to level 4")

        // Test approximate matches
        assertEquals(1, service.getLevelForSpaces(1), "1 space should map to closest level (1)")
        assertEquals(2, service.getLevelForSpaces(4), "4 spaces should map to closest level (2)")
        assertEquals(3, service.getLevelForSpaces(6), "6 spaces should map to closest level (3)")
    }

    @Test
    fun `test reset to defaults`() {
        val service = createService()

        // Modify all settings
        service.setEnabled(false)
        service.setMaxIndentationLevel(5)
        service.setFallbackIndentation(2)

        // Verify changes
        assertFalse(service.isEnabled(), "Service should be disabled")
        assertEquals(5, service.getMaxIndentationLevel(), "Max level should be 5")
        assertEquals(2, service.getFallbackIndentation(), "Fallback should be 2")

        // Reset to defaults
        service.resetToDefaults()

        // Verify defaults are restored
        assertTrue(service.isEnabled(), "Service should be enabled after reset")
        assertEquals(
            FibonacciSequence.MAX_INDENTATION_LEVEL,
            service.getMaxIndentationLevel(),
            "Max level should be reset to default",
        )
        assertEquals(
            FibonacciSequence.FALLBACK_INDENTATION,
            service.getFallbackIndentation(),
            "Fallback should be reset to default",
        )
    }

    @Test
    fun `test state persistence`() {
        val service = createService()

        // Modify state
        service.setEnabled(false)
        service.setMaxIndentationLevel(10)
        service.setFallbackIndentation(6)

        // Get state
        val state = service.getState()

        // Verify state contains correct values
        assertFalse(state.enabled, "State should reflect disabled status")
        assertEquals(10, state.maxIndentationLevel, "State should reflect max level")
        assertEquals(6, state.fallbackIndentation, "State should reflect fallback value")

        // Create new service and load state
        val newService = createService()
        newService.loadState(state)

        // Verify state was loaded correctly
        assertFalse(newService.isEnabled(), "New service should reflect loaded enabled state")
        assertEquals(10, newService.getMaxIndentationLevel(), "New service should reflect loaded max level")
        assertEquals(6, newService.getFallbackIndentation(), "New service should reflect loaded fallback")
    }

    @Test
    fun `test edge cases for indentation calculation`() {
        val service = createService()

        // Test with minimum level
        assertEquals(2, service.calculateIndentation(1), "Minimum level should work correctly")

        // Test with level at max boundary
        service.setMaxIndentationLevel(1)
        assertEquals(2, service.calculateIndentation(1), "Level at max boundary should use Fibonacci")
        assertEquals(8, service.calculateIndentation(2), "Level exceeding max should use fallback (4 * 2 = 8)")
    }
}
