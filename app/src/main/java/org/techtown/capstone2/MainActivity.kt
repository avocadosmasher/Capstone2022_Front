package org.techtown.capstone2

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.polyak.iconswitch.IconSwitch.Checked
import kotlinx.android.synthetic.main.activity_main.*
import org.techtown.capstone2.databinding.ActivityMainBinding
import org.techtown.capstone2.fragments.feeds.AllFeedFragmentDirections
import org.techtown.capstone2.util.PreferenceManager
import org.techtown.capstone2.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener{

    private lateinit var viewModel: MainViewModel
    private lateinit var binding:ActivityMainBinding
    lateinit var navController: NavController

    companion object{
        val MY_PERMISSION_REQUEST = 100;
    }

    val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST)
        }

        /** navigation animation Setting **/
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        navController = nav_host_fragment_container.findNavController()
        setUpFab()

        /** viewModel & JWT Token Config **/
        viewModel = ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        PreferenceManager.getString(applicationContext,"token")?.let {
            viewModel.setToken(it)
        }?: {
            // Token is null
            Toast.makeText(this,"JWT Token prob",Toast.LENGTH_SHORT).show()
        }
        PreferenceManager.getString(applicationContext,"id")?.let { viewModel.setUserId(it.toInt()) }


        viewModel.setUserId(2)

        icon_switch.setCheckedChangeListener {
            var tf = if(it == Checked.LEFT) true else false
            viewModel.iconSwitchListener?.onIconSwitchChanged(tf)
        }

        setting_button.setOnClickListener {
            val directions = AllFeedFragmentDirections.actionAllFeedFragmentToSettingFragment()
            if(directions != null){
                navController.navigate(directions)
            }
        }
    }

    private fun setUpFab(){
        findNavController(R.id.nav_host_fragment_container).addOnDestinationChangedListener(this@MainActivity)

        fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)

            setOnClickListener {
                navigateTo()
            }
        }
    }
    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if(destination.id == R.id.allFeedFragment){
            setUtilsForFeed()
        }else{
            setUtilsForOthers()
        }
    }
    private fun setUtilsForFeed(){
        fab.show()
        icon_switch.visibility = View.VISIBLE
        setting_button.visibility = View.VISIBLE
    }
    private fun setUtilsForOthers(){
        fab.visibility = View.INVISIBLE
        icon_switch.visibility = View.GONE
        setting_button.visibility = View.GONE
    }

    private fun navigateTo(){
        val directions = AllFeedFragmentDirections.actionAllFeedFragmentToWritingFragment(-1)
        if(directions != null){
            navController.navigate(directions)
        }
    }
}