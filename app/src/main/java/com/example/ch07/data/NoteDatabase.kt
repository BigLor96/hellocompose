package com.example.ch07.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Versi 1 -> 2: kolom tag. Daftar lama diwakili '[]' supaya tetap terbaca.
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes ADD COLUMN tags TEXT NOT NULL DEFAULT '[]'")
    }
}

// Versi 2 -> 3: kolom updatedAt, lalu diisi dari createdAt — catatan lama jangan
// sampai tampil "puluhan tahun lalu".
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE notes SET updatedAt = createdAt")
    }
}

@Database(
    entities = [NoteEntity::class],
    version = 3,                 // naik +1 setiap skema berubah
    exportSchema = true          // JSON skema -> app/schemas/ (lihat build.gradle.kts)
)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        // @Volatile: INSTANCE selalu dibaca dari memori utama, bukan cache thread
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        // synchronized: mencegah dua thread membuat instance bersamaan
        fun getInstance(context: Context): NoteDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    // Migration dipasang berurutan: 1 -> 2 -> 3. Dengan
                    // fallbackToDestructiveMigration data pengguna hilang saat
                    // versi naik, jadi tidak dipakai di hasil akhir.
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
