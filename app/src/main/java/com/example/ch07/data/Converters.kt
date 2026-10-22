package com.example.ch07.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Room hanya menyimpan nilai primitif, jadi daftar tag dipinjamkan ke JSON
// dan disimpan sebagai teks biasa di kolom `tags`.
class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromTags(tags: List<String>): String = gson.toJson(tags)

    @TypeConverter
    fun toTags(value: String): List<String> =
        if (value.isBlank()) emptyList()
        else gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
}
