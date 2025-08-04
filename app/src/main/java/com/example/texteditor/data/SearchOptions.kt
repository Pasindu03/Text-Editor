package com.example.texteditor.data

data class SearchOptions(
    val searchText: String = "",
    val replaceText: String = "",
    val isCaseSensitive: Boolean = false,
    val isWholeWord: Boolean = false,
    val isRegex: Boolean = false
) 