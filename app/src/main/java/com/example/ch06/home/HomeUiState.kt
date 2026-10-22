package com.example.ch06.home

import com.example.ch06.data.Article

// Single source of truth untuk layar Home
data class HomeUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val query: String = "",
    val errorMessage: String? = null
) {
    // Properti turunan: pencarian selesai tapi tidak ada yang cocok
    val isEmptyResult: Boolean
        get() = !isLoading && errorMessage == null && articles.isEmpty()
}
