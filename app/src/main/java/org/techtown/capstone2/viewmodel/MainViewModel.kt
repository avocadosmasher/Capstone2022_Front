package org.techtown.capstone2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.polyak.iconswitch.IconSwitch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.techtown.capstone2.fragments.feeds.IconSwitchListener
import org.techtown.capstone2.util.PreferenceManager
import kotlin.properties.Delegates

class MainViewModel : ViewModel() {
    private lateinit var token:String
    val serverUrl = "http://133.186.247.141:5000/graphql"
    val okHttpClient : OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(token))
            .build()
    }
    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(serverUrl)
            .okHttpClient(okHttpClient)
            .build()
    }
    private var userID by Delegates.notNull<Int>()
    lateinit var iconSwitchListener:IconSwitchListener
    var checkedLeft: Boolean = true

    fun setUserId(id:Int){
        userID = id
    }
    fun getUserId() = userID

    fun setToken(token:String){
        this.token = token
    }
    fun getToken() = this.token

    private class AuthorizationInterceptor(val token:String): Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            return chain.proceed(request)
        }
    }
}