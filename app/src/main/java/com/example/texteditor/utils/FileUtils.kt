package com.example.texteditor.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FileUtils {
    
    suspend fun readFile(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    suspend fun writeFile(context: Context, uri: Uri, content: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(content)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }
    
    fun getFileNameWithoutExtension(fileName: String): String {
        return fileName.substringBeforeLast('.', fileName)
    }
    
    fun getMimeType(fileExtension: String): String {
        return when (fileExtension.lowercase()) {
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
            "sql" -> "text/x-sql"
            "sh" -> "text/x-sh"
            "yml", "yaml" -> "text/x-yaml"
            else -> "text/plain"
        }
    }
    
    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    }
    
    fun createBackupFileName(originalName: String): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val nameWithoutExt = getFileNameWithoutExtension(originalName)
        val extension = getFileExtension(originalName)
        return "${nameWithoutExt}_backup_$timestamp.$extension"
    }
    
    fun isValidFileName(fileName: String): Boolean {
        val invalidChars = charArrayOf('<', '>', ':', '"', '|', '?', '*', '\\', '/')
        return fileName.none { it in invalidChars }
    }
    
    fun sanitizeFileName(fileName: String): String {
        val invalidChars = charArrayOf('<', '>', ':', '"', '|', '?', '*', '\\', '/')
        return fileName.map { if (it in invalidChars) '_' else it }.joinToString("")
    }
} 