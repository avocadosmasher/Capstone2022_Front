package org.techtown.capstone2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentProfileBinding
import org.techtown.capstone2.viewmodel.MainViewModel


class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding:FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        TODO("프로필 화면으로 넘어올때 받아온 member ID  값을 통해서 어떤 member인지 확인.")
        TODO("자신의 프로필일 경우에는 간단한 설명 부분 수정 활성화, 구독 버튼 Listener는 비활성화.")
        TODO("프로필 관련 내용 GraphQL을 통해 받아오기. => 사진, 간단한 설명글, 구독 수, 글 목록(제목만 받아와서 나열하자).")
        TODO("GraphQL로 프로필 내용 및 글 목록 받아오는 함수.")
        TODO("글 목록 Click Listener.")
        TODO("구독 Mutation")
        TODO("간단한 설명(article) Mutation")


        return binding.root
    }
}