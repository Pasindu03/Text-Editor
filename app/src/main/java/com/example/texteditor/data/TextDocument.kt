package com.example.texteditor.data

import android.net.Uri
import java.time.LocalDateTime

data class TextDocument(
    val id: String = "",
    val name: String = "",
    val content: String = "",
    val fileExtension: String = "",
    val uri: Uri? = null,
    val lastModified: LocalDateTime = LocalDateTime.now(),
    val isModified: Boolean = false
) {
    val fullName: String
        get() = if (fileExtension.isNotEmpty()) "$name.$fileExtension" else name
    
    val mimeType: String
        get() = when (fileExtension.lowercase()) {
            "txt" -> "text/plain"
            "kt" -> "text/x-kotlin"
            "java" -> "text/x-java-source"
            "xml" -> "text/xml"
            "json" -> "application/json"
            "md" -> "text/markdown"
            "html" -> "text/html"
            "css" -> "text/css"
            "js" -> "text/javascript"
            "py" -> "text/x-python"
            "cpp" -> "text/x-c++src"
            "c" -> "text/x-csrc"
            "h" -> "text/x-chdr"
            "hpp" -> "text/x-c++hdr"
            else -> "text/plain"
        }
} 