package com.example.ch06.profile

// Profil user — nama dan status notifikasi
data class UserProfile(
    val username: String,
    val notificationsEnabled: Boolean
)

// Sinkron, tanpa suspend, karena datanya lokal
interface ProfileRepository {
    fun getProfile(): UserProfile
}

// Implementasi palsu untuk latihan; nanti bisa diganti penyimpanan asli
class FakeProfileRepository : ProfileRepository {
    override fun getProfile() = UserProfile(
        username             = "Mahasiswa Android",
        notificationsEnabled = true
    )
}
