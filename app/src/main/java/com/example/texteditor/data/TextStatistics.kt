package com.example.texteditor.data

data class TextStatistics(
    val characters: Int = 0,
    val words: Int = 0,
    val lines: Int = 0,
    val paragraphs: Int = 0
) {
    companion object {
        fun calculate(text: String): TextStatistics {
            val lines = text.lines()
            val words = text.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
            val characters = text.length
            val paragraphs = text.split(Regex("\\n\\s*\\n")).filter { it.trim().isNotEmpty() }.size
            
            return TextStatistics(
                characters = characters,
                words = words,
                lines = lines.size,
                paragraphs = paragraphs
            )
        }
    }
} 