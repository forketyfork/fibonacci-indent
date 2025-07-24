package me.forketyfork.fibonacciindent

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import kotlin.math.max

/**
 * Handler for integrating Fibonacci indentation with IntelliJ's editor system.
 *
 * This class hooks into IntelliJ's typing events to provide real-time
 * Fibonacci-based indentation when users press Enter or modify code.
 * It integrates with the auto-formatting system and supports undo/redo operations.
 */
class FibonacciIndentationHandler : TypedHandlerDelegate() {
    private val service: FibonacciIndentationService by lazy {
        FibonacciIndentationService.getInstance()
    }

    private val logger = Logger.getInstance(FibonacciIndentationHandler::class.java)

    /**
     * Called before a character is typed in the editor.
     * Used to prepare for indentation changes on Enter key press.
     */
    override fun beforeCharTyped(
        c: Char,
        project: Project,
        editor: Editor,
        file: PsiFile,
        fileType: FileType,
    ): Result {
        logger.debug(
            "beforeCharTyped called with character: '$c' (code: ${c.code}), " +
                "file: ${file.name}, fileType: ${fileType.name}",
        )

        // Only process if the service is enabled
        if (!service.isEnabled()) {
            logger.debug("Service is disabled, returning CONTINUE")
            return Result.CONTINUE
        }

        // Handle Enter key press for new line indentation
        if (c == '\n') {
            logger.debug("Enter key detected, handling Enter key press")
            return handleEnterKeyPress(project, editor, file)
        }

        logger.debug("Character '$c' not handled, returning CONTINUE")
        return Result.CONTINUE
    }

    /**
     * Called after a character is typed in the editor.
     * Used to apply Fibonacci indentation after typing.
     */
    override fun charTyped(
        c: Char,
        project: Project,
        editor: Editor,
        file: PsiFile,
    ): Result {
        logger.debug("charTyped called with character: '$c' (code: ${c.code}), file: ${file.name}")

        // Only process if the service is enabled
        if (!service.isEnabled()) {
            logger.debug("Service is disabled, returning CONTINUE")
            return Result.CONTINUE
        }

        // Apply indentation after Enter key
        if (c == '\n') {
            logger.debug("Enter key detected, applying Fibonacci indentation")
            return applyFibonacciIndentation(project, editor)
        }

        logger.debug("Character '$c' not handled, returning CONTINUE")
        return Result.CONTINUE
    }

    /**
     * Handles Enter key press to prepare for Fibonacci indentation.
     */
    private fun handleEnterKeyPress(
        project: Project,
        editor: Editor,
        file: PsiFile,
    ): Result {
        logger.debug("handleEnterKeyPress called for file: ${file.name}")

        val document = editor.document
        val caretModel = editor.caretModel
        val currentOffset = caretModel.offset
        logger.debug("Current caret offset: $currentOffset")

        // Get the current line
        val currentLine = document.getLineNumber(currentOffset)
        val currentLineStart = document.getLineStartOffset(currentLine)
        val currentLineEnd = document.getLineEndOffset(currentLine)
        val currentLineText = document.getText(TextRange(currentLineStart, currentLineEnd))
        logger.debug("Current line: $currentLine, text: '$currentLineText'")

        // Calculate current indentation level
        val currentIndentLevel = calculateIndentationLevel(currentLineText)
        logger.debug("Calculated current indentation level: $currentIndentLevel")

        // Store the indentation level for use after the character is typed
        editor.putUserData(PENDING_INDENT_LEVEL_KEY, currentIndentLevel)
        logger.debug("Stored pending indent level: $currentIndentLevel")

        return Result.CONTINUE
    }

    /**
     * Applies Fibonacci indentation after Enter key press.
     */
    private fun applyFibonacciIndentation(
        project: Project,
        editor: Editor,
    ): Result {
        logger.debug("applyFibonacciIndentation called")

        val document = editor.document
        val caretModel = editor.caretModel
        val currentOffset = caretModel.offset
        logger.debug("Current caret offset: $currentOffset")

        // Get the stored indentation level
        val pendingIndentLevel = editor.getUserData(PENDING_INDENT_LEVEL_KEY) ?: 0
        editor.putUserData(PENDING_INDENT_LEVEL_KEY, null)
        logger.debug("Retrieved pending indent level: $pendingIndentLevel")

        // Get the current line (which is the new line after Enter)
        val currentLine = document.getLineNumber(currentOffset)
        val currentLineStart = document.getLineStartOffset(currentLine)
        logger.debug("New line number: $currentLine, line start offset: $currentLineStart")

        // Determine the appropriate indentation level for the new line
        val newIndentLevel = determineNewLineIndentLevel(document, currentLine, pendingIndentLevel)
        logger.debug("Determined new indent level: $newIndentLevel")

        if (newIndentLevel > 0) {
            // Calculate Fibonacci indentation
            val indentSpaces = service.calculateIndentation(newIndentLevel)
            val indentString = " ".repeat(indentSpaces)
            logger.debug("Calculated indentation: $indentSpaces spaces for level $newIndentLevel")

            // Apply indentation using WriteCommandAction for undo/redo support
            WriteCommandAction.runWriteCommandAction(project) {
                document.insertString(currentLineStart, indentString)
                caretModel.moveToOffset(currentLineStart + indentSpaces)
                logger.debug("Applied indentation: inserted '$indentString' at offset $currentLineStart")
            }
        } else {
            logger.debug("No indentation applied (newIndentLevel = $newIndentLevel)")
        }

        return Result.CONTINUE
    }

    /**
     * Calculates the current indentation level based on line text.
     */
    private fun calculateIndentationLevel(lineText: String): Int {
        val leadingSpaces = lineText.takeWhile { it == ' ' }.length
        val level = if (leadingSpaces == 0) 0 else service.getLevelForSpaces(leadingSpaces)
        logger.debug("calculateIndentationLevel: '$lineText' -> $leadingSpaces spaces -> level $level")
        return level
    }

    /**
     * Determines the appropriate indentation level for a new line.
     */
    private fun determineNewLineIndentLevel(
        document: Document,
        currentLine: Int,
        previousLineIndentLevel: Int,
    ): Int {
        logger.debug(
            "determineNewLineIndentLevel: currentLine=$currentLine, previousLineIndentLevel=$previousLineIndentLevel",
        )

        if (currentLine == 0) {
            logger.debug("First line, returning indent level 0")
            return 0
        }

        val previousLine = currentLine - 1
        val previousLineStart = document.getLineStartOffset(previousLine)
        val previousLineEnd = document.getLineEndOffset(previousLine)
        val previousLineText = document.getText(TextRange(previousLineStart, previousLineEnd)).trim()
        logger.debug("Previous line text: '$previousLineText'")

        // Determine if we should increase, maintain, or decrease indentation
        val newLevel =
            when {
                // Increase indentation after opening braces, function definitions, etc.
                shouldIncreaseIndentation(previousLineText) -> {
                    logger.debug("Should increase indentation")
                    previousLineIndentLevel + 1
                }
                // Decrease indentation for closing braces
                shouldDecreaseIndentation(previousLineText) -> {
                    logger.debug("Should decrease indentation")
                    max(0, previousLineIndentLevel - 1)
                }
                // Maintain current indentation level
                else -> {
                    logger.debug("Should maintain indentation")
                    previousLineIndentLevel
                }
            }

        logger.debug("Determined new line indent level: $newLevel")
        return newLevel
    }

    /**
     * Determines if indentation should be increased based on the previous line.
     */
    private fun shouldIncreaseIndentation(lineText: String): Boolean {
        val shouldIncrease =
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

        logger.debug("shouldIncreaseIndentation('$lineText') = $shouldIncrease")
        return shouldIncrease
    }

    /**
     * Determines if indentation should be decreased based on the previous line.
     */
    private fun shouldDecreaseIndentation(lineText: String): Boolean {
        val shouldDecrease =
            lineText.trim().startsWith("}") ||
                lineText.trim() == "}" ||
                lineText.contains("break") ||
                lineText.contains("continue") ||
                lineText.contains("return")

        logger.debug("shouldDecreaseIndentation('$lineText') = $shouldDecrease")
        return shouldDecrease
    }

    /**
     * Applies Fibonacci indentation to the entire document.
     * This method can be called from actions or formatting operations.
     */
    fun applyToDocument(
        project: Project,
        editor: Editor,
    ) {
        if (!service.isEnabled()) return

        val document = editor.document
        val lineCount = document.lineCount

        WriteCommandAction.runWriteCommandAction(project, "Apply Fibonacci Indentation", null, {
            for (line in 0 until lineCount) {
                applyFibonacciIndentationToLine(document, line)
            }
        })
    }

    /**
     * Applies Fibonacci indentation to a specific line.
     */
    private fun applyFibonacciIndentationToLine(
        document: Document,
        lineNumber: Int,
    ) {
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStart, lineEnd))

        // Skip empty lines
        if (lineText.trim().isEmpty()) return

        // Calculate current and target indentation
        val currentSpaces = lineText.takeWhile { it == ' ' }.length
        val currentLevel = if (currentSpaces == 0) 0 else service.getLevelForSpaces(currentSpaces)

        if (currentLevel > 0) {
            val targetSpaces = service.calculateIndentation(currentLevel)

            if (currentSpaces != targetSpaces) {
                // Replace current indentation with Fibonacci indentation
                val contentStart = lineStart + currentSpaces
                val newIndent = " ".repeat(targetSpaces)

                document.replaceString(lineStart, contentStart, newIndent)
            }
        }
    }

    /**
     * Applies Fibonacci indentation to a selected text range.
     */
    fun applyToSelection(
        project: Project,
        editor: Editor,
    ) {
        if (!service.isEnabled()) return

        val selectionModel = editor.selectionModel
        if (!selectionModel.hasSelection()) {
            // If no selection, apply to current line
            applyToCurrentLine(project, editor)
            return
        }

        val document = editor.document
        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd
        val startLine = document.getLineNumber(startOffset)
        val endLine = document.getLineNumber(endOffset)

        WriteCommandAction.runWriteCommandAction(project, "Apply Fibonacci Indentation to Selection", null, {
            for (line in startLine..endLine) {
                applyFibonacciIndentationToLine(document, line)
            }
        })
    }

    /**
     * Applies Fibonacci indentation to the current line.
     */
    private fun applyToCurrentLine(
        project: Project,
        editor: Editor,
    ) {
        val document = editor.document
        val caretModel = editor.caretModel
        val currentLine = document.getLineNumber(caretModel.offset)

        WriteCommandAction.runWriteCommandAction(project, "Apply Fibonacci Indentation to Line", null, {
            applyFibonacciIndentationToLine(document, currentLine)
        })
    }
}

private val PENDING_INDENT_LEVEL_KEY =
    Key.create<Int>("fibonacci.pending.indent.level")
