package me.forketyfork.fibonacciindent

import com.intellij.openapi.diagnostic.Logger

/**
 * Utility class for generating Fibonacci sequence numbers for indentation levels.
 *
 * This class generates Fibonacci numbers starting from 2, 3, 5, 8, 13, 21, ...
 * (excluding the initial 0 and 1 values from the traditional sequence).
 *
 * Features:
 * - Caching mechanism for performance optimization
 * - Edge case handling for maximum levels and fallback indentation
 * - Thread-safe implementation
 */
object FibonacciSequence {
    /**
     * Maximum supported indentation level to prevent excessive memory usage
     * and unreasonable indentation depths.
     */
    const val MAX_INDENTATION_LEVEL = 20

    /**
     * Fallback indentation value used when the level exceeds maximum or in error cases.
     */
    const val FALLBACK_INDENTATION = 4

    /**
     * Cache for storing computed Fibonacci values to avoid recalculation.
     * Uses a thread-safe map for concurrent access.
     */
    private val cache =
        mutableMapOf<Int, Int>().apply {
            // Pre-populate the cache with initial values
            put(1, 2) // Level 1 = 2 spaces
            put(2, 3) // Level 2 = 3 spaces
        }

    private val logger = Logger.getInstance(FibonacciSequence::class.java)

    /**
     * Gets the Fibonacci indentation value for the specified level.
     *
     * @param level The indentation level (1-based indexing)
     * @return The number of spaces for the given indentation level
     * @throws IllegalArgumentException if the level is lower than 1
     */
    @Synchronized
    fun getIndentationForLevel(level: Int): Int {
        logger.debug("getIndentationForLevel($level) called")
        require(level >= 1) { "Indentation level must be at least 1, got: $level" }

        // Handle edge case: level exceeds maximum
        if (level > MAX_INDENTATION_LEVEL) {
            val fallbackResult = FALLBACK_INDENTATION * level
            logger.debug("Level $level exceeds maximum ($MAX_INDENTATION_LEVEL), using fallback: $fallbackResult")
            return fallbackResult
        }

        // Return cached value if available
        cache[level]?.let {
            logger.debug("Cache hit for level $level: $it spaces")
            return it
        }

        logger.debug("Cache miss for level $level, calculating...")
        logger.debug("Current cache size: ${cache.size}, cache keys: ${cache.keys.sorted()}")

        // Calculate and cache missing values up to the requested level
        var prev1 = cache[1]!! // 2
        var prev2 = cache[2]!! // 3

        for (i in 3..level) {
            if (cache.containsKey(i)) {
                prev1 = cache[i - 1]!!
                prev2 = cache[i]!!
                logger.debug("Found cached value for level $i: ${cache[i]}")
                continue
            }

            val current = prev1 + prev2
            cache[i] = current
            logger.debug("Calculated and cached level $i: $current spaces ($prev1 + $prev2)")
            prev1 = prev2
            prev2 = current
        }

        val result = cache[level]!!
        logger.debug("getIndentationForLevel($level) = $result")
        return result
    }

    /**
     * Gets multiple indentation values for levels 1 through maxLevel.
     *
     * @param maxLevel The maximum level to calculate (inclusive)
     * @return A list of indentation values indexed by level (0-based list, but represents 1-based levels)
     * @throws IllegalArgumentException if maxLevel is less than 1
     */
    @Synchronized
    fun getIndentationSequence(maxLevel: Int): List<Int> {
        logger.debug("getIndentationSequence($maxLevel) called")
        require(maxLevel >= 1) { "Maximum level must be at least 1, got: $maxLevel" }

        val result = mutableListOf<Int>()
        for (level in 1..maxLevel) {
            val indentation = getIndentationForLevel(level)
            result.add(indentation)
            logger.debug("Added level $level: $indentation spaces to sequence")
        }

        logger.debug("getIndentationSequence($maxLevel) = $result")
        return result
    }

    /**
     * Determines the appropriate indentation level based on the number of spaces.
     * This is useful for reverse-engineering the indentation level from existing code.
     *
     * @param spaces The number of spaces in the indentation
     * @return The closest matching indentation level, or 1 if no close match is found
     */
    @Synchronized
    fun getLevelForSpaces(spaces: Int): Int {
        logger.debug("getLevelForSpaces($spaces) called")

        if (spaces <= 0) {
            logger.debug("Spaces <= 0, returning level 1")
            return 1
        }

        // Find the closest Fibonacci indentation level
        var closestLevel = 1
        var closestDifference = kotlin.math.abs(spaces - getIndentationForLevel(1))
        logger.debug(
            "Initial comparison: level 1 has ${getIndentationForLevel(1)} spaces, difference = $closestDifference",
        )

        for (level in 2..MAX_INDENTATION_LEVEL) {
            val indentation = getIndentationForLevel(level)
            val difference = kotlin.math.abs(spaces - indentation)
            logger.debug("Checking level $level: $indentation spaces, difference = $difference")

            if (difference < closestDifference) {
                closestLevel = level
                closestDifference = difference
                logger.debug("New closest match: level $level with difference $difference")
            }

            // If we found an exact match, return immediately
            if (difference == 0) {
                logger.debug("Exact match found at level $level")
                return level
            }

            // If indentation exceeds spaces significantly, we can stop searching
            if (indentation > spaces + closestDifference) {
                logger.debug(
                    "Indentation $indentation exceeds spaces + closest difference " +
                        "(${spaces + closestDifference}), stopping search",
                )
                break
            }
        }

        logger.debug("getLevelForSpaces($spaces) = $closestLevel (closest difference: $closestDifference)")
        return closestLevel
    }

    /**
     * Clears the internal cache. Useful for testing or memory management.
     * Note: This will reset the cache to initial values (levels 1 and 2).
     */
    @Synchronized
    fun clearCache() {
        logger.debug("clearCache() called, current cache size: ${cache.size}, keys: ${cache.keys.sorted()}")
        cache.clear()
        cache[1] = 2
        cache[2] = 3
        logger.debug("Cache cleared and reset to initial values: {1=2, 2=3}")
    }

    /**
     * Gets the current cache size for monitoring purposes.
     *
     * @return The number of cached indentation values
     */
    @Synchronized
    fun getCacheSize(): Int {
        val size = cache.size
        logger.debug("getCacheSize() = $size, keys: ${cache.keys.sorted()}")
        return size
    }

    /**
     * Validates that the Fibonacci sequence is correctly implemented.
     * This is primarily used for internal testing and debugging.
     *
     * @param maxLevel The maximum level to validate
     * @return true if the sequence is valid, false otherwise
     */
    @Synchronized
    fun validateSequence(maxLevel: Int = 10): Boolean {
        logger.debug("validateSequence($maxLevel) called")

        try {
            val sequence = getIndentationSequence(maxLevel)
            logger.debug("Generated sequence for validation: $sequence")

            // Check that we have the expected number of elements
            if (sequence.size != maxLevel) {
                logger.debug("Validation failed: expected $maxLevel elements, got ${sequence.size}")
                return false
            }

            // Check initial values based on sequence length
            if (sequence.isNotEmpty() && sequence[0] != 2) {
                logger.debug("Validation failed: first element should be 2, got ${sequence[0]}")
                return false
            }
            if (sequence.size > 1 && sequence[1] != 3) {
                logger.debug("Validation failed: second element should be 3, got ${sequence[1]}")
                return false
            }

            // Check the Fibonacci property for further values (only if we have at least 3 elements)
            for (i in 2 until sequence.size) {
                val expected = sequence[i - 1] + sequence[i - 2]
                if (sequence[i] != expected) {
                    logger.debug(
                        "Validation failed at index $i: expected $expected " +
                            "(${sequence[i - 1]} + ${sequence[i - 2]}), got ${sequence[i]}",
                    )
                    return false
                }
            }

            logger.debug("validateSequence($maxLevel) = true (sequence is valid)")
            return true
        } catch (e: Exception) {
            logger.debug("validateSequence($maxLevel) = false (exception occurred: ${e.message})")
            return false
        }
    }
}
