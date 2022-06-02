package org.techtown.capstone2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import org.techtown.capstone2.util.PreferenceManager
import org.techtown.download.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        val token = PreferenceManager.getString(applicationContext,"token")
        Log.d("LogoActivity: ","token : $token")
        val call = RetrofitClient.retrofitOpenService

        call.tokenValidityTest("Bearer $token")?.enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val code = response.code()
                Log.d("tokenValidityTest :", "code : $code")

                if(code == 200){
                    /** Token is valid => MainActivity **/
                    val intent = Intent(baseContext,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else if(code == 401){
                    /** Token is invalid => LoginActivity **/
                    val intent = Intent(baseContext,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Log.d("Diff_Code : ",code.toString())
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("tokenValidityTest","failed : " + t.message.toString())
            }
        })
    }
}