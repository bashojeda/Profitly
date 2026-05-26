package com.example.profitly.data.api

import android.content.Context

class AuthTokenProvider(context: Context) {
    private val prefs = context.getSharedPreferences("profitly_prefs", Context.MODE_PRIVATE)

    @Volatile
    var token: String? = prefs.getString("auth_token", null)
        private set

    fun saveToken(token: String?) {
        this.token = token
        prefs.edit().putString("auth_token", token).apply()
    }

    fun clear() {
        saveToken(null)
    }
}
