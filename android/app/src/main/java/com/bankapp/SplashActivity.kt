package com.bankapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import androidx.core.animation.doOnEnd

class SplashActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("Splash", "-1-- onCreate ---")
        
        setContentView(R.layout.activity_splash)
        
        tvTitle = findViewById(R.id.tvSplashTitle)
        
        startAnimations()
        
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("Splash", "---2---")
            
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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