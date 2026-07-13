package com.bankapp.session

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.bankapp.security.EncryptionManager
import com.bankapp.security.KeystoreManager
import org.json.JSONObject

class SessionManager(private val context: Context) {

    private val PREFS_NAME = "bankapp_secure_prefs"
    private val KEY_SESSION_ID = "session_id"
    private val KEY_USER_ID = "user_id"
    private val KEY_USER_NAME = "user_name"
    private val KEY_PHONE = "phone"
    private val KEY_BALANCE = "balance"
    private val KEY_EXPIRATION = "expiration"

    private val sharedPrefs: SharedPreferences

    init {
        Log.d("Session", "--- inicializando SessionManager ---")
        
        val key = KeystoreManager.getOrCreateKey()
        EncryptionManager.init(key)
        
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        
        sharedPrefs = EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        Log.d("Session", "---s2---")
    }


    fun saveSession(session: Session) {
        Log.d("Session", "---saave 3---")
        
        with(sharedPrefs.edit()) {
            putString(KEY_SESSION_ID, session.sessionId)
            putString(KEY_USER_ID, session.userId)
            putString(KEY_USER_NAME, session.userName)
            putString(KEY_PHONE, session.phone)
            putString(KEY_BALANCE, session.balance)
            putLong(KEY_EXPIRATION, session.expiration)
            apply()
        }
        
        Log.d("Session", "--- sesion saved ${session.userName}")
    }


    fun getSession(): Session? {
        Log.d("Session", "---s5---")
        
        val sessionId = sharedPrefs.getString(KEY_SESSION_ID, null)
        val userId = sharedPrefs.getString(KEY_USER_ID, null)
        val userName = sharedPrefs.getString(KEY_USER_NAME, null)
        val phone = sharedPrefs.getString(KEY_PHONE, null)
        val balance = sharedPrefs.getString(KEY_BALANCE, null)
        val expiration = sharedPrefs.getLong(KEY_EXPIRATION, 0)

        if (sessionId == null || userId == null) {
            Log.d("Session", "--- no sesion ---")
            return null
        }

        Log.d("Session", "--- sesion de $userName ---")
        
        return Session(
            sessionId = sessionId,
            userId = userId,
            userName = userName ?: "",
            phone = phone ?: "",
            balance = balance ?: "",
            expiration = expiration
        )
    }

    fun isSessionValid(): Boolean {
        Log.d("Session", "--- validando sesion ---")
        
        val session = getSession()
        if (session == null) {
            Log.w("Session", "--- no hay sesion para validar ---")
            return false
        }

        val now = System.currentTimeMillis()
        val isValid = now < session.expiration
        
        Log.d("Session", "--- sesion valida: $isValid ---")
        Log.d("Session", "--- expira en: ${(session.expiration - now) / 60000} min ---")
        
        if (!isValid) {
            Log.w("Session", "--- sesion expirada ---")
            clearSession()
        }
        
        return isValid
    }

    fun clearSession() {
        Log.d("Session", "--- clearSession---")
        
        with(sharedPrefs.edit()) {
            remove(KEY_SESSION_ID)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_PHONE)
            remove(KEY_BALANCE)
            remove(KEY_EXPIRATION)
            apply()
        }
        
        Log.d("Session", "--- slimpieza de sesion ok")
    }


    fun updateBalance(newBalance: String) {
        Log.d("Session", "- updateBalance 1v--")
        
        with(sharedPrefs.edit()) {
            putString(KEY_BALANCE, newBalance)
            apply()
        }
        
        Log.d("Session", "-updateBalance 2v--")
    }
}