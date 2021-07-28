package com.example.a2021summer

import android.content.Context
import android.os.AsyncTask
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class OrderListAdapter(var context: Context, var data: ArrayList<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var adapterContext = this
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
            1 -> { //가게이름
                Log.d("OrderListAdapter","가게이름")
                ShopViewHolder.create(parent, this)

            }
            2 -> {//메뉴리스트
                Log.d("OrderListAdapter","가게내용")
                MenuViewHolder.create(parent, this)
            }
            else -> {
                Log.d("OrderListAdapter","뭔가잘못됨")
                throw Exception("이게뭐노;;")
            }
        }


    override fun getItemViewType(position: Int): Int {
        when(data[position]) {
            is menuTitle -> {
                return 1
            }
            is menuElement -> {
                return 2
            }
            else -> {
                Log.d("OrderListAdapter","뭐야?")
                throw Exception("뭔가 잘못됨")
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is ShopViewHolder -> {
                holder.bind(data[position] as menuTitle)
            }
            is MenuViewHolder -> {
                holder.bind(data[position] as menuElement)
            }
            else -> {
                Log.d("OrderListAdapter","뭐야")
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("OrderListAdapter","getItemCount"+data.size)
        return data.size
    }

    class ShopViewHolder(var view: View, var adapter: OrderListAdapter): RecyclerView.ViewHolder(view) {
        val shopName = view.findViewById<TextView>(R.id.cartshopName)
        fun bind(item: menuTitle){
            var shopnametask = GetShopNameTask(item.shopname,shopName,adapter)
            shopnametask.execute()
            //shopName.text = item.shopname
            Log.d("OrderListAdapter",item.shopname)
        }
        companion object Factory{
            fun create(parent: ViewGroup, adapter: OrderListAdapter) : ShopViewHolder {
                var layoutInflater = LayoutInflater.from(parent.context)
                var view = layoutInflater.inflate(R.layout.cart_title,parent,false)
                return ShopViewHolder(view, adapter)
            }
        }
    }
    class MenuViewHolder(var view: View, var adapter: OrderListAdapter): RecyclerView.ViewHolder(view) {
        val menuName = view.findViewById<TextView>(R.id.cartmenuName)
        val menuPrice = view.findViewById<TextView>(R.id.cartmenuPrice)
        val quantity = view.findViewById<TextView>(R.id.Quantity)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)
        fun bind(item: menuElement){
            //menuName.text = item.shopMenu
            var menunametask = GetMenuNameTask(item.shopname,item.shopMenu,menuName,adapter)
            menunametask.execute()
            Log.d("OrderListAdapter",item.shopMenu.toString() + item.menuCount + "ASDF")
            quantity.text = item.menuCount
            btnPlus.setOnClickListener {
                var curcnt: Int = Integer.parseInt(quantity.text.toString())
                curcnt += 1
                quantity.text = curcnt.toString()
                CartData.modifyCount(item.shopname,Integer.parseInt(item.shopMenu),1)
                thread(start=true) {JSONManager.updateCartData(CartData.convertToJSON())}
            }
            btnMinus.setOnClickListener {
                var curcnt: Int = Integer.parseInt(quantity.text.toString())
                if(curcnt != 0){
                    curcnt -= 1
                    quantity.text = curcnt.toString()
                    CartData.modifyCount(item.shopname,Integer.parseInt(item.shopMenu),-1)
                    thread(start=true) {JSONManager.updateCartData(CartData.convertToJSON())}
                }

            }
        }
        companion object Factory{
            fun create(parent: ViewGroup, adapter: OrderListAdapter) : MenuViewHolder {
                var layoutInflater = LayoutInflater.from(parent.context)
                var view = layoutInflater.inflate(R.layout.cart_list,parent,false)
                return MenuViewHolder(view, adapter)
            }
        }

    }

}
class GetShopNameTask(var menuTableName: String, var targetShopName: TextView, var adapter: OrderListAdapter) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg p0: String?): String {
        var urlText = ipadress.urlText + "getShortData/getShopNamebytableName.jsp?menutable=" + menuTableName
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
            return content.toString()
        }
        return "오류다오류"
    }

    override fun onPostExecute(result: String?) {
        targetShopName.text = result
    }
}
class GetMenuNameTask(var menuTableName: String, var menuIdx: String, var targetMenuName: TextView, var adapter: OrderListAdapter) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg p0: String?): String {
        var urlText = ipadress.urlText + "getShortData/getMenuName.jsp?menutable=" + menuTableName + "&&menuidx="+menuIdx
        Log.d("GetMenuNameTask",urlText)
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
            return content.toString()
        }
        return "오류다오류"
    }

    override fun onPostExecute(result: String?) {
        targetMenuName.text = result
    }
}