package com.example.a2021summer

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2021summer.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
object ipadress{
    @JvmField val urlText = "http://172.30.1.19:14766/byeongseong/".toString()
}
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)




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
        viewBinding.btnSearch.setOnClickListener{
            var keyword = viewBinding.searchContent.text.toString()
            if(keyword.isEmpty()){
                var builder = AlertDialog.Builder(this)
                builder.setMessage("내용을 입력해 주세요.")
                builder.setPositiveButton("확인",{ dialogInterface: DialogInterface, i: Int -> })
                builder.show()
            } else {
                var intent = Intent(this,SearchActivity::class.java)
                intent.putExtra("key",keyword)
                startActivity(intent)
            }
        }

    }
}