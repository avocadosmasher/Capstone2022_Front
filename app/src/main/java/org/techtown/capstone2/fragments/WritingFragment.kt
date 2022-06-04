package org.techtown.capstone2.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.apollographql.apollo3.api.Optional
import com.google.android.material.transition.MaterialContainerTransform
import com.test.progressbartest.ProgressDialog
import kotlinx.android.synthetic.main.fragment_writing.*
import okhttp3.ResponseBody
import org.techtown.apollo.AddPostMutation
import org.techtown.apollo.GetUpdatePostQuery
import org.techtown.apollo.UpdatePostMutation
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


class WritingFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var fileUri:Uri
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
    private val args: WritingFragmentArgs by navArgs()
    private val progress by lazy {
        activity?.let { ProgressDialog(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWritingBinding.inflate(inflater,container,false)

        binding.isFiledSelected = isFileSelected

        if(args.updatePostId != -1){
            args.let {
                lifecycleScope.launchWhenResumed {
                    val response = viewModel.apolloClient.query(GetUpdatePostQuery(it.updatePostId.toString())).execute()
                    response.data?.getPost?.apply {
                        binding.apply {
                            writingTitle.setText(title)
                            writingContent.setText(content)
                            writingAudioTitle.text = audio
                        }
                    }
                }
            }
        }

        /** 음악 선택 **/
        binding.writingAudioUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply{
                type ="audio/*"
            }
            startActivityForResult(intent,1)
        }

        /** 글 작성 **/
        binding.writingConfirm.setOnClickListener {

            progress?.start("...글 작성중...")

            if(!isFileSelected && args.updatePostId != -1){
                // 업데이트(음악파일 변경 x)
                lifecycleScope.launchWhenResumed {
                    /** Title and content **/
                    viewModel.apolloClient.mutation(UpdatePostMutation(args.updatePostId.toString(),postInput)).execute()
                    progress?.dismiss()
                    findNavController().popBackStack()
                }

            }else if(isFileSelected && args.updatePostId != -1){
                // 업데이트(음악파일 변경 o)
                lifecycleScope.launchWhenResumed {
                    /** Title and content **/
                    val response = viewModel.apolloClient.mutation(UpdatePostMutation(args.updatePostId.toString(),postInput)).execute()
                    launchFileUpload(response.data?.updatePost?.id)
                }
            }else if(isFileSelected && args.updatePostId == -1){
                // 새로 글 쓰기
                lifecycleScope.launchWhenResumed {
                    /** Title and content **/
                    val response = viewModel.apolloClient.mutation(AddPostMutation(postInput)).execute()
                    launchFileUpload(response.data?.addPost?.id)
                }
            }else{
                // 새로 글 쓰기인데 음악 추가 안함.(오류임)
                progress?.dismiss()
                Toast.makeText(context,"음악 파일을 선택해 주세요.",Toast.LENGTH_SHORT).show()
            }
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
            Log.d("fullAudioUri",data?.data.toString())
            fileName = getFileNameFromUri(context,fullAudioUri)
            data?.data?.let {
                fileUri = it
            }
            binding.musicTitle = fileName

            if(!isFileSelected){
                isFileSelected = true
                binding.isFiledSelected = isFileSelected
            }

        }
    }
    fun launchFileUpload(postId:String?){
        /** File Upload Part **/
        val file = getFileFromUri(requireContext().contentResolver, fileUri, requireContext().cacheDir)
        val formFile = FormDataUtil.getAudioBody("audio",fileName,file)

        val call = RetrofitClient.retrofitOpenService

        call.fileUploadClient(viewModel.getToken(),Integer.valueOf(postId),formFile)?.enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("fileUploadClient","Success")
                Log.d("fileUploadClient",response.code().toString())
                progress?.dismiss()
                findNavController().popBackStack()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progress?.dismiss()
                Log.d("fileUploadClient","failed : " + t.message.toString())
            }
        })
    }

    private fun getFileFromUri(contentResolver: ContentResolver, uri: Uri, directory: File): File {
        val file =
            File.createTempFile("suffix", "prefix", directory)
        file.outputStream().use {
            contentResolver.openInputStream(uri)?.copyTo(it)
        }

        return file
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
    /**
    fun getRealPathFromUri(ctx: Context?, uri: Uri?): String{
    var realPath:String = ""
    val filePathColumn = arrayOf(MediaStore.Files.FileColumns.DATA)
    val cursor: Cursor? = uri?.let {
    ctx?.contentResolver?.query(
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
     **/
}