package com.bankapp.modules

import android.util.Log
import com.bankapp.MainActivity
import com.bankapp.session.Session
import com.bankapp.session.SessionManager
import com.facebook.react.bridge.*

class RNBridgeModule(
    private val reactContext: ReactApplicationContext,
    private val sessionManager: SessionManager
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "BankBridge"

    @ReactMethod
    fun getSession(callback: Callback) {
        Log.d("Bridge", "--- getSession ---")
        val session = sessionManager.getSession()
        if (session == null) {
            callback.invoke(false, null)
            return
        }
        val result = Arguments.createMap().apply {
            putString("sessionId", session.sessionId)
            putString("userName", session.userName)
            putString("balance", session.balance)
        }
        callback.invoke(true, result)
    }

    @ReactMethod
    fun loginSuccess(sessionJson: String) {
        Log.d("Bridge", "--- loginSuccess ---")
        try {
            val json = org.json.JSONObject(sessionJson)
            val session = Session(
                sessionId = json.getString("sessionId"),
                userId = json.getString("userId"),
                userName = json.getString("userName"),
                phone = json.getString("phone"),
                balance = json.getString("balance"),
                expiration = System.currentTimeMillis() + (30 * 60 * 1000)
            )
            sessionManager.saveSession(session)

            // Cambiar al bundle de Home en el hilo principal
            reactContext.runOnUiQueueThread {
                MainActivity.instance?.loadHome()
            }
        } catch (e: Exception) {
            Log.e("Bridge", "--- error en loginSuccess: ${e.message} ---")
        }
    }

    @ReactMethod
    fun performLogout() {
        Log.d("Bridge", "--- performLogout ---")
        sessionManager.clearSession()
        reactContext.runOnUiQueueThread {
            MainActivity.instance?.loadLogin()
        }
    }
}