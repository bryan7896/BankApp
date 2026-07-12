package com.bankapp.modules

import android.util.Log
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
    fun logout(callback: Callback) {
        Log.d("Bridge", "--- logout ---")
        sessionManager.clearSession()
        callback.invoke(true)
    }
}