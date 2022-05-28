package org.techtown.capstone2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.apollographql.apollo3.api.Optional
import org.techtown.apollo.GetProfileQuery
import org.techtown.apollo.UpdateArticleMutation
import org.techtown.apollo.type.MemberInput
import org.techtown.capstone2.databinding.FragmentProfileBinding
import org.techtown.capstone2.viewmodel.MainViewModel


class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val args:ProfileFragmentArgs by navArgs()
    private lateinit var binding:FragmentProfileBinding
    private var isMyProfile = false
    lateinit var profile: GetProfileQuery.GetMember

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

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

        TODO("글 목록 받아오는 함수.")
        TODO("글 목록 Click Listener.")

        return binding.root
    }

    private fun gqlGetProfile(){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetProfileQuery(args.userId.toString())).execute()
            response.data?.getMember?.let { profile = it }
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

    }
    private fun gqlGetSubscribeNum(){

    }
    private fun gqlDeleteSubscribe(){

    }

    private fun setSubscriberNum(num:Int){ binding.profileSubscriberNum.text = num.toString()}
}