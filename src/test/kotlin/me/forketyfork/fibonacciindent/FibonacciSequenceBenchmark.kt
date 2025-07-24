package me.forketyfork.fibonacciindent

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * Performance benchmarks for the FibonacciSequence utility class.
 *
 * These benchmarks measure:
 * - Single level calculation performance
 * - Sequence generation performance
 * - Cache effectiveness
 * - Memory usage characteristics
 */
class FibonacciSequenceBenchmark {
    @BeforeEach
    fun setUp() {
        // Clear the cache before each benchmark to ensure consistent starting conditions
        FibonacciSequence.clearCache()
    }

    @Test
    fun benchmarkSingleLevelCalculation() {
        println("\n=== Single Level Calculation Benchmark ===")

        val levels = listOf(1, 5, 10, 15, 20)

        for (level in levels) {
            FibonacciSequence.clearCache()

            val timeNanos =
                measureNanoTime {
                    FibonacciSequence.getIndentationForLevel(level)
                }

            println("Level $level: ${timeNanos}ns (${timeNanos / 1000.0}μs)")
        }
    }

    @Test
    fun benchmarkSequenceGeneration() {
        println("\n=== Sequence Generation Benchmark ===")

        val maxLevels = listOf(5, 10, 15, 20)

        for (maxLevel in maxLevels) {
            FibonacciSequence.clearCache()

            val timeNanos =
                measureNanoTime {
                    FibonacciSequence.getIndentationSequence(maxLevel)
                }

            println("Sequence up to level $maxLevel: ${timeNanos}ns (${timeNanos / 1000.0}μs)")
        }
    }

    @Test
    fun benchmarkCacheEffectiveness() {
        println("\n=== Cache Effectiveness Benchmark ===")

        val level = 15

        // First calculation (cold cache)
        FibonacciSequence.clearCache()
        val coldTime =
            measureNanoTime {
                FibonacciSequence.getIndentationForLevel(level)
            }

        // Second calculation (warm cache)
        val warmTime =
            measureNanoTime {
                FibonacciSequence.getIndentationForLevel(level)
            }

        // Third calculation (still warm cache)
        val warmTime2 =
            measureNanoTime {
                FibonacciSequence.getIndentationForLevel(level)
            }

        println("Cold cache (level $level): ${coldTime}ns (${coldTime / 1000.0}μs)")
        println("Warm cache (level $level): ${warmTime}ns (${warmTime / 1000.0}μs)")
        println("Warm cache 2 (level $level): ${warmTime2}ns (${warmTime2 / 1000.0}μs)")
        println("Cache speedup: ${coldTime.toDouble() / warmTime}x")
    }

    @Test
    fun benchmarkRepeatedAccess() {
        println("\n=== Repeated Access Benchmark ===")

        val level = 10
        val iterations = 1000

        // Warm up the cache
        FibonacciSequence.getIndentationForLevel(level)

        val totalTime =
            measureTimeMillis {
                repeat(iterations) {
                    FibonacciSequence.getIndentationForLevel(level)
                }
            }

        val avgTimeNanos = (totalTime * 1_000_000) / iterations
        println("$iterations repeated accesses to level $level:")
        println("Total time: ${totalTime}ms")
        println("Average time per access: ${avgTimeNanos}ns (${avgTimeNanos / 1000.0}μs)")
    }

    @Test
    fun benchmarkLevelForSpacesPerformance() {
        println("\n=== Level For Spaces Performance Benchmark ===")

        val spaceValues = listOf(2, 5, 13, 34, 89, 144, 233)

        for (spaces in spaceValues) {
            val timeNanos =
                measureNanoTime {
                    FibonacciSequence.getLevelForSpaces(spaces)
                }

            val level = FibonacciSequence.getLevelForSpaces(spaces)
            println("$spaces spaces -> level $level: ${timeNanos}ns (${timeNanos / 1000.0}μs)")
        }
    }

    @Test
    fun benchmarkSequenceValidation() {
        println("\n=== Sequence Validation Performance Benchmark ===")

        val maxLevels = listOf(5, 10, 15, 20)

        for (maxLevel in maxLevels) {
            val timeNanos =
                measureNanoTime {
                    FibonacciSequence.validateSequence(maxLevel)
                }

            println("Validation up to level $maxLevel: ${timeNanos}ns (${timeNanos / 1000.0}μs)")
        }
    }

    @Test
    fun benchmarkMemoryUsage() {
        println("\n=== Memory Usage Benchmark ===")

        val runtime = Runtime.getRuntime()

        // Measure initial memory
        System.gc()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        // Generate large sequence
        FibonacciSequence.clearCache()
        FibonacciSequence.getIndentationSequence(20)

        // Measure memory after sequence generation
        System.gc()
        val afterSequenceMemory = runtime.totalMemory() - runtime.freeMemory()

        val memoryUsed = afterSequenceMemory - initialMemory
        val cacheSize = FibonacciSequence.getCacheSize()

        println("Initial memory: ${initialMemory / 1024}KB")
        println("Memory after sequence generation: ${afterSequenceMemory / 1024}KB")
        println("Memory used by sequence: ${memoryUsed / 1024}KB")
        println("Cache size: $cacheSize entries")
        println("Memory per cache entry: ${if (cacheSize > 0) memoryUsed / cacheSize else 0} bytes")
    }

    @Test
    fun benchmarkConcurrentAccess() {
        println("\n=== Concurrent Access Benchmark ===")

        val level = 15
        val threadCount = 4
        val iterationsPerThread = 250

        // Warm up
        FibonacciSequence.getIndentationForLevel(level)

        val totalTime =
            measureTimeMillis {
                val threads =
                    (1..threadCount).map { threadId ->
                        Thread {
                            repeat(iterationsPerThread) {
                                FibonacciSequence.getIndentationForLevel(level)
                            }
                        }
                    }

                threads.forEach { it.start() }
                threads.forEach { it.join() }
            }

        val totalOperations = threadCount * iterationsPerThread
        val avgTimeNanos = (totalTime * 1_000_000) / totalOperations

        println("$threadCount threads, $iterationsPerThread iterations each:")
        println("Total operations: $totalOperations")
        println("Total time: ${totalTime}ms")
        println("Average time per operation: ${avgTimeNanos}ns (${avgTimeNanos / 1000.0}μs)")
        val opsPerSecond = if (totalTime > 0) totalOperations * 1000 / totalTime else "N/A (too fast to measure)"
        println("Operations per second: $opsPerSecond")
    }

    @Test
    fun benchmarkEdgeCasePerformance() {
        println("\n=== Edge Case Performance Benchmark ===")

        // Test maximum level
        val maxLevel = FibonacciSequence.MAX_INDENTATION_LEVEL
        val maxLevelTime =
            measureNanoTime {
                FibonacciSequence.getIndentationForLevel(maxLevel)
            }

        // Test exceeding maximum level (fallback calculation)
        val exceedingLevel = maxLevel + 5
        val fallbackTime =
            measureNanoTime {
                FibonacciSequence.getIndentationForLevel(exceedingLevel)
            }

        // Test very large exceeding level
        val veryLargeLevel = maxLevel + 100
        val veryLargeFallbackTime =
            measureNanoTime {
                FibonacciSequence.getIndentationForLevel(veryLargeLevel)
            }

        println("Max level ($maxLevel): ${maxLevelTime}ns (${maxLevelTime / 1000.0}μs)")
        println("Exceeding level ($exceedingLevel): ${fallbackTime}ns (${fallbackTime / 1000.0}μs)")
        println("Very large level ($veryLargeLevel): ${veryLargeFallbackTime}ns (${veryLargeFallbackTime / 1000.0}μs)")
    }

    @Test
    fun performanceReport() {
        println("\n" + "=".repeat(60))
        println("FIBONACCI SEQUENCE PERFORMANCE REPORT")
        println("=".repeat(60))

        benchmarkSingleLevelCalculation()
        benchmarkSequenceGeneration()
        benchmarkCacheEffectiveness()
        benchmarkRepeatedAccess()
        benchmarkLevelForSpacesPerformance()
        benchmarkSequenceValidation()
        benchmarkMemoryUsage()
        benchmarkConcurrentAccess()
        benchmarkEdgeCasePerformance()

        println("\n" + "=".repeat(60))
        println("PERFORMANCE REPORT COMPLETE")
        println("=".repeat(60))
    }
}
