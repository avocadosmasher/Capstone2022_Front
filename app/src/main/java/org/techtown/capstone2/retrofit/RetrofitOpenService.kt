package org.techtown.download

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitOpenService {
    @GET("/audio")
    fun fileDownloadClient(
        @Header("Authorization") header:String,
        @Query("post") postId:Int
    ): Call<ResponseBody>?

    @Multipart
    @PUT("/audio")
    fun fileUploadClient(
        @Header("Authorization") header:String,
        @Query("post") postId:Int,
        @Part audioFile: MultipartBody.Part
    ): Call<ResponseBody>?

    @GET("/test")
    fun tokenValidityTest(@Header("Authorization") header:String):Call<ResponseBody>?
}