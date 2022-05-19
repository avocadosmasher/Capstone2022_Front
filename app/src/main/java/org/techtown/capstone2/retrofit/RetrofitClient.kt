package org.techtown.download

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.techtown.capstone2.retrofit.RetrofitApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitClient {
    val interceptor = HttpLoggingInterceptor().apply{
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val logHelperClient: OkHttpClient.Builder by lazy{
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
    }
    private val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(RetrofitApi.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(logHelperClient.build())
    }
    val retrofitOpenService:RetrofitOpenService by lazy {
        retrofitClient.build().create(RetrofitOpenService::class.java)
    }
}

/** WAY TO USE **/