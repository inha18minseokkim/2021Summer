package com.example.a2021summer

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.a2021summer.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)
        viewBinding.textView.text = "ASDFASFDASDFA"
        viewBinding.btnRequest.setOnClickListener{
            thread(start=true){//스레드로 시작
                val urlText = "http://118.45.175.171:14766/byeongseong/index.jsp".toString()
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
    }
}