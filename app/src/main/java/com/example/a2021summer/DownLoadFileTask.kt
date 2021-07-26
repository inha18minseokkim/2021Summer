package com.example.a2021summer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
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