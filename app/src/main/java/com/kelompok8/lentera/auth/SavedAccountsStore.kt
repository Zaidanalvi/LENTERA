package com.kelompok8.lentera.auth

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SavedAccount(
    val email: String,
    val password: String,
    val name: String = "",
    val avatarId: Int = 0
)

/**
 * Menyimpan riwayat akun yang pernah login di perangkat ini (seperti fitur
 * simpan akun pada aplikasi populer) supaya saat pengguna mengetuk kolom Email,
 * daftar akun sebelumnya muncul dan otomatis mengisi email beserta password-nya.
 */
object SavedAccountsStore {
    private const val PREFS_NAME = "lentera_saved_accounts"
    private const val KEY_ACCOUNTS = "accounts_json"
    private const val KEY_REMEMBER_ME = "remember_me"
    private const val MAX_ACCOUNTS = 5

    fun isRememberMeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMEMBER_ME, true)
    }

    fun setRememberMe(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_REMEMBER_ME, enabled)
            .apply()
    }

    fun getAccounts(context: Context): List<SavedAccount> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val email = obj.optString("email").trim()
                    if (email.isNotEmpty()) {
                        add(
                            SavedAccount(
                                email = email,
                                password = obj.optString("password"),
                                name = obj.optString("name"),
                                avatarId = obj.optInt("avatarId", 0)
                            )
                        )
                    }
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveAccount(
        context: Context,
        email: String,
        password: String,
        name: String = "",
        avatarId: Int = 0
    ) {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty()) return

        val existing = getAccounts(context).toMutableList()
        val oldEntry = existing.find { it.email.equals(cleanEmail, ignoreCase = true) }
        existing.removeAll { it.email.equals(cleanEmail, ignoreCase = true) }

        val updated = SavedAccount(
            email = cleanEmail,
            password = password,
            name = name.ifBlank { oldEntry?.name.orEmpty() },
            avatarId = if (name.isNotBlank()) avatarId else (oldEntry?.avatarId ?: avatarId)
        )
        val newList = (listOf(updated) + existing).take(MAX_ACCOUNTS)
        persist(context, newList)
    }

    fun removeAccount(context: Context, email: String) {
        val filtered = getAccounts(context).filterNot { it.email.equals(email.trim(), ignoreCase = true) }
        persist(context, filtered)
    }

    private fun persist(context: Context, accounts: List<SavedAccount>) {
        val array = JSONArray()
        accounts.forEach { acc ->
            array.put(
                JSONObject().apply {
                    put("email", acc.email)
                    put("password", acc.password)
                    put("name", acc.name)
                    put("avatarId", acc.avatarId)
                }
            )
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ACCOUNTS, array.toString())
            .apply()
    }
}
