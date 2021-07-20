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
        var key = intent.getSerializableExtra("key") as String
        thread(start=true){
            var shoplist = jsonManager.searchShopList(key)
            var shopadapter = ShopListAdapter(mainContext,shoplist)
            //Log.d("searchactivity",)
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