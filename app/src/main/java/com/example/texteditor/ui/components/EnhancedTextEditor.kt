package com.example.texteditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnhancedTextEditor(
    text: String,
    onTextChanged: (String) -> Unit,
    searchResults: List<Int>,
    currentSearchIndex: Int,
    searchText: String,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onCut: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }
    
    LaunchedEffect(text) {
        if (textFieldValue.text != text) {
            textFieldValue = TextFieldValue(text)
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Edit toolbar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Copy button
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Cut button
                IconButton(
                    onClick = onCut,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Cut",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Paste button
                IconButton(
                    onClick = onPaste,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Paste",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                VerticalDivider(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 4.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                
                // Undo button
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (canUndo) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) MaterialTheme.colorScheme.onSecondaryContainer
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Redo button
                IconButton(
                    onClick = onRedo,
                    enabled = canRedo,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (canRedo) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) MaterialTheme.colorScheme.onSecondaryContainer
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Search result indicator
                if (searchResults.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentSearchIndex + 1}/${searchResults.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }
        
        // Text editor area
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            SelectionContainer {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { 
                        textFieldValue = it
                        onTextChanged(it.text)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            innerTextField()
                            
                            // Search highlighting overlay
                            if (searchResults.isNotEmpty() && searchText.isNotEmpty()) {
                                SearchHighlightOverlay(
                                    text = text,
                                    searchResults = searchResults,
                                    currentSearchIndex = currentSearchIndex,
                                    searchText = searchText
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SearchHighlightOverlay(
    text: String,
    searchResults: List<Int>,
    currentSearchIndex: Int,
    searchText: String,
    modifier: Modifier = Modifier
) {
    // This is a simplified highlight overlay
    // In a real implementation, you'd need more sophisticated text measurement
    if (searchResults.isNotEmpty() && currentSearchIndex >= 0 && currentSearchIndex < searchResults.size) {
        val currentPosition = searchResults[currentSearchIndex]
        val lines = text.lines()
        var charCount = 0
        var lineIndex = 0
        
        for (line in lines) {
            if (charCount + line.length >= currentPosition) {
                val charInLine = currentPosition - charCount
                val lineHeight = 24.dp
                val charWidth = 9.dp // Approximate character width
                
                Box(
                    modifier = modifier
                        .offset(
                            x = (charInLine * charWidth.value).dp,
                            y = (lineIndex * lineHeight.value).dp
                        )
                        .size(
                            width = (searchText.length * charWidth.value).dp,
                            height = lineHeight
                        )
                        .background(
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                break
            }
            charCount += line.length + 1 // +1 for newline
            lineIndex++
        }
    }
}

@Composable
fun EnhancedSearchBar(
    searchOptions: com.example.texteditor.data.SearchOptions,
    searchResults: List<Int>,
    currentSearchIndex: Int,
    onSearchOptionsChanged: (com.example.texteditor.data.SearchOptions) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onReplace: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit
) {
    var localSearchText by remember { mutableStateOf(searchOptions.searchText) }
    var localReplaceText by remember { mutableStateOf(searchOptions.replaceText) }
    var localCaseSensitive by remember { mutableStateOf(searchOptions.isCaseSensitive) }
    var localWholeWord by remember { mutableStateOf(searchOptions.isWholeWord) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Find & Replace",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = localSearchText,
                    onValueChange = { 
                        localSearchText = it
                        onSearchOptionsChanged(searchOptions.copy(searchText = it))
                    },
                    label = { Text("Find") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Navigation buttons
                IconButton(
                    onClick = onPrevious,
                    enabled = searchResults.isNotEmpty(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (searchResults.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Previous",
                        tint = if (searchResults.isNotEmpty()) MaterialTheme.colorScheme.onPrimaryContainer
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = if (searchResults.isNotEmpty()) "${currentSearchIndex + 1}/${searchResults.size}" else "0/0",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                IconButton(
                    onClick = onNext,
                    enabled = searchResults.isNotEmpty(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (searchResults.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Next",
                        tint = if (searchResults.isNotEmpty()) MaterialTheme.colorScheme.onPrimaryContainer
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Replace input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = localReplaceText,
                    onValueChange = { 
                        localReplaceText = it
                        onSearchOptionsChanged(searchOptions.copy(replaceText = it))
                    },
                    label = { Text("Replace with") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Replace buttons
                Button(
                    onClick = onReplace,
                    enabled = searchResults.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Replace")
                }
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Button(
                    onClick = onReplaceAll,
                    enabled = searchResults.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("All")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Search options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = localCaseSensitive,
                        onCheckedChange = { 
                            localCaseSensitive = it
                            onSearchOptionsChanged(searchOptions.copy(isCaseSensitive = it))
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text("Case sensitive")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = localWholeWord,
                        onCheckedChange = { 
                            localWholeWord = it
                            onSearchOptionsChanged(searchOptions.copy(isWholeWord = it))
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text("Whole word")
                }
            }
        }
    }
} 