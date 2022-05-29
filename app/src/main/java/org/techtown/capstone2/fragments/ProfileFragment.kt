package org.techtown.capstone2.fragments

import android.os.Bundle
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
import org.techtown.apollo.*
import org.techtown.apollo.type.MemberInput
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentProfileBinding
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.profilePostList.layoutManager = layoutManager
        binding.profilePostList.adapter = profilePostListAdapter

        /** 프로필 정보 받아오기 **/
        gqlGetProfile()

        /** 프로필 화면으로 넘어올때 받아온 member ID값을 통해서 어떤 member인지 확인 **/
        if(args.userId == viewModel.getUserId()) isMyProfile = true

        /** 구독 관련 및 자기소개  부분 **/
        if(isMyProfile){
            binding.apply {
                profileEditButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked){
                        gqlSetArticle()
                        profileArticle.isEnabled = false
                    }else {
                        profileArticle.isEnabled = true
                    }
                }
                profileSubscribeButton.isClickable = false
            }
        }else{
            binding.profileSubscribeButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    gqlAddSubscribe()
                }else{
                    gqlDeleteSubscribe()
                }
            }
        }
        gqlGetSubscribeNum()

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

            /** 글 목록을 받아옴 **/
            setPostListItems()
        }
    }

    private fun gqlSetArticle(){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.mutation(UpdateArticleMutation(profile.id,
                Optional.Present(MemberInput(
                    email = profile.email,
                    name = profile.name,
                    article = Optional.Present(profile.article))
                ))
            )
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
    }
}