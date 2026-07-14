package com.bankapp

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.bankapp.session.Session
import com.bankapp.session.SessionManager
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.LifecycleState
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.facebook.react.modules.core.DeviceEventManagerModule
import org.json.JSONObject

class MainActivity : ReactActivity() {

    companion object {
        var instance: MainActivity? = null
    }

    private lateinit var container: FrameLayout
    private lateinit var sessionManager: SessionManager
    private var reactInstanceManager: ReactInstanceManager? = null
    private var reactRootView: ReactRootView? = null
    private var currentBundle: String = ""

    override fun getMainComponentName(): String = "BankApp"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

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

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        reactRootView?.unmountReactApplication()
        reactInstanceManager?.destroy()
    }

    fun loadLogin() {
        currentBundle = "login"
        loadBundle("login", null)
    }

    fun loadHome() {
        currentBundle = "home"
        loadBundle("home", null)

        val session = sessionManager.getSession()
        val payload = JSONObject().apply {
            put("userName", session?.userName ?: "")
            put("balance", session?.balance ?: "0")
        }.toString()

        emitToJS("LOAD_HOME", payload)
    }

    fun loadTransfer() {
        currentBundle = "transfer"
        val session = sessionManager.getSession()
        val props = Bundle().apply {
            putString("balance", session?.balance ?: "0")
        }
        loadBundle("transfer", props)
    }

    fun loadMovements() {
        currentBundle = "movements"
        val session = sessionManager.getSession()
        val props = Bundle().apply {
            putString("userId", session?.userId ?: "")
        }
        loadBundle("movements", props)
    }

    private fun loadBundle(bundleName: String, props: Bundle?) {
        Log.d("Main", "--- cargando $bundleName ---")

        reactRootView?.unmountReactApplication()
        reactInstanceManager?.let {
            it.onHostDestroy(this)
            it.destroy()
        }
        container.removeAllViews()

        val instanceManager = ReactInstanceManager.builder()
            .setApplication(application)
            .setCurrentActivity(this)
            .setBundleAssetName("bundles/$bundleName.bundle")
            .setJSMainModulePath("index")
            .setUseDeveloperSupport(BuildConfig.DEBUG)
            .addPackages((application as MainApplication).getReactPackages())
            .setInitialLifecycleState(LifecycleState.RESUMED)
            .build()

        reactInstanceManager = instanceManager

        val rootView = ReactRootView(this)
        reactRootView = rootView
        rootView.startReactApplication(instanceManager, bundleName, props)

        container.addView(rootView)
    }

    private fun emitToJS(eventName: String, data: String?) {
        val manager = reactInstanceManager ?: return
        val context = manager.currentReactContext

        if (context != null) {
            sendDeviceEvent(context, eventName, data)
        } else {
            manager.addReactInstanceEventListener(object : ReactInstanceManager.ReactInstanceEventListener {
                override fun onReactContextInitialized(reactContext: ReactContext) {
                    sendDeviceEvent(reactContext, eventName, data)
                    manager.removeReactInstanceEventListener(this)
                }
            })
        }
    }

    private fun sendDeviceEvent(context: ReactContext, eventName: String, data: String?) {
        context
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, data)
    }

    fun notifySessionExpired() {
        emitToJS("SESSION_EXPIRED", null)
    }

    fun onRNEvent(eventName: String, data: String?) {
        Log.d("Main", "--- event: $eventName ---")

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
                            expiration = System.currentTimeMillis() + (2 * 60 * 1000)
                        )
                        sessionManager.saveSession(session)
                        loadHome()
                    } catch (e: Exception) {
                        Log.e("Main", "--- error LOGIN_SUCCESS: ${e.message} ---")
                    }
                }
            }
            "OPEN_TRANSFER" -> loadTransfer()
            "OPEN_MOVEMENTS" -> loadMovements()
            "TRANSFER_SUCCESS" -> {
                data?.let {
                    try {
                        val json = JSONObject(it)
                        val newBalance = json.optString("newBalance", null)
                        if (newBalance != null) {
                            sessionManager.updateBalance(newBalance)
                        }
                    } catch (e: Exception) {
                        Log.e("Main", "--- error TRANSFER_SUCCESS: ${e.message} ---")
                    }
                }
                loadHome()
            }
            "LOGOUT" -> {
                sessionManager.clearSession()
                loadLogin()
            }
            "GO_BACK" -> loadHome()
            else -> Log.w("Main", "--- evento no manejado: $eventName ---")
        }
    }
}