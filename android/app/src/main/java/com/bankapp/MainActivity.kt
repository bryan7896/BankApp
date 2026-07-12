package com.bankapp

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.bankapp.modules.RNBridgeModule
import com.bankapp.session.Session
import com.bankapp.session.SessionManager
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.bridge.Arguments
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.facebook.react.modules.core.DeviceEventManagerModule
import org.json.JSONObject

class MainActivity : ReactActivity() {

    private lateinit var container: FrameLayout
    private lateinit var sessionManager: SessionManager

    override fun getMainComponentName(): String = "BankApp"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("Main", "--onCreate --a")
        
        sessionManager = SessionManager(this)
        
        container = FrameLayout(this)
        container.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        setContentView(container)
        
        if (sessionManager.isSessionValid()) {
            loadHome()
        } else {
            loadLogin()
        }
    }

    private fun loadLogin() {
        Log.d("Main", "--- loadLogin--")
        loadBundle("login.bundle", null)
    }

    private fun loadHome() {
        Log.d("Main", "-- loadHome ---")
        val session = sessionManager.getSession()
        val props = Bundle().apply {
            putString("userName", session?.userName ?: "")
            putString("balance", session?.balance ?: "0")
        }
        loadBundle("home.bundle", props)
    }

    private fun loadBundle(bundleName: String, props: Bundle?) {
        Log.d("Main", "--- cargando $bundleName ---")
    }

    fun onRNEvent(eventName: String, data: String?) {
        Log.d("Main", "event $eventName --")
        
        when (eventName) {
            "LOGIN_SUCCESS" -> {
                data?.let {
                    try {
                        val json = JSONObject(it)
                        val session = Session(
                            sessionId = json.getString("sessionId"),
                            userId = json.getString("userId"),
                            userName = json.getString("userName"),
                            phone = json.getString("phone"),
                            balance = json.getString("balance"),
                            expiration = System.currentTimeMillis() + (30 * 60 * 1000)
                        )
                        sessionManager.saveSession(session)
                        Log.d("Main", "--giuardada sesion--")
                        loadHome()
                    } catch (e: Exception) {
                        Log.e("Main", "--- error: ${e.message} ---")
                    }
                }
            }
            "LOGOUT" -> {
                sessionManager.clearSession()
                Log.d("Main", "--- session eliminada ---")
                loadLogin()
            }
            "NAVIGATE_TO_TRANSFER" -> {
                Log.d("Main", "--test navigation Transfer ---")
            }
            "NAVIGATE_TO_MOVEMENTS" -> {
                Log.d("Main", "--- test navigation Movements ---")
            }
            else -> {
                Log.w("Main", "--- evento raro --> $eventName ---")
            }
        }
    }
}