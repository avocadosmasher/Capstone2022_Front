package org.techtown.capstone2.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.apollographql.apollo3.api.Optional
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_detailed_post.*
import kotlinx.android.synthetic.main.fragment_writing.*
import kotlinx.android.synthetic.main.fragment_writing.WritingView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.notify
import okhttp3.internal.notifyAll
import okhttp3.internal.wait
import org.techtown.apollo.*
import org.techtown.apollo.type.CommentInput
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentDetailedPostBinding
import org.techtown.capstone2.fragments.feeds.AllFeedFragmentDirections
import org.techtown.capstone2.fragments.feeds.MediaPlayerFragment
import org.techtown.capstone2.util.CommentsDiffUtil
import org.techtown.capstone2.util.themeColor
import org.techtown.capstone2.viewmodel.MainViewModel

class DetailedPostFragment : Fragment() {

    private lateinit var binding: FragmentDetailedPostBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: DetailedPostFragmentArgs by navArgs()
    private val commentsAdapter = CommentsAdapter()


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
        // Inflate the layout for this fragment
        binding =  FragmentDetailedPostBinding.inflate(inflater,container,false)

        /** GetPost를 통해서 Post 정보 불러오기 **/
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetPostQuery(args.postId.toString())).execute()
            binding.post = response.data?.getPost

            /** Music Player Fragment를 FrameLayout에 적용 **/
            response.data?.getPost?.apply {
                val frag = MediaPlayerFragment.newInstance(audio,id)
                parentFragmentManager.beginTransaction().replace(R.id.detailed_post_music_player_container,frag).commit()
            }

            /** 본인이 작성한 글인지 확인 **/
            // 수정, 삭제 버튼의 활성화 여부를 결정합시다.

            if(viewModel.getUserId() == response.data?.getPost?.member?.id?.toInt()){
                binding.detailedPostUpdate.visibility=View.VISIBLE
                binding.detailedPostDelete.visibility=View.VISIBLE
                /** 수정 버튼 Listener **/
                binding.detailedPostUpdate.setOnClickListener {
                    binding?.post?.let{
                        findNavController().navigate(DetailedPostFragmentDirections.actionDetailedPostFragmentToWritingFragment(it.id.toInt()))
                    }
                }
                /** 삭제 버튼 Listener **/
                binding.detailedPostDelete.setOnClickListener {
                    lifecycleScope.launchWhenResumed {
                        viewModel.apolloClient.mutation(DeletePostMutation(args.postId.toString())).execute()
                        findNavController().popBackStack()
                    }
                }
            }
        }

        binding.detailedPostBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        /** RecyclerView를 위한 Configuration **/
        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.detailedPostCommentsContainer.apply {
            this.layoutManager = layoutManager
            this.adapter = commentsAdapter
        }

        /** 프로필 CardView 클릭 Listener **/
        binding.detailedPostWriter.setOnClickListener {
            val view = it
            binding?.post?.let {
                Log.d("PostClick","클릭")
                val profileTransitionName = getString(R.string.profile_card_profile_transition_name)
                val extras = FragmentNavigatorExtras((view to profileTransitionName) as Pair<View, String>)
                val directions = DetailedPostFragmentDirections.actionDetailedPostFragmentToProfileFragment(it.member.id.toInt())
                findNavController().navigate(directions,extras)
            }
        }

        /** 댓글 불러오기. **/
        gqlGetComments()

        /** 댓글 작성 부분 **/
        binding.detailedPostCommentAddButton.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.apolloClient.mutation(AddCommentMutation(
                    CommentInput(
                        postId = args.postId,
                        memberId = viewModel.getUserId(),
                        content = binding.detailedPostCommentWriting.text.toString()
                    )
                )).execute()

                binding.detailedPostCommentWriting.text.clear()
                gqlGetComments()
            }

        }

        commentsAdapter.listener = object: CommentsAdapterListener{
            /** 댓글 삭제 **/
            override fun onDeleteClick(commentId: Int) {

                lifecycleScope.launchWhenResumed {
                    viewModel.apolloClient.mutation(DeleteCommentMutation(commentId = commentId.toString())).execute()
                    gqlGetComments()
                }
            }
        }
        commentsAdapter.mainViewModel = viewModel

        return binding.root
    }

    private fun gqlGetComments(){
        /** GetComments를 통한 Comment 정보 불러오기 **/
        /** CommentsAdapter에 받아온 List 넣기. **/
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetCommentsQuery(args.postId.toString())).execute()
            response.data?.getCommentsByPostId?.let { setComments(it) }
        }
    }
    private fun setComments(comments:List<GetCommentsQuery.GetCommentsByPostId>){
        val diffResult = DiffUtil.calculateDiff(CommentsDiffUtil(commentsAdapter.itemList,comments))
        diffResult.dispatchUpdatesTo(commentsAdapter)
        commentsAdapter.itemList = ArrayList(comments)
    }
}