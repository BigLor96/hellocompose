package com.example.ch07.util

// Tag diketik bebas ("Kuliah, #Ide, kuliah") — di sini dinormalkan sekali supaya
// bentuknya konsisten: huruf kecil, tanpa #, tanpa duplikat, tanpa yang kosong.
fun parseTags(text: String): List<String> = text.split(",")
    .map { it.trim().removePrefix("#").lowercase() }
    .filter { it.isNotEmpty() }
    .distinct()

// Kebalikannya — dipakai saat memuat catatan lama ke form.
fun formatTags(tags: List<String>): String = tags.joinToString(", ")
