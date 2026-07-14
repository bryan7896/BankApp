package com.bankapp.modules

import android.util.Log
import com.bankapp.MainActivity
import com.bankapp.session.SessionManager
import com.facebook.react.bridge.*

class RNBridgeModule(
    private val reactContext: ReactApplicationContext,
    private val sessionManager: SessionManager
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "BankBridge"

    @ReactMethod
    fun getSession(callback: Callback) {
        if (!sessionManager.isSessionValid()) {
            reactContext.runOnUiQueueThread {
                MainActivity.instance?.notifySessionExpired()
            }
            callback.invoke(false, null)
            return
        }

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
    fun sendEvent(eventName: String, data: String?) {
        Log.d("Bridge", "--- sendEvent: $eventName ---")
        reactContext.runOnUiQueueThread {
            MainActivity.instance?.onRNEvent(eventName, data)
        }
    }
}