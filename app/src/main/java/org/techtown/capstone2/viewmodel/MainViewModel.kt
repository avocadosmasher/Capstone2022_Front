package org.techtown.capstone2.viewmodel

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.polyak.iconswitch.IconSwitch
import okhttp3.OkHttpClient
import org.techtown.capstone2.fragments.feeds.IconSwitchListener

class MainViewModel : ViewModel() {
    val serverUrl = "http://133.186.247.141:5000/graphql"
    val okHttpClient : OkHttpClient
    val apolloClient: ApolloClient
    lateinit var iconSwitchListener:IconSwitchListener
    var checkedLeft: Boolean = true

    init {
        okHttpClient = OkHttpClient.Builder().build()
        apolloClient = ApolloClient.Builder()
            .serverUrl(serverUrl)
            .okHttpClient(okHttpClient)
            .build()
    }
}