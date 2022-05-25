package org.techtown.capstone2.fragments.feeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.google.android.material.transition.MaterialElevationScale
import org.techtown.apollo.GetAllPostsQuery
import org.techtown.apollo.GetSubscriberPostsQuery
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentAllFeedBinding
import org.techtown.capstone2.viewmodel.MainViewModel

class AllFeedFragment : Fragment() {

    private lateinit var binding : FragmentAllFeedBinding
    private lateinit var offset: Array<Int>
    private var isReachedLast = false
    private var offsetIndex = 0
    private val importSize = 10
    private val viewModel: MainViewModel by activityViewModels()
    private var postAdapter = PostAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Setting Animation Config **/
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }

        /** Adapter Setting **/
        postAdapter.mainViewModel = viewModel

        offsetIndex = 0
        offset = arrayOf(0,0)
        // If there is no change after the initialization, that must be coded here!
        postAdapter.listener = object : PostAdapterListener{
            override fun onItemClick(holder: PostAdapter.ViewHolder?, view: View?, position: Int,postId:Int) {
                val detailedPostTransitionName = getString(R.string.post_card_detail_transition_name)
                val extras = FragmentNavigatorExtras((view to detailedPostTransitionName) as Pair<View, String>)
                val directions = AllFeedFragmentDirections.actionAllFeedFragmentToDetailedPostFragment(postId)
                findNavController().navigate(directions,extras)
            }
            override fun onReachedLastItem() {
                if(!isReachedLast){
                    when(viewModel.checkedLeft){
                        true -> getAllPosts(offset[offsetIndex],importSize,true)
                        false -> getSubPosts(offset[offsetIndex],importSize,true)
                    }
                    offset[offsetIndex] += 1
                }
            }
        }
        viewModel.iconSwitchListener = object : IconSwitchListener{
            override fun onIconSwitchChanged(state: Boolean) {
                isReachedLast = false
                offset[offsetIndex] = 0

                viewModel.checkedLeft = state
                when(state){
                    true -> {
                        offsetIndex = 0
                        getAllPosts(offset[offsetIndex],importSize,false)
                    }
                    false ->{
                        offsetIndex = 1
                        getSubPosts(offset[offsetIndex],importSize,false)
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllFeedBinding.inflate(inflater,container,false)

        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter = postAdapter

        getAllPosts(offset[offsetIndex],importSize,true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    /** 전체 피드 관련 Method **/
    private fun addAllPosts(posts: List<GetAllPostsQuery.Post?>?){
        // Can be used to initialize or just add more data to the list.
        if(posts != null){
            postAdapter.allList.addAll(posts)
        }
    }
    private fun getAllPosts(off : Int, size : Int, addTsetF: Boolean){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetAllPostsQuery(toOptionalInt(off), toOptionalInt(size))).execute()

            if(response.data?.getBoard?.posts?.size?:9 < 10) isReachedLast = true

            when(addTsetF){
                true -> addAllPosts(response.data?.getBoard?.posts)
                false -> setAllPosts(response.data?.getBoard?.posts)
            }
            postAdapter.notifyDataSetChanged()
        }
    }
    private fun setAllPosts(posts: List<GetAllPostsQuery.Post?>?){
        // Can be used to refresh the arraylist
        if(posts != null){
            postAdapter.allList.clear()
            postAdapter.allList.addAll(posts)
        }
    }

    /** 구독 피드 관련 Method **/
    private fun addSubPosts(posts:List<GetSubscriberPostsQuery.GetSubscriberPost?>?){
        if(posts != null){
            postAdapter.subList.addAll(posts)
        }
    }
    private fun getSubPosts(off : Int, size : Int, addTsetF: Boolean){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetSubscriberPostsQuery(toOptionalString("1"),toOptionalInt(off), toOptionalInt(size))).execute()
            if(response.data?.getSubscriberPosts?.size?:9 < 10) isReachedLast = true
            when(addTsetF){
                true -> addSubPosts(response.data?.getSubscriberPosts)
                false -> setSubPosts(response.data?.getSubscriberPosts)
            }
            postAdapter.notifyDataSetChanged()
        }
    }
    private fun setSubPosts(posts:List<GetSubscriberPostsQuery.GetSubscriberPost?>?){
        if(posts != null){
            postAdapter.subList.clear()
            postAdapter.subList.addAll(posts)
        }
    }
    private fun toOptionalInt(num : Int) = Optional.Present(num)
    private fun toOptionalString(st : String) = Optional.Present(st)
}