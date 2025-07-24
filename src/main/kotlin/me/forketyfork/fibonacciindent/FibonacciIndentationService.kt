package me.forketyfork.fibonacciindent

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
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
    private val logger = Logger.getInstance(FibonacciIndentationServiceImpl::class.java)

    override fun getState(): FibonacciIndentationState {
        logger.debug(
            "getState called, returning: enabled=${state.enabled}, " +
                "maxLevel=${state.maxIndentationLevel}, fallback=${state.fallbackIndentation}",
        )
        return state
    }

    override fun loadState(state: FibonacciIndentationState) {
        logger.debug(
            "loadState called with: enabled=${state.enabled}, " +
                "maxLevel=${state.maxIndentationLevel}, fallback=${state.fallbackIndentation}",
        )
        XmlSerializerUtil.copyBean(state, this.state)
        logger.debug("State loaded successfully")
    }

    override fun isEnabled(): Boolean {
        val enabled = state.enabled
        logger.debug("isEnabled() = $enabled")
        return enabled
    }

    override fun setEnabled(enabled: Boolean) {
        logger.debug("setEnabled($enabled), previous value: ${state.enabled}")
        state.enabled = enabled
    }

    override fun getMaxIndentationLevel(): Int {
        val maxLevel = state.maxIndentationLevel
        logger.debug("getMaxIndentationLevel() = $maxLevel")
        return maxLevel
    }

    override fun setMaxIndentationLevel(maxLevel: Int) {
        logger.debug("setMaxIndentationLevel($maxLevel), previous value: ${state.maxIndentationLevel}")
        require(maxLevel in 1..FibonacciSequence.MAX_INDENTATION_LEVEL) {
            "Maximum indentation level must be between 1 and ${FibonacciSequence.MAX_INDENTATION_LEVEL}, got: $maxLevel"
        }
        state.maxIndentationLevel = maxLevel
        logger.debug("Max indentation level set to $maxLevel")
    }

    override fun getFallbackIndentation(): Int {
        val fallback = state.fallbackIndentation
        logger.debug("getFallbackIndentation() = $fallback")
        return fallback
    }

    override fun setFallbackIndentation(fallback: Int) {
        logger.debug("setFallbackIndentation($fallback), previous value: ${state.fallbackIndentation}")
        require(fallback > 0) {
            "Fallback indentation must be positive, got: $fallback"
        }
        state.fallbackIndentation = fallback
        logger.debug("Fallback indentation set to $fallback")
    }

    override fun calculateIndentation(level: Int): Int {
        logger.debug("calculateIndentation($level) called, maxLevel=${state.maxIndentationLevel}")
        val result =
            if (level <= state.maxIndentationLevel) {
                val fibonacciSpaces = FibonacciSequence.getIndentationForLevel(level)
                logger.debug("Using Fibonacci sequence: level $level -> $fibonacciSpaces spaces")
                fibonacciSpaces
            } else {
                val fallbackSpaces = state.fallbackIndentation * level
                logger.debug(
                    "Using fallback calculation: level $level -> $fallbackSpaces spaces " +
                        "(${state.fallbackIndentation} * $level)",
                )
                fallbackSpaces
            }
        logger.debug("calculateIndentation($level) = $result")
        return result
    }

    override fun getLevelForSpaces(spaces: Int): Int {
        logger.debug("getLevelForSpaces($spaces) called")
        val level = FibonacciSequence.getLevelForSpaces(spaces)
        logger.debug("getLevelForSpaces($spaces) = $level")
        return level
    }

    override fun resetToDefaults() {
        logger.debug(
            "resetToDefaults() called, current state: enabled=${state.enabled}, " +
                "maxLevel=${state.maxIndentationLevel}, fallback=${state.fallbackIndentation}",
        )
        state = FibonacciIndentationState()
        logger.debug(
            "State reset to defaults: enabled=${state.enabled}, " +
                "maxLevel=${state.maxIndentationLevel}, fallback=${state.fallbackIndentation}",
        )
    }
}
