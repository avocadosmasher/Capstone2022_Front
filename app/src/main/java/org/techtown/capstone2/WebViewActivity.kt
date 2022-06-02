package org.techtown.capstone2

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.techtown.capstone2.databinding.ActivityWebViewBinding
import org.techtown.capstone2.util.PreferenceManager


class WebViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWebViewBinding

    private val KAKAO_URL = "http://133.186.247.141:5000/oauth2/authorization/kakao"
    private val NAVER_URL = "http://133.186.247.141:5000/oauth2/authorization/naver"
    private val GOOGLE_URL = "http://133.186.247.141:5000/oauth2/authorization/google"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val choice = intent.getStringExtra("choice")

        val cookieManager = CookieManager.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies { aBoolean ->
                // a callback which is executed when the cookies have been removed
                Log.d("CookieManager : ", "Cookie removed: $aBoolean")
            }
        } else cookieManager.removeAllCookie()

        binding = DataBindingUtil.setContentView(this,R.layout.activity_web_view)
        binding.mWebView.apply {
            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val uri = Uri.parse(request.url.toString())
                    val token = uri.getQueryParameter("token")
                    val id = uri.getQueryParameter("id")

                    if (uri.path!!.contains("/auth/token")) {
                        PreferenceManager.setString(applicationContext,"token",token.toString())
                        PreferenceManager.setString(applicationContext,"id",id.toString())
                        view.destroy()
                        val intent = Intent(baseContext,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    return false
                }
            }

            settings.javaScriptEnabled = true

            loadUrl(when(choice){
                LoginActivity.KAKAO -> KAKAO_URL
                LoginActivity.NAVER -> NAVER_URL
                LoginActivity.GOOGLE -> GOOGLE_URL
                else -> KAKAO_URL
            })
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.mWebView.canGoBack()) {
            binding.mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}