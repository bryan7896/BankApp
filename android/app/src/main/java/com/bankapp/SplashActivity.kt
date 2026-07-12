package com.bankapp

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bankapp.session.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("Splash", "-1-- onCreate ---")
        
        setContentView(R.layout.activity_splash)
        
        tvTitle = findViewById(R.id.tvSplashTitle)
        
        sessionManager = SessionManager(this)
        
        startAnimations()
        
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("Splash", "sesion init---r---")
            
            if (sessionManager.isSessionValid()) {
                Log.d("Splash", "sesion init---a1---")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("loadHome", true)
                startActivity(intent)
            } else {
                Log.d("Splash", "sesion init---a2---")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("loadLogin", true)
                startActivity(intent)
            }
            finish()
        }, 2500) 
    }

    private fun startAnimations() {
        Log.d("spsh", "--- start ---")
        
        val fadeInTitle = ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f)
        fadeInTitle.duration = 1000
        fadeInTitle.interpolator = AccelerateDecelerateInterpolator()
        fadeInTitle.start()
        
        val scaleUp = ObjectAnimator.ofFloat(tvTitle, "scaleX", 0.8f, 1f)
        scaleUp.duration = 800
        scaleUp.interpolator = AccelerateDecelerateInterpolator()
        scaleUp.repeatCount = 1
        scaleUp.repeatMode = ValueAnimator.REVERSE
        scaleUp.start()
        
        val scaleUpY = ObjectAnimator.ofFloat(tvTitle, "scaleY", 0.8f, 1.2f)
        scaleUpY.duration = 800
        scaleUpY.interpolator = AccelerateDecelerateInterpolator()
        scaleUpY.repeatCount = 1
        scaleUpY.repeatMode = ValueAnimator.REVERSE
        scaleUpY.start()
        
        Log.d("Splash", "---3--end-")
    }
}