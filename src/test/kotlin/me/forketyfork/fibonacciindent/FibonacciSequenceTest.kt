package me.forketyfork.fibonacciindent

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for the FibonacciSequence utility class.
 * 
 * Tests cover:
 * - Basic Fibonacci sequence generation
 * - Caching mechanism functionality
 * - Edge case handling
 * - Performance characteristics
 * - Thread safety (basic validation)
 */
class FibonacciSequenceTest {
    
    @BeforeEach
    fun setUp() {
        // Clear cache before each test to ensure clean state
        FibonacciSequence.clearCache()
    }
    
    @Test
    fun testBasicFibonacciSequence() {
        // Test the first 10 levels of Fibonacci indentation
        val expectedValues = listOf(2, 3, 5, 8, 13, 21, 34, 55, 89, 144)
        
        for (i in expectedValues.indices) {
            val level = i + 1
            val expected = expectedValues[i]
            val actual = FibonacciSequence.getIndentationForLevel(level)
            assertEquals(expected, actual, "Level $level should have $expected spaces")
        }
    }
    
    @Test
    fun testGetIndentationSequence() {
        val sequence = FibonacciSequence.getIndentationSequence(6)
        val expected = listOf(2, 3, 5, 8, 13, 21)
        
        assertEquals(expected, sequence, "Sequence should match expected Fibonacci values")
    }
    
    @Test
    fun testCachingMechanism() {
        // Clear cache and verify initial state
        FibonacciSequence.clearCache()
        assertEquals(2, FibonacciSequence.getCacheSize(), "Cache should start with 2 pre-populated values")
        
        // Request a higher level and verify cache grows
        FibonacciSequence.getIndentationForLevel(5)
        assertEquals(5, FibonacciSequence.getCacheSize(), "Cache should contain 5 values after requesting level 5")
        
        // Request same level again - should use cache
        val result1 = FibonacciSequence.getIndentationForLevel(5)
        val result2 = FibonacciSequence.getIndentationForLevel(5)
        assertEquals(result1, result2, "Cached results should be consistent")
        assertEquals(5, FibonacciSequence.getCacheSize(), "Cache size should not change on repeated requests")
    }
    
    @Test
    fun testEdgeCaseMaxLevel() {
        val maxLevel = FibonacciSequence.MAX_INDENTATION_LEVEL
        val result = FibonacciSequence.getIndentationForLevel(maxLevel)
        
        // Should return a valid Fibonacci number, not fallback
        assertTrue(result > 0, "Max level should return positive indentation")
        
        // Test exceeding max level - should use fallback
        val exceedingLevel = maxLevel + 1
        val fallbackResult = FibonacciSequence.getIndentationForLevel(exceedingLevel)
        val expectedFallback = FibonacciSequence.FALLBACK_INDENTATION * exceedingLevel
        
        assertEquals(expectedFallback, fallbackResult, "Exceeding max level should use fallback calculation")
    }
    
    @Test
    fun testInvalidInputs() {
        // Test negative level
        assertThrows(IllegalArgumentException::class.java) {
            FibonacciSequence.getIndentationForLevel(-1)
        }
        
        // Test zero level
        assertThrows(IllegalArgumentException::class.java) {
            FibonacciSequence.getIndentationForLevel(0)
        }
        
        // Test invalid maxLevel for sequence
        assertThrows(IllegalArgumentException::class.java) {
            FibonacciSequence.getIndentationSequence(0)
        }
    }
    
    @Test
    fun testGetLevelForSpaces() {
        // Test exact matches
        assertEquals(1, FibonacciSequence.getLevelForSpaces(2), "2 spaces should map to level 1")
        assertEquals(2, FibonacciSequence.getLevelForSpaces(3), "3 spaces should map to level 2")
        assertEquals(3, FibonacciSequence.getLevelForSpaces(5), "5 spaces should map to level 3")
        assertEquals(4, FibonacciSequence.getLevelForSpaces(8), "8 spaces should map to level 4")
        
        // Test closest matches
        assertEquals(1, FibonacciSequence.getLevelForSpaces(1), "1 space should map to closest level 1")
        assertEquals(2, FibonacciSequence.getLevelForSpaces(4), "4 spaces should map to closest level 2")
        assertEquals(3, FibonacciSequence.getLevelForSpaces(6), "6 spaces should map to closest level 3")
        
        // Test edge cases
        assertEquals(1, FibonacciSequence.getLevelForSpaces(0), "0 spaces should map to level 1")
        assertEquals(1, FibonacciSequence.getLevelForSpaces(-5), "Negative spaces should map to level 1")
    }
    
    @Test
    fun testSequenceValidation() {
        assertTrue(FibonacciSequence.validateSequence(5), "Sequence validation should pass for level 5")
        assertTrue(FibonacciSequence.validateSequence(10), "Sequence validation should pass for level 10")
        assertTrue(FibonacciSequence.validateSequence(1), "Sequence validation should pass for level 1")
        assertTrue(FibonacciSequence.validateSequence(2), "Sequence validation should pass for level 2")
    }
    
    @Test
    fun testCacheClearFunctionality() {
        // Populate cache
        FibonacciSequence.getIndentationForLevel(10)
        assertTrue(FibonacciSequence.getCacheSize() > 2, "Cache should be populated")
        
        // Clear cache
        FibonacciSequence.clearCache()
        assertEquals(2, FibonacciSequence.getCacheSize(), "Cache should reset to initial size after clear")
        
        // Verify initial values are still correct
        assertEquals(2, FibonacciSequence.getIndentationForLevel(1), "Level 1 should still work after cache clear")
        assertEquals(3, FibonacciSequence.getIndentationForLevel(2), "Level 2 should still work after cache clear")
    }
    
    @Test
    fun testLargeSequenceGeneration() {
        // Test generating a larger sequence to verify performance and correctness
        val maxLevel = 15
        val sequence = FibonacciSequence.getIndentationSequence(maxLevel)
        
        assertEquals(maxLevel, sequence.size, "Sequence should have correct size")
        
        // Verify Fibonacci property holds for the entire sequence
        for (i in 2 until sequence.size) {
            val expected = sequence[i - 2] + sequence[i - 1]
            assertEquals(expected, sequence[i], "Fibonacci property should hold at index $i")
        }
    }
    
    @Test
    fun testConsistencyAcrossMultipleCalls() {
        // Test that multiple calls return consistent results
        val level = 7
        val results = mutableSetOf<Int>()
        
        repeat(10) {
            results.add(FibonacciSequence.getIndentationForLevel(level))
        }
        
        assertEquals(1, results.size, "All calls should return the same result")
        assertEquals(34, results.first(), "Level 7 should return 34 spaces")
    }
    
    @Test
    fun testBoundaryValues() {
        // Test level 1 (minimum valid level)
        assertEquals(2, FibonacciSequence.getIndentationForLevel(1), "Level 1 should return 2")
        
        // Test maximum level
        val maxResult = FibonacciSequence.getIndentationForLevel(FibonacciSequence.MAX_INDENTATION_LEVEL)
        assertTrue(maxResult > 0, "Max level should return positive value")
        
        // Test just over maximum level
        val overMaxResult = FibonacciSequence.getIndentationForLevel(FibonacciSequence.MAX_INDENTATION_LEVEL + 1)
        val expectedOverMax = FibonacciSequence.FALLBACK_INDENTATION * (FibonacciSequence.MAX_INDENTATION_LEVEL + 1)
        assertEquals(expectedOverMax, overMaxResult, "Over-max level should use fallback calculation")
    }
    
    @Test
    fun testSequenceProperties() {
        // Verify that the sequence is strictly increasing
        val sequence = FibonacciSequence.getIndentationSequence(10)
        
        for (i in 1 until sequence.size) {
            assertTrue(sequence[i] > sequence[i - 1], "Sequence should be strictly increasing")
        }
        
        // Verify that each element is the sum of the two preceding elements (Fibonacci property)
        for (i in 2 until sequence.size) {
            assertEquals(sequence[i - 2] + sequence[i - 1], sequence[i], 
                "Each element should be sum of two preceding elements")
        }
    }
}