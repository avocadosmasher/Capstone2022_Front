package org.techtown.capstone2.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.google.android.material.transition.MaterialContainerTransform
import org.techtown.apollo.*
import org.techtown.apollo.type.MemberInput
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentProfileBinding
import org.techtown.capstone2.util.themeColor
import org.techtown.capstone2.viewmodel.MainViewModel


class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val args:ProfileFragmentArgs by navArgs()
    private lateinit var binding:FragmentProfileBinding
    private var isMyProfile = false
    lateinit var profile: GetProfileQuery.GetMember
    private val profilePostListAdapter = ProfilePostListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment_container
            duration = resources.getInteger(R.integer.material_motion_duration_long_2).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        binding.profileBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.profileArticle.isEnabled = false

        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.profilePostList.layoutManager = layoutManager
        binding.profilePostList.adapter = profilePostListAdapter

        /** 프로필 정보 받아오기 **/
        gqlGetProfile()

        /** 프로필 화면으로 넘어올때 받아온 member ID값을 통해서 어떤 member인지 확인 **/
        if(args.userId == viewModel.getUserId()) isMyProfile = true

        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(IsSubscribedQuery(viewModel.getUserId().toString(),args.userId.toString())).execute()
            response.data?.let {

                binding.profileSubscribeButton.isChecked = it.isSubscribed

                /** 구독 관련 및 자기소개  부분 **/
                if(isMyProfile){
                    binding.apply {

                        profileEditButton.setOnCheckedChangeListener { buttonView, isChecked ->

                            if (isChecked){
                                profileArticle.isEnabled = true
                            }else {
                                gqlSetArticle()
                                profileArticle.isEnabled = false
                            }
                        }
                        profileSubscribeButton.isClickable = false
                    }
                }else{
                    binding.profileEditButton.isClickable = false
                    binding.profileSubscribeButton.setOnCheckedChangeListener { buttonView, isChecked ->
                        if(isChecked){
                            gqlAddSubscribe()
                        }else{
                            gqlDeleteSubscribe()
                        }
                    }
                }
            }
        }

        profilePostListAdapter.listener = object : ProfilePostListAdapterListener{
            override fun onItemClick(
                holder: ProfilePostListAdapter.ViewHolder?,
                view: View?,
                position: Int,
                postId: Int
            ) {
                val profilePostListTransitionName = getString(R.string.post_card_detail_transition_name)
                val extras = FragmentNavigatorExtras((view to profilePostListTransitionName) as Pair<View, String>)
                val directions = ProfileFragmentDirections.actionProfileFragmentToDetailedPostFragment(postId)
                findNavController().navigate(directions,extras)
            }
        }

        return binding.root
    }

    private fun gqlGetProfile(){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetProfileQuery(args.userId.toString())).execute()
            response.data?.getMember?.let { profile = it }

            binding.profile = profile

            /** 글 목록을 받아옴 **/
            setPostListItems()
            /** 비동기 실행으로 인한 Subscriber 수는 여기서 받도록. **/
            gqlGetSubscribeNum()
        }
    }

    private fun gqlSetArticle(){
        lifecycleScope.launchWhenResumed {
            viewModel.apolloClient.mutation(UpdateArticleMutation(profile.id,
                Optional.Present(MemberInput(
                    email = profile.email,
                    name = profile.name,
                    article = Optional.Present(binding.profileArticle.text.toString()))
                ))
            ).execute()
        }
    }

    private fun gqlAddSubscribe(){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.mutation(AddSubscribeMutation(viewModel.getUserId().toString(),profile.id)).execute()
            val subNum = response.data?.addSubscribe?.subscriberCount
            subNum?.let { setSubscriberNum(it) }
        }
    }
    private fun gqlGetSubscribeNum(){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetSubscriberCountQuery(profile.id)).execute()
            val subNum = response.data?.getSubscriberCount
            subNum?.let{ setSubscriberNum(it) }
        }
    }
    private fun gqlDeleteSubscribe(){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.mutation(DeleteSubscribeMutation(viewModel.getUserId().toString(),profile.id)).execute()
            val subNum = response.data?.deleteSubscribe?.subscriberCount
            subNum?.let { setSubscriberNum(it) }
        }
    }

    private fun setSubscriberNum(num:Int){ binding.profileSubscriberNum.text = num.toString()}

    private fun setPostListItems(){
        profile.posts?.let {
            profilePostListAdapter.itemList.apply {
                clear()
                addAll(it)
            }
        }
        profilePostListAdapter.notifyDataSetChanged()
    }
}