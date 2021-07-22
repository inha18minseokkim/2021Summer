package com.example.a2021summer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2021summer.databinding.ActivitySearchBinding
import kotlin.concurrent.thread

class SearchActivity: AppCompatActivity() {
    lateinit var viewBinding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        var jsonManager = JSONManager()
        var mainContext = this
        var tmp = intent.getSerializableExtra("key")
        Log.d("searchactivityXX",tmp.javaClass.toString())
        Log.d("searchactivityXX",String.javaClass.toString())
        if (tmp.javaClass.toString().equals("class java.lang.String")){
            var key = tmp as String
            Log.d("searchactivity",key)
            thread(start=true){
                var shoplist = jsonManager.searchShopList(key)
                var shopadapter = ShopListAdapter(mainContext,shoplist)
                Log.d("searchactivity",shoplist.toString())
                runOnUiThread{
                    viewBinding.mainshoplist.adapter = shopadapter
                    var layout = LinearLayoutManager(mainContext)
                    viewBinding.mainshoplist.layoutManager = layout
                    viewBinding.mainshoplist.setHasFixedSize(true)
                    shopadapter.notifyDataSetChanged()
                }
            }
        } else if(tmp.javaClass.toString().equals("class java.lang.Integer")){
            var key = tmp as Int
            Log.d("searchactivity",key.toString())
            thread(start=true){
                var shoplist = jsonManager.searchShopList(key)
                var shopadapter = ShopListAdapter(mainContext,shoplist)
                Log.d("searchactivity",shoplist.toString())
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


}