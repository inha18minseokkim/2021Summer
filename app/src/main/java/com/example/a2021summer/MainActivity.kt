package com.example.a2021summer

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2021summer.databinding.ActivityMainBinding
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlin.concurrent.thread


object ipadress{
    @JvmField val urlText = "http://192.168.1.101:14766/byeongseong/".toString()
}
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    lateinit var session: Session
    private var sessionCallback = SessionCallback(this)
    var accountID: String ="A"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)
        /*버튼 및 리스트 초기화 부분*/

        viewBinding.btnOrder.setOnClickListener {
            if(accountID.equals("A")){
                Toast.makeText(this,"로그인을 해주세요",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("accountID",accountID)
                startActivity(intent)

            }

        }
        viewBinding.btnChicken.setOnClickListener{
            var intent = Intent(this,SearchActivity::class.java)
            intent.putExtra("key",1)
            startActivity(intent)
        }
        viewBinding.btnNoodle.setOnClickListener{
            var intent = Intent(this,SearchActivity::class.java)
            intent.putExtra("key",2)
            startActivity(intent)
        }
        viewBinding.btnPizza.setOnClickListener{
            var intent = Intent(this,SearchActivity::class.java)
            intent.putExtra("key",3)
            startActivity(intent)
        }
        viewBinding.btnJokBal.setOnClickListener{
            var intent = Intent(this,SearchActivity::class.java)
            intent.putExtra("key",4)
            startActivity(intent)
        }


        /*메인화면 레이아웃 배치*/
        var jsonManager = JSONManager()
        var mainContext = this
        thread(start=true){
            var shoplist = jsonManager.loadAllShopList()
            var shopadapter = ShopListAdapter(mainContext,shoplist)
            Log.d("recycleView",shoplist.size.toString())
            runOnUiThread{
                viewBinding.mainshoplist.adapter = shopadapter
                var layout = LinearLayoutManager(mainContext)
                viewBinding.mainshoplist.layoutManager = layout
                viewBinding.mainshoplist.setHasFixedSize(true)
                shopadapter.notifyDataSetChanged()
            }
        }
        viewBinding.btnSearch.setOnClickListener{
            var keyword = viewBinding.searchContent.text.toString()
            if(keyword.isEmpty()){
                var builder = AlertDialog.Builder(this)
                builder.setMessage("내용을 입력해 주세요.")
                builder.setPositiveButton("확인",{ dialogInterface: DialogInterface, i: Int -> })
                builder.show()
            } else {
                var intent = Intent(this,SearchActivity::class.java)
                intent.putExtra("key",keyword)
                startActivity(intent)
            }
        }

        /*카카오톡 로그인 API 구현*/
        session = Session.getCurrentSession()
        session.addCallback(sessionCallback)
        viewBinding.login.setOnClickListener{
            session.open(AuthType.KAKAO_LOGIN_ALL,this)
            Log.d("login활동",accountID + "떴냐? 이걸로 리퀘스트 ㄱㄱ")
            AccountManager.accountID = accountID
        }
        viewBinding.logout.setOnClickListener{
            Log.d("login활동","로그아웃 시도...")
            UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                override fun onSuccess(result: Long?) {
                    super.onSuccess(result)
                    Log.d("login활동","로그아웃 성공")
                    accountID = "A"
                    AccountManager.accountID = "A"
                    Log.d("login활동","로그아웃 완료" + accountID)
                    Toast.makeText(this@MainActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(errorResult: ErrorResult?) {
                    super.onFailure(errorResult)
                    Log.d("login활동","로그아웃 실패")
                }
                override fun onCompleteLogout() {
                    accountID = "A"
                    Log.d("login활동","로그아웃 완료" + accountID)
                }

            })
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){
            return
        }
        Log.d("kakaoLogin",data.toString())
        super.onActivityResult(requestCode, resultCode, data)
    }
    /*private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
        }
    }*/
}