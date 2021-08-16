package com.example.a2021summer

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import kotlin.properties.Delegates

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
                holder.bind(data[position] as menuTitle, position)
            }
            is MenuViewHolder -> {
                holder.bind(data[position] as menuElement, position)
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
        fun bind(item: menuTitle, idx: Int){
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
        var menuviewholder = this
        val menuName = view.findViewById<TextView>(R.id.cartmenuName)
        val menuPrice = view.findViewById<TextView>(R.id.cartmenuPrice)
        val quantity = view.findViewById<TextView>(R.id.Quantity)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)
        val productRes = view.findViewById<TextView>(R.id.productRes)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        var curPrice: Int by Delegates.observable(0) {
            props,old,new ->
            productRes.text = curPrice.toString()
        }
        fun bind(item: menuElement, idx: Int){
            //menuName.text = item.shopMenu
            quantity.text = item.menuCount
            var menutask = GetMenuInfoTask(adapter.context, menuviewholder, item.shopname,item.shopMenu,menuName,menuPrice,productRes,Integer.parseInt(item.menuCount))
            menutask.execute()
            Log.d("OrderListAdapter",item.shopMenu.toString() + item.menuCount + "ASDF")

            btnPlus.setOnClickListener {
                var curcnt: Int = Integer.parseInt(quantity.text.toString())
                curcnt += 1
                quantity.text = curcnt.toString()
                curPrice += Integer.parseInt(menuPrice.text.toString())
                CartData.modifyCount(item.shopname,Integer.parseInt(item.shopMenu),1)
                thread(start=true) {
                    JSONManager.updateCartData(CartData.convertToJSON())
                    var res = CartData.getDataForOrderAdapter()
                }
                CartData.totalCost += Integer.parseInt(menuPrice.text.toString())
                (adapter.context as OrderActivity).viewBinding.totalCost.text = CartData.totalCost.toString()
            }
            btnMinus.setOnClickListener {
                var curcnt: Int = Integer.parseInt(quantity.text.toString())
                if(curcnt > 1){
                    curcnt -= 1
                    quantity.text = curcnt.toString()
                    curPrice -= Integer.parseInt(menuPrice.text.toString())
                    CartData.modifyCount(item.shopname,Integer.parseInt(item.shopMenu),-1)
                    thread(start=true) {
                        JSONManager.updateCartData(CartData.convertToJSON())
                        var res = CartData.getDataForOrderAdapter()
                    }
                    CartData.totalCost -= Integer.parseInt(menuPrice.text.toString())
                    (adapter.context as OrderActivity).viewBinding.totalCost.text = CartData.totalCost.toString()
                }
            }
            btnDelete.setOnClickListener {
                AlertDialog.Builder(adapter.context).setTitle("삭제").setMessage("삭제할래?").setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener{
                            dialog, whichButton ->
                        CartData.delElement(idx)
                        CartData.totalCost -= curPrice
                        thread(start=true){
                            JSONManager.updateCartData(CartData.convertToJSON())
                            var res = CartData.getDataForOrderAdapter()
                            (adapter.context as OrderActivity).runOnUiThread{
                                adapter.data = res
                                CartData.totalCost = 0
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }).setNegativeButton("no",DialogInterface.OnClickListener({
                        dialog, whichButton ->

                })).show()

                /*CartData.delElement(idx)
                thread(start=true){
                    JSONManager.updateCartData(CartData.convertToJSON())
                    var res = CartData.getDataForOrderAdapter()
                    (adapter.context as OrderActivity).runOnUiThread{
                        adapter.data = res
                        adapter.notifyDataSetChanged()
                    }
                }*/
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

class GetMenuInfoTask(var orderActivity: Context, var viewholder: OrderListAdapter.MenuViewHolder, var menuTableName: String, var menuIdx: String,  var targetMenuName: TextView, var targetMenuPrice: TextView,
                      var productRes: TextView, var cnt: Int) : AsyncTask<String,Void,Pair<String,String>>(){
    override fun doInBackground(vararg p0: String?): Pair<String, String> {
        lateinit var menuname: String
        lateinit var menuprice: String
        var urlText = ipadress.urlText + "getShortData/getMenuName.jsp?menutable=" + menuTableName + "&&menuidx="+menuIdx
        Log.d("GetMenuPriceTask",urlText)
        var url = URL(urlText)
        var urlConnection = url.openConnection() as HttpURLConnection
        if(urlConnection.responseCode == HttpURLConnection.HTTP_OK){
            val streamReader = InputStreamReader(urlConnection.inputStream)
            val buffered = BufferedReader(streamReader)
            val content = StringBuilder()
            while(true){
                val line = buffered.readLine() ?: break
                content.append(line)
            }
            buffered.close()
            menuname = content.toString()
            urlConnection.disconnect()

        } else {
            menuname = "Null"
        }
        urlText = ipadress.urlText + "getShortData/getMenuPrice.jsp?menutable=" + menuTableName + "&&menuidx="+menuIdx
        Log.d("GetMenuPriceTask",urlText)
        url = URL(urlText)
        urlConnection = url.openConnection() as HttpURLConnection
        if(urlConnection.responseCode == HttpURLConnection.HTTP_OK){
            val streamReader = InputStreamReader(urlConnection.inputStream)
            val buffered = BufferedReader(streamReader)
            val content = StringBuilder()
            while(true){
                val line = buffered.readLine() ?: break
                content.append(line)
            }
            buffered.close()
            menuprice = content.toString()
            urlConnection.disconnect()

        } else {
            menuprice = "0"
        }
        return Pair(menuname,menuprice)
    }

    override fun onPostExecute(result: Pair<String, String>?) {
        var menuname = result?.first
        var menuprice = result!!.second
        targetMenuName.text = menuname
        targetMenuPrice.text = menuprice
        productRes.text = (Integer.parseInt(menuprice) * cnt).toString()
        viewholder.curPrice = Integer.parseInt(menuprice) * cnt
        CartData.totalCost += Integer.parseInt(menuprice) * cnt
        (orderActivity as OrderActivity).viewBinding.totalCost.text = CartData.totalCost.toString()
    }
}