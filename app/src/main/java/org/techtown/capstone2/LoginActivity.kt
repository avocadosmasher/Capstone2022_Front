
package org.techtown.capstone2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.techtown.capstone2.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding

    companion object{
        const val KAKAO = "kakao"
        const val GOOGLE = "google"
        const val NAVER = "naver"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)

        val intent =  Intent(this,WebViewActivity::class.java)

        binding.googleLogin.setOnClickListener {
            intent.putExtra("choice",GOOGLE)
            startActivity(intent)
            finish()
        }

        binding.kakaoLogin.setOnClickListener {
            intent.putExtra("choice",KAKAO)
            startActivity(intent)
            finish()
        }

        binding.naverLogin.setOnClickListener {
            intent.putExtra("choice",NAVER)
            startActivity(intent)
            finish()
        }
    }

}