package com.example.a2021summer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
    }

    override fun onBackPressed(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}