package com.example.a2021summer
import android.util.Log
/*import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch*/
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object JSONManager {
    fun loadAllShopList(): MutableList<ShopData> {
        var res = mutableListOf<ShopData>()
        //GlobalScope.launch(Dispatchers.Main){//스레드로 시작
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
                //JSONObject 안에 list 가 jsonArray임
                var tmpfile = JSONObject(content.toString()).getJSONArray("list")
                for(i in 0 until tmpfile.length()){
                    var tmpdata = tmpfile.getJSONObject(i)//jsonObject 각각 개별요소
                    var number = tmpdata.getInt("number")
                    var name = tmpdata.getString("name")
                    var score = tmpdata.getDouble("score")
                    var menutable = tmpdata.getString("menutable")
                    var info = tmpdata.getString("info")
                    var category = tmpdata.getInt("category")
                    Log.d("loading","" + number + " " + name+ " " + score+ " " + menutable+ " " + info+ " " + category)
                    var tmpobj = ShopData(number,name,score,menutable,category,info)
                    res.add(tmpobj)
                }
            }
      //  }
        return res
    }
    fun searchShopList(key: String): MutableList<ShopData> {
        var res = mutableListOf<ShopData>()
        //GlobalScope.launch(Dispatchers.Main){//스레드로 시작
        val urlText = ipadress.urlText + "searchShoplistbyString.jsp?key="+ key
        Log.d("searchShopList",urlText)
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
            Log.d("searchShopList",content.toString())
            buffered.close()
            urlConnection.disconnect()
            //JSONObject 안에 list 가 jsonArray임
            var tmpfile = JSONObject(content.toString()).getJSONArray("list")
            for(i in 0 until tmpfile.length()){
                var tmpdata = tmpfile.getJSONObject(i)//jsonObject 각각 개별요소
                var number = tmpdata.getInt("number")
                var name = tmpdata.getString("name")
                var score = tmpdata.getDouble("score")
                var menutable = tmpdata.getString("menutable")
                var info = tmpdata.getString("info")
                var category = tmpdata.getInt("category")
                Log.d("loading","" + number + " " + name+ " " + score+ " " + menutable+ " " + info+ " " + category)
                var tmpobj = ShopData(number,name,score,menutable,category,info)
                res.add(tmpobj)
            }
        }
        //  }
        return res
    }
    fun searchShopList(key: Int): MutableList<ShopData> {
        var res = mutableListOf<ShopData>()
        //GlobalScope.launch(Dispatchers.Main){//스레드로 시작
        val urlText = ipadress.urlText + "searchShoplist.jsp?key="+ key
        Log.d("searchShopList",urlText)
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
            Log.d("searchShopList",content.toString())
            buffered.close()
            urlConnection.disconnect()
            //JSONObject 안에 list 가 jsonArray임
            var tmpfile = JSONObject(content.toString()).getJSONArray("list")
            for(i in 0 until tmpfile.length()){
                var tmpdata = tmpfile.getJSONObject(i)//jsonObject 각각 개별요소
                var number = tmpdata.getInt("number")
                var name = tmpdata.getString("name")
                var score = tmpdata.getDouble("score")
                var menutable = tmpdata.getString("menutable")
                var info = tmpdata.getString("info")
                var category = tmpdata.getInt("category")
                Log.d("loading","" + number + " " + name+ " " + score+ " " + menutable+ " " + info+ " " + category)
                var tmpobj = ShopData(number,name,score,menutable,category,info)
                res.add(tmpobj)
            }
        }
        //  }
        return res
    }
    fun loadMenuList(name: String): MutableList<MenuData>{
        val res = mutableListOf<MenuData>()
        val urlText = ipadress.urlText + "getmenulistjson.jsp?key=" + name
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
            Log.d("AAA",content.toString())
            var tmpfile = JSONObject(content.toString()).getJSONArray("list")
            for(i in 0 until tmpfile.length()){
                var tmpdata = tmpfile.getJSONObject(i)
                var idx = tmpdata.getInt("idx")
                var name = tmpdata.getString("name")
                var price = tmpdata.getInt("price")
                var tmpobj = MenuData(idx,name,price)
                Log.d("AAA","" + idx + " " + name + " "+ price)
                res.add(tmpobj)
            }
        }
        return res
    }
    fun loadCartData() {
        var urlText = ipadress.urlText + "getCartData.jsp?accountID=" + AccountManager.accountID
        val url = URL(urlText)
        Log.d("cartdata",urlText +" 로딩 ㄱㄱ")
        val urlConnection = url.openConnection() as HttpURLConnection
        if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
            val streamReader = InputStreamReader(urlConnection.inputStream)
            val buffered = BufferedReader(streamReader)
            val content = StringBuilder()
            while (true) {
                val line = buffered.readLine() ?: break
                content.append(line)
            }
            buffered.close()
            urlConnection.disconnect()
            var tmpfile = JSONObject(content.toString())
            var cartData = CartData.loadData(tmpfile)
        }
    }
    fun updateCartData(res: JSONObject){
        var urlText = ipadress.urlText + "setCartData.jsp?accountID="+AccountManager.accountID+"&&cart="+res.toString()
        val url = URL(urlText)
        Log.d("cartdata",urlText +" 로딩 ㄱㄱ")
        val urlConnection = url.openConnection() as HttpURLConnection
        if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("updateCartData","업데이트 완료")
            urlConnection.disconnect()

        }
    }
}