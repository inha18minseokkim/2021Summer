package com.example.a2021summer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.a2021summer.databinding.ActivityKakaoPayBinding.inflate
import com.example.a2021summer.databinding.ActivityOrdercheckBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class OrderCheckActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityOrdercheckBinding
    var res = MutableList(2, {index -> 0})
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityOrdercheckBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.totalcost.text = CartData.totalCost.toString()
        thread(start=true) {
            val urlText = ipadress.urlText + "getAccountLocation.jsp?account=" + AccountManager.accountID
            Log.d("OrderCheckActivity",urlText)
            val url = URL(urlText)
            val urlConnection = url.openConnection() as HttpURLConnection
            if(urlConnection.responseCode == HttpURLConnection.HTTP_OK){
                val streamReader = InputStreamReader(urlConnection.inputStream)
                val buffered = BufferedReader(streamReader)
                val content = StringBuilder()
                while(true){
                    val line = buffered.readLine() ?: break
                    content.append(line)
                }
                buffered.close()
                urlConnection.disconnect()
                Log.d("OrderCheckActivity",content.toString())
                runOnUiThread {

                    viewBinding.deliveryspot.setText(content.toString())

                }
            }
        }
        viewBinding.savelocation.setOnClickListener {
            AccountManager.updateLocation(viewBinding.deliveryspot.text.toString())
        }


        viewBinding.deliveryselect.setOnCheckedChangeListener { radioGroup, i ->
            if(i == R.id.yesdelivery) res[0] = 1
            if(i == R.id.nodelivery) res[0] = 0
        }
        viewBinding.payway.setOnCheckedChangeListener{ radioGroup, i ->
            if(i == R.id.kakaopay) res[1] = 0
            if(i == R.id.creditcard) res[1] = 1
            if(i == R.id.payoffline) res[1] = 2
        }
        viewBinding.btnsubmit.setOnClickListener {
            var intent = Intent(this,KakaoPayActivity::class.java)
            var curdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss a").format(Date())
            intent.putExtra("name",curdate.toString())
            intent.putExtra("price",viewBinding.totalcost.text)
            startActivity(intent)
        }
    }


}