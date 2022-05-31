package org.techtown.download

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitOpenService {
    @GET("/audio")
    fun fileDownloadClient(
        @Query("post") postId:Int
    ): Call<ResponseBody>?

    @Multipart
    @PUT("/audio")
    fun fileUploadClient(
        @Query("post") postId:Int,
        @Part audioFile: MultipartBody.Part
    ): Call<ResponseBody>?
}