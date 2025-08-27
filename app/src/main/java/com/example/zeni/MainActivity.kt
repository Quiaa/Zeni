package com.example.zeni

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We just need to set the content view. The NavHostFragment will handle the rest.
        setContentView(R.layout.activity_main)
    }
}