package com.example.a2021summer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonParser


class KakaoPayActivity : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    lateinit var productName: String
    lateinit var productPrice: String
    lateinit var mywebViewClient: MyWebViewClient
    lateinit var tidPin: String
    lateinit var pgToken: String
    lateinit var gson: Gson
    lateinit var webview: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_pay)
        productName = intent.getSerializableExtra("name") as String
        productPrice = intent.getSerializableExtra("price") as String
        mywebViewClient = MyWebViewClient()
        requestQueue = Volley.newRequestQueue(applicationContext)
        gson = Gson()
        webview = findViewById<WebView>(R.id.paywebview)
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = mywebViewClient
        requestQueue.add(mywebViewClient.readyRequest)
    }
    inner class MyWebViewClient : WebViewClient() {
        // 에러 - 통신을 받을 Response 변수
        var errorListener =
            Response.ErrorListener { error -> Log.e("KakaoPay", "Error : $error") }

        // 결제 준비 단계 - 통신을 받을 Response 변수
        var readyResponse: Response.Listener<String> =
            Response.Listener { response ->
                Log.e("KakaoPay", response)
                // 결제가 성공 했다면 돌려받는 JSON객체를 파싱함.
                val parser = JsonParser()
                val element = parser.parse(response)

                // get("받을 Key")로 Json 데이터를 받음
                // - 결제 요청에 필요한 next_redirect_mobile_url, tid를 파싱
                val url = element.asJsonObject["next_redirect_mobile_url"].asString
                val tid = element.asJsonObject["tid"].asString
                Log.e("KakaoPay", "url : $url")
                Log.e("KakaoPay", "tid : $tid")
                webview.loadUrl(url)
                tidPin = tid
            }

        // 결제 준비 단계 - 통신을 넘겨줄 Request 변수
        var readyRequest: StringRequest = object : StringRequest(
            Request.Method.POST,
            "https://kapi.kakao.com/v1/payment/ready",
            readyResponse,
            errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                Log.e("KakaoPay", "name : $productName")
                Log.e("KakaoPay", "price : $productPrice")
                val params: MutableMap<String, String> = HashMap()
                params["cid"] = "TC0ONETIME" // 가맹점 코드
                params["partner_order_id"] = "1001" // 가맹점 주문 번호
                params["partner_user_id"] = "gorany" // 가맹점 회원 아이디
                params["item_name"] = productName // 상품 이름
                params["quantity"] = "1" // 상품 수량
                params["total_amount"] = productPrice // 상품 총액
                params["tax_free_amount"] = "0" // 상품 비과세
                params["approval_url"] = "https://www.naver.com/success" // 결제 성공시 돌려 받을 url 주소
                params["cancel_url"] = "https://www.naver.com/cancel" // 결제 취소시 돌려 받을 url 주소
                params["fail_url"] = "https://www.naver.com/fali" // 결제 실패시 돌려 받을 url 주소
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = "KakaoAK " + "cd4970359ae8acc28ac4ff12ab04b5c9"
                return headers
            }
        }
        var approvalResponse: Response.Listener<String> =
            Response.Listener { response -> Log.e("KakaoPay", response) }
        var approvalRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://kapi.kakao.com/v1/payment/approve",
            approvalResponse,
            errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["cid"] = "TC0ONETIME"
                params["tid"] = tidPin
                params["partner_order_id"] = "1001"
                params["partner_user_id"] = "gorany"
                params["pg_token"] = pgToken
                params["total_amount"] = productPrice
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = "KakaoAK " + "cd4970359ae8acc28ac4ff12ab04b5c9"
                return headers
            }
        }

        // URL 변경시 발생 이벤트
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            Log.e("KakaoPay", "url$url")
            if (url != null && url.contains("pg_token=")) {
                val pg_Token = url.substring(url.indexOf("pg_token=") + 9)
                Log.d("KakaoPay",pg_Token)
                pgToken = pg_Token
                requestQueue.add(approvalRequest)
            } else if (url != null && url.startsWith("intent://")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    val existPackage =
                        packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                    if (existPackage != null) {
                        startActivity(intent)
                    }
                    return true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            view.loadUrl(url)
            return false
        }
    }
}
