package com.example.a2021summer
import android.util.Log
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class JSONManager {
    public fun loadAllShopList(): MutableList<ShopData> {
        var res = mutableListOf<ShopData>()
        thread(start=true){//스레드로 시작
            val urlText = ipadress.urlText
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
        }
        return res
    }
}