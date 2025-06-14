package com.example.todow

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todow.databinding.ActivityExplainBinding

class ExplainActivity : AppCompatActivity() {
    lateinit var binding: ActivityExplainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityExplainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnStarted.setOnClickListener{
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.edit().putBoolean("isFirstTime", false).apply()
            startActivity(Intent(this@ExplainActivity, MainActivity::class.java))
            finish()
        }
    }
}