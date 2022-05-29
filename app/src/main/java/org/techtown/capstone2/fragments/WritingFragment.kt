package org.techtown.capstone2.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.apollographql.apollo3.api.Optional
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_writing.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.techtown.apollo.AddPostMutation
import org.techtown.apollo.type.PostInput
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentWritingBinding
import org.techtown.capstone2.util.themeColor
import org.techtown.capstone2.viewmodel.MainViewModel
import org.techtown.download.FormDataUtil
import org.techtown.download.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URI
import kotlin.contracts.contract
import kotlin.properties.Delegates

class WritingFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var filePath:String
    private lateinit var fileName:String
    private lateinit var binding:FragmentWritingBinding
    private val postInput by lazy {
        Optional.Present(
            PostInput(
                title = binding.writingTitle.text.toString(),
                content = binding.writingContent.text.toString(),
                memberId = viewModel.getUserId(),
                boardId = 1
            )
        )
    }
    private var isFileSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWritingBinding.inflate(inflater,container,false)

        /** 음악 선택 **/
        binding.writingAudioUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply{
                type ="*/*"
            }
            startActivityForResult(intent,1)
        }

        /** 글 작성 **/
        binding.writingConfirm.setOnClickListener {
            lifecycleScope.launch {
                /** Title and content **/
                viewModel.apolloClient.mutation(AddPostMutation(postInput)).execute()
            }
            launchFileUpload()
        }

        if (viewModel.getWTMode() == viewModel.MODE_UPDATE){
            TODO("Post에 대한 정보를 DataBinding을 해줘야함.")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        writing_back_button.setOnClickListener { findNavController().popBackStack() }

        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.fab)
            endView = WritingView
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.material_motion_duration_medium_2).toLong()
            addTarget(R.id.WritingView)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            val fullAudioUri = data?.data
            fileName = getFileNameFromUri(context,fullAudioUri)
            filePath = getRealPathFromUri(context,fullAudioUri)
            binding.musicTitle = fileName

            if(!isFileSelected){
                isFileSelected = true
                binding.isFiledSelected = isFileSelected
            }

        }
    }
    fun launchFileUpload(){
        /** File Upload Part **/
        val file = File(filePath)
        val formFile = FormDataUtil.getAudioBody("audio",fileName,file)

        val call = RetrofitClient.retrofitOpenService

        call.fileUploadClient(Integer.valueOf(viewModel.getUserId()),formFile)?.enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("fileUploadClient","Success")
                findNavController().popBackStack()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("fileUploadClient","failed : " + t.message.toString())
            }
        })
    }
    fun getRealPathFromUri(ctx: Context?, uri: Uri?): String{
        var realPath:String = ""
        val filePathColumn = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor: Cursor? = uri?.let {
            ctx?.getContentResolver()?.query(
                it, filePathColumn,
                null, null, null
            )
        }

        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            realPath = cursor.getString(columnIndex)
            cursor.close()
        }
        return realPath
    }
    fun getFileNameFromUri(ctx: Context?,uri: Uri?):String{
        uri?.let {
            ctx?.contentResolver?.query(it,null,null,null,null)
        }?.use{
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            return it.getString(nameIndex)
        }?:return ""
    }
}