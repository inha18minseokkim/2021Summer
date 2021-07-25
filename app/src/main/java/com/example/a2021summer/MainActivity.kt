package com.example.a2021summer

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2021summer.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import kotlin.concurrent.thread


object ipadress{
    @JvmField val urlText = "http://192.168.1.101:14766/byeongseong/".toString()
}
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    lateinit var session: Session
    private var sessionCallback = SessionCallback(this)
    var mainActivityContext = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)
        AccountManager.mainActivityContext = this
        /*버튼 및 리스트 초기화 부분*/
        viewBinding.btnOrder.setOnClickListener {
            if(AccountManager.accountID.equals("A")){
                Toast.makeText(this,"로그인을 해주세요",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("accountID",AccountManager.accountID)
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
        //var jsonManager = JSONManager()
        var mainContext = this
        thread(start=true){
            var shoplist = JSONManager.loadAllShopList()
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
            var intent = Intent(this,SearchActivity::class.java)
            intent.putExtra("key","")
            startActivity(intent)
        }
        /*툴 바 메뉴 설정 및 네비게이션 바 메뉴 설정*/
        setSupportActionBar(viewBinding.maintoolbar)
        viewBinding.navView.setNavigationItemSelectedListener(object: NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                item.setChecked(true)
                viewBinding.drawerLayout.closeDrawers()

                var id = item.itemId
                when(id){
                    com.example.a2021summer.R.id.account -> {

                    }
                    com.example.a2021summer.R.id.setting -> {

                    }
                    com.example.a2021summer.R.id.logout -> {
                        if(AccountManager.isLogOn()){ //로그인 되어있으면 로그아웃
                            UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                                override fun onSuccess(result: Long?) {
                                    super.onSuccess(result)
                                    Log.d("login활동","로그아웃 성공")
                                    AccountManager.accountID = "A"
                                    Log.d("login활동","로그아웃 완료" + AccountManager.accountID)
                                    Toast.makeText(this@MainActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                                    var target = viewBinding.navView.menu.findItem(com.example.a2021summer.R.id.logout)
                                    target.title = "로그인"
                                }

                                override fun onFailure(errorResult: ErrorResult?) {
                                    super.onFailure(errorResult)
                                    Log.d("login활동","로그아웃 실패")
                                }
                                override fun onCompleteLogout() {

                                }

                            })
                        } else {//로그아웃 되어있으면 로그인
                            session.open(AuthType.KAKAO_LOGIN_ALL,mainActivityContext)
                            Log.d("login활동",AccountManager.accountID + "떴냐? 이걸로 리퀘스트 ㄱㄱ")
                            var target = viewBinding.navView.menu.findItem(com.example.a2021summer.R.id.logout)
                            target.title = "로그아웃"
                        }
                    }
                }
                return true
            }

        })
        if(AccountManager.isLogOn()){//로그온 되어있으면 로그아웃버튼 텍스트를 로그아웃으로 만듬.
            //viewBinding.logout.text = "로그아웃"
            var target = viewBinding.navView.menu.findItem(com.example.a2021summer.R.id.logout)
            target.title = "로그아웃"
        } else {
            //viewBinding.logout.text = "로그인"
            var target = viewBinding.navView.menu.findItem(com.example.a2021summer.R.id.logout)
            target.title = "로그인"
        }

        /*카카오톡 로그인 API 구현*/
        session = Session.getCurrentSession()
        session.addCallback(sessionCallback)


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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater = getMenuInflater()
        menuInflater.inflate(com.example.a2021summer.R.menu.menu_toolbar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {//메뉴 우상단 위의 버튼 누르면 내비게이션 드로어 열림
            com.example.a2021summer.R.id.devinfo -> {
                //Toast.makeText(this,"일단여기까진했음 근데 뭐지",Toast.LENGTH_SHORT).show()
                viewBinding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return true
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