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
import com.facebook.react.common.LifecycleState
import com.facebook.react.defaults.DefaultReactActivityDelegate
import org.json.JSONObject

class MainActivity : ReactActivity() {

    companion object {
        var instance: MainActivity? = null
    }

    private lateinit var container: FrameLayout
    private lateinit var sessionManager: SessionManager
    private var reactInstanceManager: ReactInstanceManager? = null
    private var reactRootView: ReactRootView? = null

    override fun getMainComponentName(): String = "BankApp"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this  

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

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        reactRootView?.unmountReactApplication()
        reactInstanceManager?.destroy()
    }

    fun loadLogin() {
        Log.d("Main", "--- loadLogin--")
        loadBundle("login", null)
    }

    fun loadHome() {
        Log.d("Main", "-- loadHome ---")
        val session = sessionManager.getSession()
        val props = Bundle().apply {
            putString("userName", session?.userName ?: "")
            putString("balance", session?.balance ?: "0")
        }
        loadBundle("home", props)
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
}