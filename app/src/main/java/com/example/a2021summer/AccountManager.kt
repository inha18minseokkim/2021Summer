package com.example.a2021summer

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object AccountManager {
    var accountID: String = "A"
    fun addCartData(context: Context, shopName: String, selectedMenu: MutableList<Int>, selectedMenuCount: MutableList<Int>){
        /*스레드 상황에서 쓰세요*/
        if(accountID.equals("A")){
            (context as SubActivity).runOnUiThread{
            Toast.makeText(context,"로그인을 먼저 해주세요 제발",Toast.LENGTH_SHORT).show()
            }
            return
        }
        //메뉴랑 카운트 데이터 전처리
        var resSelectedMenu: String = ""
        var resSelectedMenuCount: String = ""
        for(i in 0..selectedMenu.size-1){
            resSelectedMenu += selectedMenu[i]
            if(i != selectedMenu.size-1)
                resSelectedMenu += ','
        }
        for(i in 0..selectedMenuCount.size-1){
            resSelectedMenuCount += selectedMenuCount[i]
            if(i != selectedMenuCount.size-1)
                resSelectedMenuCount += ','
        }
        //담아서 request
        val urlText = ipadress.urlText + "addCartData.jsp?accountID="+accountID+
                "&&shopname="+shopName+"&&menu="+resSelectedMenu+"&&menucount="+resSelectedMenuCount
        Log.d("addCart",urlText +"리퀘스트 중...")
        val url = URL(urlText)
        val urlConnection = url.openConnection() as HttpURLConnection
        if(urlConnection.responseCode == HttpURLConnection.HTTP_OK){
            (context as SubActivity).runOnUiThread{
                Toast.makeText(context,"장바구니에 담았음",Toast.LENGTH_SHORT).show();
            }
        } else {
            (context as SubActivity).runOnUiThread{
                Toast.makeText(context,"뭔가 문제가 있음",Toast.LENGTH_SHORT).show();
            }
        }

    }
}