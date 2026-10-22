package com.example.ch07.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Satu objek = satu baris tabel "notes".
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    // Terakhir kali isi diubah — menyematkan tidak menyentuh kolom ini.
    val updatedAt: Long = createdAt,
    // List<String> tidak dikenal Room, jadi disimpan sebagai JSON di Converters.
    val tags: List<String> = emptyList()
)
