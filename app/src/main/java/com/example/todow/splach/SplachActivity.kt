package com.example.todow.splach

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.todow.ExplainActivity
import com.example.todow.LoginActivity
import com.example.todow.MainActivity
import com.example.todow.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplachActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splach)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        supportActionBar?.hide()
        lifecycleScope.launch {
            delay(1500)

            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val isFirstTime = prefs.getBoolean("isFirstTime", true)
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser


            when {
                isFirstTime -> {
                    startActivity(Intent(this@SplachActivity, ExplainActivity::class.java))
                }
                currentUser != null -> {
                    startActivity(Intent(this@SplachActivity, MainActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this@SplachActivity, LoginActivity::class.java))
                }
            }

            finish()
        }

        }
    }
