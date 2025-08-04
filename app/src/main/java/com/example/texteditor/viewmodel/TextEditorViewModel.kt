package com.example.texteditor.viewmodel

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texteditor.data.SearchOptions
import com.example.texteditor.data.TextDocument
import com.example.texteditor.data.TextStatistics
import com.example.texteditor.utils.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.util.UUID

class TextEditorViewModel : ViewModel() {
    
    private val _currentDocument = MutableStateFlow(TextDocument())
    val currentDocument: StateFlow<TextDocument> = _currentDocument.asStateFlow()
    
    private val _textContent = MutableStateFlow("")
    val textContent: StateFlow<String> = _textContent.asStateFlow()
    
    private val _statistics = MutableStateFlow(TextStatistics())
    val statistics: StateFlow<TextStatistics> = _statistics.asStateFlow()
    
    private val _searchOptions = MutableStateFlow(SearchOptions())
    val searchOptions: StateFlow<SearchOptions> = _searchOptions.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Int>>(emptyList())
    val searchResults: StateFlow<List<Int>> = _searchResults.asStateFlow()
    
    private val _currentSearchIndex = MutableStateFlow(-1)
    val currentSearchIndex: StateFlow<Int> = _currentSearchIndex.asStateFlow()
    
    private val _isSearchVisible = MutableStateFlow(false)
    val isSearchVisible: StateFlow<Boolean> = _isSearchVisible.asStateFlow()
    
    private val _undoStack = MutableStateFlow<List<String>>(emptyList())
    private val _redoStack = MutableStateFlow<List<String>>(emptyList())
    
    private val _isModified = MutableStateFlow(false)
    val isModified: StateFlow<Boolean> = _isModified.asStateFlow()
    
    private val _autoSaveEnabled = MutableStateFlow(true)
    val autoSaveEnabled: StateFlow<Boolean> = _autoSaveEnabled.asStateFlow()
    
    private val _autoSavedDocument = MutableStateFlow<TextDocument?>(null)
    val autoSavedDocument: StateFlow<TextDocument?> = _autoSavedDocument.asStateFlow()
    
    private val _showStartupMenu = MutableStateFlow(true)
    val showStartupMenu: StateFlow<Boolean> = _showStartupMenu.asStateFlow()
    
    init {
        updateStatistics()
        startAutoSave()
        loadAutoSavedDocument()
    }
    
    private fun loadAutoSavedDocument() {
        // In a real app, you'd load from SharedPreferences or local storage
        // For now, we'll simulate an auto-saved document
        viewModelScope.launch {
            // Check if there's an auto-saved document
            // This would typically be loaded from SharedPreferences
            val autoSaved = TextDocument(
                id = "auto_saved",
                name = "Auto-saved Document",
                content = "This is an auto-saved document from your previous session.\n\nYou can continue editing or start fresh.",
                fileExtension = "txt",
                lastModified = LocalDateTime.now().minusHours(2),
                isModified = false
            )
            _autoSavedDocument.value = autoSaved
        }
    }
    
    private fun startAutoSave() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(30000) // Auto-save every 30 seconds
                if (_autoSaveEnabled.value && _isModified.value && _currentDocument.value.uri != null) {
                    // Note: We can't access context here, so we'll trigger auto-save from the UI
                    // The actual auto-save will be handled by the UI layer
                }
            }
        }
    }
    
    fun toggleAutoSave() {
        _autoSaveEnabled.update { !it }
    }
    
    fun performAutoSave(context: Context) {
        if (_autoSaveEnabled.value && _isModified.value && _currentDocument.value.uri != null) {
            saveFile(context)
        }
    }
    
    fun updateText(newText: String) {
        val oldText = _textContent.value
        if (oldText != newText) {
            // Add to undo stack
            _undoStack.update { it + oldText }
            _redoStack.value = emptyList()
            
            _textContent.value = newText
            _currentDocument.update { it.copy(content = newText, isModified = true) }
            _isModified.value = true
            updateStatistics()
        }
    }
    
    private fun updateStatistics() {
        _statistics.value = TextStatistics.calculate(_textContent.value)
    }
    
    fun newFile() {
        val newDoc = TextDocument(
            id = UUID.randomUUID().toString(),
            name = "Untitled",
            content = "",
            fileExtension = "txt",
            lastModified = LocalDateTime.now(),
            isModified = false
        )
        _currentDocument.value = newDoc
        _textContent.value = ""
        _isModified.value = false
        _undoStack.value = emptyList()
        _redoStack.value = emptyList()
        updateStatistics()
    }
    
    fun openFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val documentFile = DocumentFile.fromSingleUri(context, uri)
                val fileName = documentFile?.name ?: "Unknown"
                val extension = FileUtils.getFileExtension(fileName)
                val name = FileUtils.getFileNameWithoutExtension(fileName)
                
                val content = FileUtils.readFile(context, uri)
                
                val newDoc = TextDocument(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    content = content,
                    fileExtension = extension,
                    uri = uri,
                    lastModified = LocalDateTime.now(),
                    isModified = false
                )
                
                _currentDocument.value = newDoc
                _textContent.value = content
                _isModified.value = false
                _undoStack.value = emptyList()
                _redoStack.value = emptyList()
                updateStatistics()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun saveFile(context: Context) {
        viewModelScope.launch {
            try {
                val document = _currentDocument.value
                val uri = document.uri
                
                if (uri != null) {
                    val success = FileUtils.writeFile(context, uri, _textContent.value)
                    if (success) {
                        _currentDocument.update { 
                            it.copy(
                                lastModified = LocalDateTime.now(),
                                isModified = false
                            )
                        }
                        _isModified.value = false
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun saveFileAs(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val success = FileUtils.writeFile(context, uri, _textContent.value)
                if (success) {
                    val documentFile = DocumentFile.fromSingleUri(context, uri)
                    val fileName = documentFile?.name ?: "Unknown"
                    val extension = FileUtils.getFileExtension(fileName)
                    val name = FileUtils.getFileNameWithoutExtension(fileName)
                    
                    val newDoc = TextDocument(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        content = _textContent.value,
                        fileExtension = extension,
                        uri = uri,
                        lastModified = LocalDateTime.now(),
                        isModified = false
                    )
                    
                    _currentDocument.value = newDoc
                    _isModified.value = false
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun undo() {
        val undoStack = _undoStack.value
        if (undoStack.isNotEmpty()) {
            val previousText = undoStack.last()
            val currentText = _textContent.value
            
            _redoStack.update { it + currentText }
            _undoStack.update { it.dropLast(1) }
            
            _textContent.value = previousText
            _currentDocument.update { it.copy(content = previousText) }
            updateStatistics()
        }
    }
    
    fun redo() {
        val redoStack = _redoStack.value
        if (redoStack.isNotEmpty()) {
            val nextText = redoStack.last()
            val currentText = _textContent.value
            
            _undoStack.update { it + currentText }
            _redoStack.update { it.dropLast(1) }
            
            _textContent.value = nextText
            _currentDocument.update { it.copy(content = nextText) }
            updateStatistics()
        }
    }
    
    fun canUndo(): Boolean = _undoStack.value.isNotEmpty()
    fun canRedo(): Boolean = _redoStack.value.isNotEmpty()
    
    fun updateSearchOptions(options: SearchOptions) {
        _searchOptions.value = options
        performSearch()
    }
    
    fun performSearch() {
        val options = _searchOptions.value
        val text = _textContent.value
        val searchText = options.searchText
        
        if (searchText.isEmpty()) {
            _searchResults.value = emptyList()
            _currentSearchIndex.value = -1
            return
        }
        
        val results = mutableListOf<Int>()
        var index = 0
        
        while (index < text.length) {
            val foundIndex = if (options.isCaseSensitive) {
                text.indexOf(searchText, index)
            } else {
                text.lowercase().indexOf(searchText.lowercase(), index)
            }
            
            if (foundIndex == -1) break
            
            if (options.isWholeWord) {
                val before = if (foundIndex > 0) text[foundIndex - 1] else ' '
                val after = if (foundIndex + searchText.length < text.length) text[foundIndex + searchText.length] else ' '
                if (before.isLetterOrDigit() || after.isLetterOrDigit()) {
                    index = foundIndex + 1
                    continue
                }
            }
            
            results.add(foundIndex)
            index = foundIndex + searchText.length
        }
        
        _searchResults.value = results
        _currentSearchIndex.value = if (results.isNotEmpty()) 0 else -1
    }
    
    fun replaceCurrent() {
        val options = _searchOptions.value
        val results = _searchResults.value
        val currentIndex = _currentSearchIndex.value
        
        if (currentIndex >= 0 && currentIndex < results.size) {
            val text = _textContent.value
            val startIndex = results[currentIndex]
            val endIndex = startIndex + options.searchText.length
            
            val newText = text.substring(0, startIndex) + options.replaceText + text.substring(endIndex)
            updateText(newText)
            
            // Update search results after replacement
            performSearch()
        }
    }
    
    fun replaceAll() {
        val options = _searchOptions.value
        val text = _textContent.value
        val searchText = options.searchText
        val replaceText = options.replaceText
        
        if (searchText.isEmpty()) return
        
        val newText = if (options.isCaseSensitive) {
            text.replace(searchText, replaceText)
        } else {
            text.replace(searchText, replaceText, ignoreCase = true)
        }
        
        updateText(newText)
        performSearch()
    }
    
    fun nextSearchResult() {
        val results = _searchResults.value
        val currentIndex = _currentSearchIndex.value
        
        if (results.isNotEmpty()) {
            val nextIndex = if (currentIndex < results.size - 1) currentIndex + 1 else 0
            _currentSearchIndex.value = nextIndex
        }
    }
    
    fun previousSearchResult() {
        val results = _searchResults.value
        val currentIndex = _currentSearchIndex.value
        
        if (results.isNotEmpty()) {
            val prevIndex = if (currentIndex > 0) currentIndex - 1 else results.size - 1
            _currentSearchIndex.value = prevIndex
        }
    }
    
    fun toggleSearchVisibility() {
        _isSearchVisible.update { !it }
        if (!_isSearchVisible.value) {
            _searchResults.value = emptyList()
            _currentSearchIndex.value = -1
        }
    }
    
    fun getCurrentSearchPosition(): Int {
        val results = _searchResults.value
        val currentIndex = _currentSearchIndex.value
        return if (currentIndex >= 0 && currentIndex < results.size) {
            results[currentIndex]
        } else -1
    }
    
    fun continueWithAutoSave() {
        val autoSaved = _autoSavedDocument.value
        if (autoSaved != null) {
            _currentDocument.value = autoSaved
            _textContent.value = autoSaved.content
            _isModified.value = false
            _undoStack.value = emptyList()
            _redoStack.value = emptyList()
            updateStatistics()
            _showStartupMenu.value = false
        }
    }
    
    fun startNewFile() {
        newFile()
        _showStartupMenu.value = false
    }
    
    fun saveAutoSavedFile(context: Context) {
        val autoSaved = _autoSavedDocument.value
        if (autoSaved != null) {
            // This would typically open a save dialog
            // For now, we'll just continue with the auto-saved document
            continueWithAutoSave()
        }
    }
    
    fun showStartupMenu() {
        _showStartupMenu.value = true
    }
    
    // Clipboard operations
    fun copyText(): String {
        return _textContent.value
    }
    
    fun pasteText(text: String) {
        updateText(_textContent.value + text)
    }
    
    fun cutText(): String {
        val currentText = _textContent.value
        updateText("")
        return currentText
    }
} 