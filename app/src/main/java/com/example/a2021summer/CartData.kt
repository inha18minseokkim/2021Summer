package com.example.a2021summer

import android.util.Log
import android.widget.Toast
import com.example.a2021summer.ipadress.urlText
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.properties.Delegates


/*json 받아오고 출력하는 예시*/
/*
{"list":
[
{"hosigi":"1,2,3"},
{"sawolnoodle":"1,2"}
],
"count":
[
{"hosigi":"3,4,5"},
{"sawolnoodle":"4,5"}
]
}*/

/*json 받아오고 출력하는 예시*/ /*
{"list":
[
{"hosigi":"1,2,3"},
{"sawolnoodle":"1,2"}
],
"count":
[
{"hosigi":"3,4,5"},
{"sawolnoodle":"4,5"}
]
}*/
interface Item
data class menuTitle(var shopname: String): Item
class menuElement(var shopname: String, var shopMenu: String, var menuCount: String): Item
object CartData {
    lateinit var shopName: ArrayList<String>
    lateinit var shopMenu: ArrayList<ArrayList<String>>
    lateinit var menuCount: ArrayList<ArrayList<String>>
    lateinit var dataforadapter : ArrayList<Item>
    var totalCost: Int by Delegates.observable(0){
        props,old,new ->
        thread(start=true){
            val urlText = ipadress.urlText + "updateCurCost.jsp?accountID="+ AccountManager.accountID + "&&cost=" + new
            Log.d("addCart",urlText +"리퀘스트 중...")
            val url = URL(urlText)
            val urlConnection = url.openConnection() as HttpURLConnection
            if(urlConnection.responseCode == HttpURLConnection.HTTP_OK){
                Log.d("totalCost","가격 갱신완료")
            } else {
                Log.d("totalCost","가격 갱신실패")
            }
        }

    }
    fun loadData(raw: JSONObject) {
        shopName = ArrayList()
        shopMenu = ArrayList()
        menuCount = ArrayList()

        val raws = raw.getJSONArray("list")
        println(raws.toString())
        for (i in 0 until raws.length()) {
            val tmp = raws.getJSONObject(i)
            val tmpkey: Iterator<*> = tmp.keys()
            val shopname = tmpkey.next() as String //hosigi 추출함
            println("키 값$shopname")
            shopName.add(shopname)
            val arrayList = ArrayList<String>()
            println("value " + tmp[shopname])
            arrayList.addAll(Arrays.asList(*tmp.getString(shopname).split(",").toTypedArray()))
            println(arrayList.toString())
            shopMenu.add(arrayList)
        }
        val counts = raw.getJSONArray("count")
        for (i in 0 until counts.length()) {
            val tmp = counts.getJSONObject(i)
            val tmpkey: Iterator<*> = tmp.keys()
            val shopname = tmpkey.next() as String //hosigi 추출함
            val arrayList = ArrayList<String>()
            arrayList.addAll(Arrays.asList(*tmp.getString(shopname).split(",").toTypedArray()))
            menuCount.add(arrayList)
        }

    }

    fun init() {
        shopName = ArrayList()
        shopMenu = ArrayList()
        menuCount = ArrayList()
    }

    fun printLog() {
        println("ShopName")
        for (element in shopName) {
            println(element)
        }
        println("shopMenu")
        for (element in shopMenu) {
            for (i in element.indices) {
                print(element[i] + " ")
            }
            println("")
        }
        println("menuCount")
        for (element in menuCount) {
            for (i in element.indices) {
                print(element[i] + " ")
            }
            println("")
        }
    }

    fun convertToJSON(): JSONObject {
        val res = JSONObject()
        val tmparray = JSONArray()
        for (i in shopName.indices) {
            val key = shopName[i]
            val value = shopMenu[i]
            val resvalue = StringBuilder()
            for (j in value.indices) {
                resvalue.append(value[j])
                if (j != value.size - 1) resvalue.append(",")
            }
            val tmpobj = JSONObject()
            tmpobj.put(key, resvalue.toString())
            tmparray.put(tmpobj)
        }
        res.put("list", tmparray)
        val tmpcountarray = JSONArray()
        for (i in shopName.indices) {
            val key = shopName[i]
            val value = menuCount[i]
            val resvalue = StringBuilder()
            for (j in value.indices) {
                resvalue.append(value[j])
                if (j != value.size - 1) resvalue.append(",")
            }
            val tmpobj = JSONObject()
            tmpobj.put(key, resvalue.toString())
            tmpcountarray.put(tmpobj)
        }
        res.put("count", tmpcountarray)
        return res
    }

    fun addCartData(shopname: String, menulist: String, menucount: String) { //카트에 데이터 추가함
        var targetidx = -1
        for (i in shopName.indices) {
            if (shopName[i] == shopname) {
                targetidx = i
                break
            }
        } //해당 인덱스 찾는다
        if (targetidx == -1) { //만약에 가게가 없으면 shopName에 추가하고 shopMenu에 새로 리스트를 만들어 추가한다.
            shopName.add(shopname)
            val tmpmenulist = ArrayList<String>()
            tmpmenulist.addAll(Arrays.asList(*menulist.split(",").toTypedArray()))
            shopMenu.add(tmpmenulist)
            val tmpmenucount = ArrayList<String>()
            tmpmenucount.addAll(Arrays.asList(*menucount.split(",").toTypedArray()))
            menuCount.add(tmpmenucount)
        } else { //가게가 있으면 shopMenu를 수정한다.
            val tmpmenulist = ArrayList<String>()
            tmpmenulist.addAll(Arrays.asList(*menulist.split(",").toTypedArray()))
            shopMenu[targetidx] = tmpmenulist
            val tmpmenucount = ArrayList<String>()
            tmpmenucount.addAll(Arrays.asList(*menucount.split(",").toTypedArray()))
            menuCount[targetidx] = tmpmenucount
        }
    }
    fun getDataForOrderAdapter() : ArrayList<Item> {
        dataforadapter = ArrayList<Item>()
        for(i in 0..shopName.size-1){
            Log.d("getDataForOrderAdapter",shopName[i])
            dataforadapter.add(menuTitle(shopName[i]))
            for (j in 0..shopMenu[i].size-1){
                dataforadapter.add(menuElement(shopName[i],shopMenu[i][j],menuCount[i][j]))
                Log.d("getDataForOrderAdapter",shopMenu[i][j] +" "+ menuCount[i][j])
            }
        }
        Log.d("getDataForOrderAdapter",dataforadapter.size.toString() + "생성완료")
        return dataforadapter
    }
    fun modifyCount(targetShop: String, targetidx: Int, direct: Int) {
        var shoptarget: Int = shopName.indexOf(targetShop)
        var menutarget = shopMenu[shoptarget].indexOf(targetidx.toString())
        if(direct == 1){//btnplus
            menuCount[shoptarget][menutarget] = (Integer.parseInt(menuCount[shoptarget][menutarget])+1).toString()
        } else {//btnminus
            menuCount[shoptarget][menutarget] = (Integer.parseInt(menuCount[shoptarget][menutarget])-1).toString()
        }
        printLog()
    }
}