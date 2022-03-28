package org.techtown.capstone2

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MediaPlayerFragment: Fragment() {

    private var mp: MediaPlayer? = null
    private var url: String? = null

    companion object{
        fun newInstance(url:String?) :MediaPlayerFragment{
            val fragment = MediaPlayerFragment()
            val bundle = Bundle()

            bundle.putString("url",url)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments?.getString("url")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.music_player,container,false)

        val toggle = rootView.findViewById<ToggleButton>(R.id.toggleButton)
        val seekBar = rootView.findViewById<SeekBar>(R.id.seekBar)

        toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked){
                true -> {
                    if(mp == null){
                        mp = MediaPlayer()
                        mp?.apply{
                            setDataSource(url)
                            prepare()
                        }
                        initializeSeekBar(seekBar)
                    }
                    mp?.start()
                }
                false -> {
                    mp?.pause()
                }
            }
        }

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) mp?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        return rootView
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
}