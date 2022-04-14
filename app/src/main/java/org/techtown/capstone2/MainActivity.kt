package org.techtown.capstone2

import android.app.ActivityOptions
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.android.synthetic.main.activity_main.*
import org.techtown.capstone2.fragments.AllFeedFragmentDirections
import java.lang.Exception

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener{

    lateinit var navController: NavController

    val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = nav_host_fragment_container.findNavController()
        setUpFab()
    }

    private fun setUpFab(){
        findNavController(R.id.nav_host_fragment_container).addOnDestinationChangedListener(this@MainActivity)

        fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)

            setOnClickListener {
                navigateTo(NavDir.WRITING)
            }
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        when(destination.id){
            R.id.allFeedFragment->{
                setFabForFeed()
            }
            R.id.writingFragment->{
                setFabForWriting()
            }
        }
    }
    private fun setFabForFeed(){
        fab.show()
        icon_switch.visibility = View.VISIBLE
    }
    private fun setFabForWriting(){
        fab.visibility = View.INVISIBLE
        icon_switch.visibility = View.GONE
    }

    private fun navigateTo(dir:NavDir){

        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
        }

        val directions = when(dir){
            NavDir.FOLLOWING_FEED ->{
                AllFeedFragmentDirections.actionAllFeedFragmentToFollowingFeedFragment()
            }
            NavDir.DETAILED_POST->{
                AllFeedFragmentDirections.actionAllFeedFragmentToDetailedPostFragment()
            }
            NavDir.WRITING->{
                AllFeedFragmentDirections.actionAllFeedFragmentToWritingFragment()
            }
            NavDir.SETTING->{
                AllFeedFragmentDirections.actionAllFeedFragmentToSettingFragment()
            }
            else -> null
        }
        if(directions != null){
            navController.navigate(directions)
        }
    }
}