package com.kelompok8.lentera.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kelompok8.lentera.core.data.AuthRepository
import com.kelompok8.lentera.core.model.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    private val users get() = db.collection("users")

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val firebaseUser = auth.signInWithEmailAndPassword(email, password).await().user
            ?: error("Login gagal, coba lagi")

        // Profil lengkap (gender, lokasi, avatar) disimpan di Firestore, bukan di Firebase Auth
        val profile = users.document(firebaseUser.uid).get().await()
        profile.toUser(firebaseUser.uid, firebaseUser.email.orEmpty(), firebaseUser.displayName.orEmpty())
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        gender: String,
        location: String,
        avatarId: Int
    ): Result<User> = runCatching {
        val firebaseUser = auth.createUserWithEmailAndPassword(email, password).await().user
            ?: error("Registrasi gagal, coba lagi")

        // Nama juga disimpan di profil Auth supaya bisa dibaca tanpa query Firestore
        val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        firebaseUser.updateProfile(profileUpdate).await()

        val user = User(
            uid = firebaseUser.uid,
            name = name,
            coin = 0,
            email = email,
            gender = gender,
            location = location,
            avatarId = avatarId
        )
        users.document(user.uid).set(
            mapOf(
                "name" to user.name,
                "email" to user.email,
                "coin" to user.coin,
                "gender" to user.gender,
                "location" to user.location,
                "avatarId" to user.avatarId,
                "level" to user.level,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
        user
    }

    // Firebase menyimpan sesi login di HP, jadi setelah aplikasi ditutup user tetap masuk
    override fun currentUser(): User? = auth.currentUser?.let {
        User(uid = it.uid, name = it.displayName.orEmpty(), email = it.email.orEmpty())
    }

    override suspend fun getProfile(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return runCatching {
            users.document(firebaseUser.uid).get().await()
                .toUser(firebaseUser.uid, firebaseUser.email.orEmpty(), firebaseUser.displayName.orEmpty())
        }.getOrNull()
    }

    override suspend fun updateProfile(
        name: String,
        gender: String,
        location: String,
        avatarId: Int
    ): Result<User> = runCatching {
        val firebaseUser = auth.currentUser ?: error("Sesi login berakhir, silakan masuk lagi")

        users.document(firebaseUser.uid).update(
            mapOf(
                "name" to name,
                "gender" to gender,
                "location" to location,
                "avatarId" to avatarId
            )
        ).await()

        // Nama di profil Auth ikut diganti supaya currentUser() tetap sesuai
        val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        firebaseUser.updateProfile(profileUpdate).await()

        getProfile() ?: error("Profil tidak ditemukan")
    }

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    override fun logout() = auth.signOut()

    private fun DocumentSnapshot.toUser(uid: String, email: String, fallbackName: String) = User(
        uid = uid,
        name = getString("name") ?: fallbackName,
        coin = getLong("coin")?.toInt() ?: 0,
        email = email,
        gender = getString("gender").orEmpty(),
        location = getString("location").orEmpty(),
        avatarId = getLong("avatarId")?.toInt() ?: 0,
        level = getString("level") ?: "Pemula"
    )
}
