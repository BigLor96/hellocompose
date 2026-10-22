package com.example.ch07.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteListUiState(
    val isLoading: Boolean = false,
    val notes: List<NoteEntity> = emptyList(),
    val query: String = "",
    // Catatan yang baru dihapus — sementara ditawarkan pengembalian lewat Snackbar.
    val recentlyDeleted: NoteEntity? = null,
    // Chip tag: semua tag yang masih dipakai, dan yang sedang menyaring daftar.
    val availableTags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val errorMessage: String? = null
) {
    // Turunan: sedang mencari/memfilter atau tidak (menentukan pesan kosong).
    val isFiltering: Boolean
        get() = query.isNotBlank() || selectedTag != null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NoteListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    // Teks yang sedang diketik (langsung tampil di TextField)
    private val queryText = MutableStateFlow("")
    private val loadError = MutableStateFlow<String?>(null)
    // Catatan yang baru dihapus, dan tag yang sedang menyaring daftar.
    private val recentlyDeleted = MutableStateFlow<NoteEntity?>(null)
    private val selectedTag = MutableStateFlow<String?>(null)

    // Query ke database ditunda 300 ms setelah pengguna berhenti mengetik
    // (debounce), dan hanya query TERBARU yang diamati (flatMapLatest).
    // Query kosong tidak ditunda: daftar penuh tampil segera.
    private val searchResults: Flow<List<NoteEntity>> = queryText
        .debounce { query -> if (query.isBlank()) 0L else 300L }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllNotes()
            else repository.searchNotes(query)
        }
        .catch { e ->
            loadError.value = e.message ?: "Gagal memuat catatan"
            emit(emptyList())
        }

    // Semua catatan, apa pun hasil pencarian — sumber daftar chip tag.
    private val allNotes: Flow<List<NoteEntity>> = repository.getAllNotes()
        .catch { emit(emptyList()) }

    // UiState DITURUNKAN dari beberapa sumber (pola Chapter 6).
    // combine cuma muat 5 flow, jadi recentlyDeleted dan selectedTag digabung
    // dulu jadi satu pasangan sebelum masuk ke hitungan utama.
    val uiState: StateFlow<NoteListUiState> = combine(
        combine(recentlyDeleted, selectedTag) { deleted, tag -> deleted to tag },
        searchResults,
        queryText,
        loadError,
        allNotes
    ) { deletedAndTag, results, query, error, all ->
        val (deleted, tag) = deletedAndTag
        // Tag yang sudah tidak dipakai catatan mana pun otomatis dilepas.
        val activeTag = tag?.takeIf { t -> all.any { t in it.tags } }
        val visible = if (activeTag == null) results
        else results.filter { activeTag in it.tags }
        NoteListUiState(
            notes = visible,
            query = query,
            recentlyDeleted = deleted,
            availableTags = all.flatMap { it.tags }.distinct().sorted(),
            selectedTag = activeTag,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteListUiState(isLoading = true)
    )

    fun onQueryChange(newQuery: String) {
        queryText.value = newQuery
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            // Disimpan setelah benar-benar terhapus, untuk tombol "Batalkan".
            recentlyDeleted.value = note
        }
    }

    // Mengembalikan catatan yang sama persis — id ikut dibawa, jadi REPLACE
    // di repository menaruhnya kembali di posisi semula.
    fun undoDelete() {
        val note = recentlyDeleted.value ?: return
        recentlyDeleted.value = null
        viewModelScope.launch { repository.insertNote(note) }
    }

    // Snackbar hilang sendiri tanpa ditekan: catatan tetap terhapus.
    fun onUndoDismissed() {
        recentlyDeleted.value = null
    }

    // Ketuk tag yang sedang aktif berarti melepas filter; null = "Semua".
    fun onTagSelected(tag: String?) {
        selectedTag.value = if (tag != null && tag == selectedTag.value) null else tag
    }

    // Menyematkan tidak mengubah updatedAt (bukan perubahan isi catatan)
    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }
}
