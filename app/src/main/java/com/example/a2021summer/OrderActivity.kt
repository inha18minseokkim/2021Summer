package com.example.a2021summer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a2021summer.databinding.ActivitySubBinding

class OrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
    }

    override fun onBackPressed(){
        finish()
    }
}