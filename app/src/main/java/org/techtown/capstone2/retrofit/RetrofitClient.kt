package org.techtown.capstone2.retrofit

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object{
        private val baseUrl = ""
        private lateinit var instance: RetrofitClient
        private lateinit var initMyApi: initMyApi
        private lateinit var appContext: Context

        fun getInstance(context:Context):RetrofitClient{
            appContext = context

            instance?.apply {
                instance = RetrofitClient()
            }
            return instance
        }
        fun initMyApi():initMyApi{
            return initMyApi
        }
        fun OkHttpCLientBuilding(interceptor:HttpLoggingInterceptor):OkHttpClient{
            var client = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()
            return client
        }
    }

    private constructor(){
        val interceptor = HttpLoggingInterceptor().apply{
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        lateinit var client:OkHttpClient

        appContext?.apply{
            val token:String? = null
            token?.apply {
                client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(1,TimeUnit.MINUTES)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .writeTimeout(15,TimeUnit.SECONDS)
                    .addInterceptor(object:Interceptor{
                        override fun intercept(chain: Interceptor.Chain): Response {
                            var newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer $token")
                                .build()

                            return chain.proceed(newRequest)
                        }
                    })
                    .build()
            }?:{
                client = OkHttpCLientBuilding(interceptor)
            }
        }?:{
            client = OkHttpCLientBuilding(interceptor)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        initMyApi = retrofit.create(initMyApi.javaClass)
    }


}