package com.example.texteditor.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.texteditor.viewmodel.TextEditorViewModel
import com.example.texteditor.ui.components.AdvancedTextEditor
import com.example.texteditor.ui.components.EnhancedTextEditor
import com.example.texteditor.ui.components.EnhancedSearchBar
import com.example.texteditor.ui.components.StartupMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen(
    onSettingsClick: () -> Unit = {},
    viewModel: TextEditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentDocument by viewModel.currentDocument.collectAsState()
    val textContent by viewModel.textContent.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val isModified by viewModel.isModified.collectAsState()
    val searchOptions by viewModel.searchOptions.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val currentSearchIndex by viewModel.currentSearchIndex.collectAsState()
    val isSearchVisible by viewModel.isSearchVisible.collectAsState()
    val autoSaveEnabled by viewModel.autoSaveEnabled.collectAsState()
    val autoSavedDocument by viewModel.autoSavedDocument.collectAsState()
    val showStartupMenu by viewModel.showStartupMenu.collectAsState()
    
    val clipboardManager = LocalClipboardManager.current
    
    // Auto-save effect
    LaunchedEffect(autoSaveEnabled, isModified) {
        if (autoSaveEnabled && isModified) {
            kotlinx.coroutines.delay(30000) // 30 seconds delay
            viewModel.performAutoSave(context)
        }
    }
    
    // Show startup menu if needed
    if (showStartupMenu) {
        StartupMenu(
            autoSavedDocument = autoSavedDocument,
            onContinueWithAutoSave = { viewModel.continueWithAutoSave() },
            onStartNewFile = { viewModel.startNewFile() },
            onSaveAutoSavedFile = { viewModel.saveAutoSavedFile(context) }
        )
        return
    }
    
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.openFile(context, it) }
    }
    
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        uri?.let { viewModel.saveFileAs(context, it) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isModified) "${currentDocument.fullName} *" else currentDocument.fullName,
                        maxLines = 1
                    )
                },
                actions = {
                    // File operations
                    IconButton(onClick = { viewModel.newFile() }) {
                        Icon(Icons.Default.Add, contentDescription = "New File")
                    }
                    IconButton(onClick = { openFileLauncher.launch("*/*") }) {
                        Icon(Icons.Default.FolderOpen, contentDescription = "Open File")
                    }
                    IconButton(
                        onClick = { viewModel.saveFile(context) },
                        enabled = currentDocument.uri != null
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                    IconButton(onClick = { saveFileLauncher.launch("${currentDocument.name}.${currentDocument.fileExtension}") }) {
                        Icon(Icons.Default.SaveAs, contentDescription = "Save As")
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                    
                    // Edit operations
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = viewModel.canUndo()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                    }
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = viewModel.canRedo()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                    
                    // Search
                    IconButton(onClick = { viewModel.toggleSearchVisibility() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                    
                    // Settings
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            StatisticsBar(statistics = statistics)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isSearchVisible) {
                EnhancedSearchBar(
                    searchOptions = searchOptions,
                    searchResults = searchResults,
                    currentSearchIndex = currentSearchIndex,
                    onSearchOptionsChanged = { viewModel.updateSearchOptions(it) },
                    onNext = { viewModel.nextSearchResult() },
                    onPrevious = { viewModel.previousSearchResult() },
                    onReplace = { viewModel.replaceCurrent() },
                    onReplaceAll = { viewModel.replaceAll() },
                    onClose = { viewModel.toggleSearchVisibility() }
                )
            }
            
            EnhancedTextEditor(
                text = textContent,
                onTextChanged = { viewModel.updateText(it) },
                searchResults = searchResults,
                currentSearchIndex = currentSearchIndex,
                searchText = searchOptions.searchText,
                onCopy = { 
                    val textToCopy = viewModel.copyText()
                    clipboardManager.setText(AnnotatedString(textToCopy))
                },
                onPaste = { 
                    clipboardManager.getText()?.let { annotatedString ->
                        viewModel.pasteText(annotatedString.text)
                    }
                },
                onCut = { 
                    val textToCut = viewModel.cutText()
                    clipboardManager.setText(AnnotatedString(textToCut))
                },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                canUndo = viewModel.canUndo(),
                canRedo = viewModel.canRedo()
            )
        }
    }
}

@Composable
fun StatisticsBar(statistics: com.example.texteditor.data.TextStatistics) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Chars: ${statistics.characters}")
            Text("Words: ${statistics.words}")
            Text("Lines: ${statistics.lines}")
            Text("Paragraphs: ${statistics.paragraphs}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchOptions: com.example.texteditor.data.SearchOptions,
    searchResults: List<Int>,
    currentSearchIndex: Int,
    onSearchOptionsChanged: (com.example.texteditor.data.SearchOptions) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onReplace: () -> Unit,
    onReplaceAll: () -> Unit
) {
    var localSearchText by remember { mutableStateOf(searchOptions.searchText) }
    var localReplaceText by remember { mutableStateOf(searchOptions.replaceText) }
    var localCaseSensitive by remember { mutableStateOf(searchOptions.isCaseSensitive) }
    var localWholeWord by remember { mutableStateOf(searchOptions.isWholeWord) }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search input
                OutlinedTextField(
                    value = localSearchText,
                    onValueChange = { 
                        localSearchText = it
                        onSearchOptionsChanged(searchOptions.copy(searchText = it))
                    },
                    label = { Text("Search") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Navigation buttons
                IconButton(
                    onClick = onPrevious,
                    enabled = searchResults.isNotEmpty()
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous")
                }
                
                Text(
                    text = if (searchResults.isNotEmpty()) "${currentSearchIndex + 1}/${searchResults.size}" else "0/0",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(
                    onClick = onNext,
                    enabled = searchResults.isNotEmpty()
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Replace input
                OutlinedTextField(
                    value = localReplaceText,
                    onValueChange = { 
                        localReplaceText = it
                        onSearchOptionsChanged(searchOptions.copy(replaceText = it))
                    },
                    label = { Text("Replace") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Replace buttons
                TextButton(onClick = onReplace) {
                    Text("Replace")
                }
                
                TextButton(onClick = onReplaceAll) {
                    Text("Replace All")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Search options
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = localCaseSensitive,
                        onCheckedChange = { 
                            localCaseSensitive = it
                            onSearchOptionsChanged(searchOptions.copy(isCaseSensitive = it))
                        }
                    )
                    Text("Case sensitive")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = localWholeWord,
                        onCheckedChange = { 
                            localWholeWord = it
                            onSearchOptionsChanged(searchOptions.copy(isWholeWord = it))
                        }
                    )
                    Text("Whole word")
                }
            }
        }
    }
}

 