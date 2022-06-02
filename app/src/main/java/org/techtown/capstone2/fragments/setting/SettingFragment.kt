package org.techtown.capstone2.fragments.setting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import org.techtown.capstone2.LoginActivity
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentSettingBinding
import org.techtown.capstone2.fragments.feeds.AllFeedFragmentDirections
import org.techtown.capstone2.util.PreferenceManager
import org.techtown.capstone2.viewmodel.MainViewModel

class SettingFragment : Fragment() {

    private lateinit var binding:FragmentSettingBinding
    private val viewModel:MainViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Setting Animation Config **/
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(inflater,container,false)

        binding.settingBackButton.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.settingNotification.setOnClickListener {
            val notificationTransitionName = getString(R.string.setting_notification_transition_name)
            val extras = FragmentNavigatorExtras((it to notificationTransitionName) as Pair<View, String>)
            val directions = SettingFragmentDirections.actionSettingFragmentToNotificationFragment()
            findNavController().navigate(directions,extras)
        }

        binding.settingProfileUpdate.setOnClickListener {
            val profileUpdateTransitionName = getString(R.string.profile_card_profile_transition_name)
            val extras = FragmentNavigatorExtras((it to profileUpdateTransitionName) as Pair<View, String>)
            val directions = SettingFragmentDirections.actionSettingFragmentToProfileFragment(viewModel.getUserId())
            findNavController().navigate(directions,extras)
        }

        binding.settingLogOut.setOnClickListener {
            activity?.let { it1 ->
                PreferenceManager.setString(it1.applicationContext,"token","")
                PreferenceManager.setString(it1.applicationContext,"id","")
                val intent = Intent(it1.baseContext,LoginActivity::class.java)
                startActivity(intent)
                it1.finish()
            }
        }

        return binding.root
    }
}