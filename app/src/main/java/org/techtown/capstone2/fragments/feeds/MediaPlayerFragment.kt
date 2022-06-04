package org.techtown.capstone2.fragments.feeds

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.test.progressbartest.ProgressDialog
import kotlinx.android.synthetic.main.music_player.*
import okhttp3.ResponseBody
import okio.utf8Size
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.MusicPlayerBinding
import org.techtown.capstone2.viewmodel.MainViewModel
import org.techtown.download.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import java.io.*
import java.lang.Exception

class MediaPlayerFragment: Fragment() {

    private var address = "http://133.186.247.141:5000/audio?post="
    private var mp: MediaPlayer? = null
    private var title: String? = null
    private var postId:String? = null
    private lateinit var binding:MusicPlayerBinding
    private val viewModel:MainViewModel by activityViewModels()
    private val progress by lazy {
        activity?.let { ProgressDialog(it) }
    }

    companion object{
        fun newInstance(title:String?,postId:String?) : MediaPlayerFragment {
            val fragment = MediaPlayerFragment()
            val bundle = Bundle()

            bundle.putString("title",title)
            bundle.putString("postId",postId)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("title")
        postId = arguments?.getString("postId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = MusicPlayerBinding.inflate(layoutInflater,container,false)

        binding.musicTitle.text = title

        binding.audioDownloadButton.setOnClickListener {
            val call = RetrofitClient.retrofitOpenService

            progress?.start("...음악 다운로드중...")

            call.fileDownloadClient(viewModel.getToken(),postId?.toInt()?:-1)?.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                    val success = writeResponseBodyToDisk(response.body())
                    if(success) Log.d("FileDownLoad : ", " Success ")
                    else Log.d("FileDownLoad : ", "Failed")
                    progress?.dismiss()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("FileDownLoad : ", "Failed(Reason : ${t.message})")
                    progress?.dismiss()
                }
            })
        }

        binding.toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("music_src",address+postId)

            when(isChecked){
                true -> {
                    if(mp == null){
                        mp = MediaPlayer()
                        mp?.apply{
                            val headers = HashMap<String,String>()
                            headers.put("Authorization","Bearer ${viewModel.getToken()}")
                            val uri = Uri.parse(address+postId)
                            activity?.applicationContext?.let { setDataSource(it,uri,headers) }
                            prepare()
                        }
                        initializeSeekBar(binding.seekBar)
                    }
                    mp?.start()
                }
                false -> {
                    mp?.pause()
                }
            }
            findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
                if(destination.id != R.id.detailedPostFragment){
                    mp?.stop()
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) mp?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        return binding.root
    }

    fun initializeSeekBar(seekBar: SeekBar){
        seekBar.max = mp!!.duration

        val handler = Handler()
        handler.postDelayed(object:Runnable{
            override fun run() {
                try{
                    seekBar.progress = mp!!.currentPosition
                    handler.postDelayed(this,1000)
                }catch (e: Exception){
                    seekBar.progress = 0
                }
            }
        },0)
    }
    private fun writeResponseBodyToDisk(body: ResponseBody?): Boolean {

        Log.d("FilePath",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator.toString() + title)

        return try {
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator.toString() + title
            val downloadFile = File(filePath)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                FileOutputStream(downloadFile)
                val fileReader = ByteArray(4096)
                val fileSize: Long = body?.contentLength()?:0
                var fileSizeDownloaded: Long = 0
                inputStream = body?.byteStream()
                outputStream = FileOutputStream(downloadFile)
                while (true) {
                    val read: Int = inputStream?.read(fileReader)?:-1
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("FileDownload", "file download: $fileSizeDownloaded of $fileSize")
                }

                outputStream.flush()
                true

            } catch (e: IOException) {
                Log.d("FileDownloadError",e.message.toString())
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            false
        }
    }
}