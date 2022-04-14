package org.techtown.capstone2.fragments.feeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentAllFeedBinding

class AllFeedFragment : Fragment() {

    private lateinit var binding : FragmentAllFeedBinding

    private var postAdapter = PostAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllFeedBinding.inflate(inflater,container,false)
        return binding.root
    }
}