package com.example.a2021summer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DownLoadFileTask(imgView: ImageView) : AsyncTask<String, Void, Bitmap>() {
    var target = imgView
    override fun doInBackground(vararg p0: String?): Bitmap {
        lateinit var bmp:Bitmap
        try{
            var img_url = p0[0]
            var url = URL(img_url)
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch(e: Exception){
            Log.d("ImgDownload","이미지 다운중 문제가 생김 ㅜㅜ")
        }
        return bmp
    }

    override fun onPostExecute(result: Bitmap?) {
        target.setImageBitmap(result)
    }
}
class DownLoadShopLogo(imgView: ImageView,shopName: String) : AsyncTask<String, Void, Bitmap>(){
    var target = imgView
    var shopname = shopName
    override fun doInBackground(vararg p0: String?): Bitmap {
        lateinit var bmp:Bitmap
        try{
            var img_url = ipadress.urlText + "getImage.jsp?shopname=" + shopname
            Log.d("DownloadShopLogo",img_url)
            var url = URL(img_url)
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch(e : Exception){
            Log.d("DownloadShopLogo","뭔가 문제가 있음" + e.localizedMessage)
        }
        return bmp
    }

    override fun onPostExecute(result: Bitmap?) {
        target.setImageBitmap(result)
    }
}