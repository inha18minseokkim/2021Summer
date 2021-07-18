package com.example.a2021summer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2021summer.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
object ipadress{
    @JvmField val urlText = "http://192.168.1.101:14766/byeongseong/".toString()
}
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)


        viewBinding.btnRequest.setOnClickListener{

            thread(start=true){//스레드로 시작
                val urlText = ipadress.urlText + "index.jsp"
                val url = URL(urlText)//url 객체 생성
                val urlConnection = url.openConnection() as HttpURLConnection//openConnection으로 서버와 연결, HttpURLConnection으로 변환
                if(urlConnection.responseCode == HttpURLConnection.HTTP_OK){//응답이 괜찮으면
                    val streamReader = InputStreamReader(urlConnection.inputStream)//입력스트림 연결
                    val buffered = BufferedReader(streamReader) //버퍼에 리더 담아

                    val content = StringBuilder()
                    while(true){
                        val line = buffered.readLine() ?: break
                        content.append(line)
                    }
                    buffered.close()
                    urlConnection.disconnect()
                    runOnUiThread{
                        viewBinding.textView.text = content.toString()
                    }
                }
            }
        }

        /*viewBinding.button2.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }

        viewBinding.button3.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }

        viewBinding.button4.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }

        viewBinding.button5.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }*/

        //메인화면 레이아웃 배치
        var jsonManager = JSONManager()
        var mainContext = this
        thread(start=true){
            var shoplist = jsonManager.loadAllShopList()
            var shopadapter = ShopListAdapter(mainContext,shoplist)
            Log.d("recycleView",shoplist.size.toString())
            runOnUiThread{
                viewBinding.mainshoplist.adapter = shopadapter
                var layout = LinearLayoutManager(mainContext)
                viewBinding.mainshoplist.layoutManager = layout
                viewBinding.mainshoplist.setHasFixedSize(true)
                shopadapter.notifyDataSetChanged()
            }
        }

    }
}