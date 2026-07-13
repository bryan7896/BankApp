package com.bankapp

import android.app.Application
import android.util.Log
import com.bankapp.modules.RNBridgeModule
import com.bankapp.session.SessionManager
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ViewManager
import com.facebook.soloader.SoLoader

class MainApplication : Application(), ReactApplication {

  private val bridgePackage = object : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
      Log.d("App", "--- registrando RNBridgeModule ---")
      return listOf(RNBridgeModule(reactContext, SessionManager(reactContext)))
    }
    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
      return emptyList()
    }
  }

  private val allPackages: List<ReactPackage> by lazy {
    PackageList(this).packages.apply {
      add(bridgePackage)
    }
  }

  override val reactNativeHost: ReactNativeHost =
      object : DefaultReactNativeHost(this) {
        override fun getPackages(): List<ReactPackage> = allPackages

        override fun getJSMainModuleName(): String = "index"

        override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

        override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
        override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
      }

  override val reactHost: ReactHost
    get() = getDefaultReactHost(applicationContext, reactNativeHost)

  fun getReactPackages(): List<ReactPackage> = allPackages

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this, OpenSourceMergedSoMapping)
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
        DefaultNewArchitectureEntryPoint.load()
    }
  }
}
