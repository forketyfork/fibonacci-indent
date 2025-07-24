package me.forketyfork.fibonacciindent

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Service interface for managing Fibonacci-based indentation functionality.
 *
 * This service provides the core API for:
 * - Managing plugin state (enabled/disabled)
 * - Configuring indentation parameters
 * - Calculating Fibonacci-based indentation levels
 * - Persisting user preferences
 */
interface FibonacciIndentationService {
    /**
     * Checks if the Fibonacci indentation is currently enabled.
     *
     * @return true if the plugin is active, false otherwise
     */
    fun isEnabled(): Boolean

    /**
     * Enables or disables the Fibonacci indentation functionality.
     *
     * @param enabled true to enable, false to disable
     */
    fun setEnabled(enabled: Boolean)

    /**
     * Gets the maximum indentation level supported by the service.
     *
     * @return the maximum indentation level
     */
    fun getMaxIndentationLevel(): Int

    /**
     * Sets the maximum indentation level.
     *
     * @param maxLevel the maximum level to set (must be between 1 and 20)
     * @throws IllegalArgumentException if maxLevel is out of valid range
     */
    fun setMaxIndentationLevel(maxLevel: Int)

    /**
     * Gets the fallback indentation value used when Fibonacci calculation fails
     * or exceeds maximum level.
     *
     * @return the fallback indentation in spaces
     */
    fun getFallbackIndentation(): Int

    /**
     * Sets the fallback indentation value.
     *
     * @param fallback the fallback indentation in spaces (must be positive)
     * @throws IllegalArgumentException if fallback is not positive
     */
    fun setFallbackIndentation(fallback: Int)

    /**
     * Calculates the Fibonacci indentation for the given level.
     *
     * @param level the indentation level (1-based)
     * @return the number of spaces for the given level
     */
    fun calculateIndentation(level: Int): Int

    /**
     * Determines the indentation level based on the number of spaces.
     *
     * @param spaces the number of spaces
     * @return the closest matching indentation level
     */
    fun getLevelForSpaces(spaces: Int): Int

    /**
     * Resets all settings to their default values.
     */
    fun resetToDefaults()

    companion object {
        /**
         * Gets the application-level instance of the service.
         *
         * @return the FibonacciIndentationService instance
         */
        fun getInstance(): FibonacciIndentationService =
            ApplicationManager.getApplication().getService(FibonacciIndentationService::class.java)
    }
}

/**
 * State class for persisting service configuration.
 */
data class FibonacciIndentationState(
    var enabled: Boolean = true,
    var maxIndentationLevel: Int = FibonacciSequence.MAX_INDENTATION_LEVEL,
    var fallbackIndentation: Int = FibonacciSequence.FALLBACK_INDENTATION,
)

/**
 * Implementation of the FibonacciIndentationService with state persistence.
 *
 * This service manages the plugin's configuration and provides access to
 * Fibonacci indentation calculations. The state is automatically persisted
 * across IDE sessions.
 */
@Service(Service.Level.APP)
@State(
    name = "FibonacciIndentationService",
    storages = [Storage("fibonacci-indent.xml")],
)
class FibonacciIndentationServiceImpl :
    FibonacciIndentationService,
    PersistentStateComponent<FibonacciIndentationState> {
    private var state = FibonacciIndentationState()

    override fun getState(): FibonacciIndentationState = state

    override fun loadState(state: FibonacciIndentationState) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    override fun isEnabled(): Boolean = state.enabled

    override fun setEnabled(enabled: Boolean) {
        state.enabled = enabled
    }

    override fun getMaxIndentationLevel(): Int = state.maxIndentationLevel

    override fun setMaxIndentationLevel(maxLevel: Int) {
        require(maxLevel in 1..FibonacciSequence.MAX_INDENTATION_LEVEL) {
            "Maximum indentation level must be between 1 and ${FibonacciSequence.MAX_INDENTATION_LEVEL}, got: $maxLevel"
        }
        state.maxIndentationLevel = maxLevel
    }

    override fun getFallbackIndentation(): Int = state.fallbackIndentation

    override fun setFallbackIndentation(fallback: Int) {
        require(fallback > 0) {
            "Fallback indentation must be positive, got: $fallback"
        }
        state.fallbackIndentation = fallback
    }

    override fun calculateIndentation(level: Int): Int =
        if (level <= state.maxIndentationLevel) {
            FibonacciSequence.getIndentationForLevel(level)
        } else {
            state.fallbackIndentation * level
        }

    override fun getLevelForSpaces(spaces: Int): Int = FibonacciSequence.getLevelForSpaces(spaces)

    override fun resetToDefaults() {
        state = FibonacciIndentationState()
    }
}
