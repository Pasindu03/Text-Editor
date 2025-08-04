package com.example.texteditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdvancedTextEditor(
    text: String,
    onTextChanged: (String) -> Unit,
    searchResults: List<Int>,
    currentSearchIndex: Int,
    searchText: String,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }
    
    LaunchedEffect(text) {
        if (textFieldValue.text != text) {
            textFieldValue = TextFieldValue(text)
        }
    }
    
    SelectionContainer {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { 
                textFieldValue = it
                onTextChanged(it.text)
            },
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    innerTextField()
                    
                    // Show search result count
                    if (searchResults.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "${currentSearchIndex + 1}/${searchResults.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SearchHighlightedText(
    text: String,
    searchResults: List<Int>,
    currentSearchIndex: Int,
    searchText: String,
    modifier: Modifier = Modifier
) {
    if (searchText.isEmpty() || searchResults.isEmpty()) {
        Text(
            text = text,
            modifier = modifier,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 20.sp
            )
        )
        return
    }
    
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        
        searchResults.forEachIndexed { resultIndex, startIndex ->
            // Add text before the search result
            if (startIndex > lastIndex) {
                append(text.substring(lastIndex, startIndex))
            }
            
            // Add the search result with highlighting
            val endIndex = startIndex + searchText.length
            val isCurrentResult = resultIndex == currentSearchIndex
            
            pushStringAnnotation(
                tag = "search_result",
                annotation = if (isCurrentResult) "current" else "other"
            )
            append(text.substring(startIndex, endIndex))
            pop()
            
            lastIndex = endIndex
        }
        
        // Add remaining text
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
    
    Text(
        text = annotatedString,
        modifier = modifier,
        style = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 20.sp
        )
    )
}

@Composable
fun LineNumberedTextEditor(
    text: String,
    onTextChanged: (String) -> Unit,
    searchResults: List<Int>,
    currentSearchIndex: Int,
    searchText: String,
    modifier: Modifier = Modifier
) {
    val lines = text.lines()
    
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        // Line numbers
        Column(
            modifier = Modifier
                .width(50.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            lines.forEachIndexed { index, _ ->
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        
        // Text content
        AdvancedTextEditor(
            text = text,
            onTextChanged = onTextChanged,
            searchResults = searchResults,
            currentSearchIndex = currentSearchIndex,
            searchText = searchText,
            modifier = Modifier.weight(1f)
        )
    }
} 