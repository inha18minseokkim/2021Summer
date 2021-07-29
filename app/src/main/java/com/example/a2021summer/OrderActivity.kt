package com.example.a2021summer

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2021summer.databinding.ActivityOrderBinding
import com.example.a2021summer.databinding.ActivitySubBinding
import kotlinx.coroutines.delay
import kotlin.concurrent.thread

class OrderActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityOrderBinding
    var tmpidx = 0
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d("OrderListAdapter","여기까지는됨")
        viewBinding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.ordertoolbar)
        /*리사이클러뷰 아답터 설정해서 뿌리기*/
        var orderContext = this
        thread(start=true){
            JSONManager.loadCartData()
            var li = CartData.getDataForOrderAdapter()
            Log.d("OrderListAdapter",li.size.toString())
            var orderadapter = OrderListAdapter(orderContext, li)
            Log.d("OrderListAdapter","여기까지는됨2")
            runOnUiThread {
                viewBinding.recycleView.adapter = orderadapter
                var layout = LinearLayoutManager(orderContext)
                viewBinding.recycleView.layoutManager = layout
                viewBinding.recycleView.setHasFixedSize(true)
                Log.d("OrderListAdapter","여기까지는됨3")
                orderadapter.notifyDataSetChanged()
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater = getMenuInflater()
        menuInflater.inflate(com.example.a2021summer.R.menu.order_toolbar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            com.example.a2021summer.R.id.btnsave -> {

            }
        }
        return true
    }
    override fun onBackPressed(){
        finish()
    }
}