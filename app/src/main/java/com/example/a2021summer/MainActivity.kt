package com.example.a2021summer

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
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
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import java.net.URL
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
                                    AccountManager.doLogOut()
                                    Log.d("login활동","로그아웃 성공")
                                    Log.d("login활동","로그아웃 완료" + AccountManager.accountID)
                                    Toast.makeText(this@MainActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                                    SwapNavDrawerLogInOut()
                                }

                                override fun onFailure(errorResult: ErrorResult?) {
                                    super.onFailure(errorResult)
                                    Log.d("login활동","로그아웃 실패")
                                }
                                override fun onCompleteLogout() {

                                }

                            })
                        } else {//로그아웃 되어있으면 로그인
                            session.open(AuthType.KAKAO_ACCOUNT,mainActivityContext)
                            Log.d("login활동",AccountManager.accountID + "아이디로 로그인 완료")

                        }
                    }
                    com.example.a2021summer.R.id.unlink -> {
                        UserManagement.getInstance().requestUnlink(object: UnLinkResponseCallback() {
                            override fun onSuccess(result: Long?) {
                                Toast.makeText(mainActivityContext,"탈퇴 완료",Toast.LENGTH_SHORT).show()
                                AccountManager.doLogOut()
                                SwapNavDrawerLogInOut()
                            }

                            override fun onSessionClosed(errorResult: ErrorResult?) {

                            }

                        })
                    }
                }
                return true
            }

        })

        /*카카오톡 로그인 API 구현*/
        session = Session.getCurrentSession()
        session.addCallback(sessionCallback)
        SwapNavDrawerLogInOut()
    }
    fun setProfileNickname(nickname: String){
        viewBinding.navView.getHeaderView(0).findViewById<TextView>(com.example.a2021summer.R.id.accountNickname).text = nickname
    }
    fun setProfileImg(profileImg: String){
        if(profileImg.isEmpty() == false){
            var task = DownLoadFileTask(viewBinding.navView.getHeaderView(0).findViewById(com.example.a2021summer.R.id.accountImage))
            task.execute(profileImg)
        } else {
            (viewBinding.navView.getHeaderView(0).findViewById(com.example.a2021summer.R.id.accountImage) as ImageView).setImageResource(
                com.kakao.util.R.drawable.kakao_default_profile_image
            )
        }
    }
    fun SwapNavDrawerLogInOut() {
        if(AccountManager.isLogOn()){
            viewBinding.navView.menu.findItem(com.example.a2021summer.R.id.logout).title = "로그아웃"
        } else {
            viewBinding.navView.menu.findItem(com.example.a2021summer.R.id.logout).title = "로그인"
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(sessionCallback)
    }
    override fun onResume(){
        super.onResume()
        /*나갔다 들어오면 초기화되더라..*/
        setProfileImg(AccountManager.accountProfileImage)
        setProfileNickname(AccountManager.accountNickName)
        SwapNavDrawerLogInOut()

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
            com.example.a2021summer.R.id.btnsearch -> {//검색버튼 누르면 검색창 열림
                var intent = Intent(this,SearchActivity::class.java)
                intent.putExtra("key","")
                startActivity(intent)
                return true
            }
            com.example.a2021summer.R.id.btnCart -> {//카트버튼 누르면 장바구니 가는화면
                if(AccountManager.isLogOn() == false){
                    Toast.makeText(this,"로그인을 해주세요",Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, OrderActivity::class.java)
                    startActivity(intent)
                }
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