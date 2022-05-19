package org.techtown.download

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object FormDataUtil {

    fun getAudioBody(key: String,fileName:String,file:File)= MultipartBody.Part.createFormData(
        key,fileName,file.asRequestBody("audio/*".toMediaType())
    )
}