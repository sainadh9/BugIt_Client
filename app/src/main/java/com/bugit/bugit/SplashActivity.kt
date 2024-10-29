package com.bugit.bugit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bugit.bugit.bugappscope.ui.ReportBugActivity
import com.bugit.bugit.utils.Constants.SPLASH_TIME_OUT

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initViews()


    }

    private fun initViews() {

        Handler(Looper.getMainLooper()).postDelayed({
            val gotoreportBugIntent = Intent(this@SplashActivity, ReportBugActivity::class.java)
            startActivity(gotoreportBugIntent)
            finish()
        }, SPLASH_TIME_OUT)
    }

}