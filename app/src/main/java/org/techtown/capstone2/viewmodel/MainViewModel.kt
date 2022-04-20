package org.techtown.capstone2.viewmodel

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient

class MainViewModel : ViewModel() {
    val serverUrl = "http://133.186.247.141:5000/graphql"
    val okHttpClient : OkHttpClient
    val apolloClient: ApolloClient

    init {
        okHttpClient = OkHttpClient.Builder().build()
        apolloClient = ApolloClient.Builder()
            .serverUrl(serverUrl)
            .okHttpClient(okHttpClient)
            .build()
    }
}