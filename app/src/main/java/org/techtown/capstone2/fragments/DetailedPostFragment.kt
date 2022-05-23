package org.techtown.capstone2.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_detailed_post.*
import kotlinx.android.synthetic.main.fragment_writing.*
import kotlinx.android.synthetic.main.fragment_writing.WritingView
import org.techtown.apollo.GetPostQuery
import org.techtown.capstone2.R
import org.techtown.capstone2.databinding.FragmentDetailedPostBinding
import org.techtown.capstone2.fragments.feeds.MediaPlayerFragment
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

        /** RecyclerView를 위한 Configuration **/
        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.detailedPostCommentsContainer.apply {
            this.layoutManager = layoutManager
            this.adapter = commentsAdapter
        }

        /** GetPost를 통해서 Post 정보 불러오기 **/
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetPostQuery(args.postId.toString())).execute()
            binding.post = response.data?.getPost
        }

        /** 본인이 작성한 글인지 확인 **/
        // 수정, 삭제 버튼의 활성화 여부를 결정합시다.
        if(viewModel.getUserId() != binding.post.member.id.toInt()){
            // 타인이 작성한 글.
            binding.detailedPostUpdate.visibility=View.GONE
            binding.detailedPostDelete.visibility=View.GONE
        }

        /** Music Player Fragment를 FrameLayout에 적용 **/
        val frag = MediaPlayerFragment.newInstance(binding.post.audio)
        parentFragmentManager.beginTransaction().replace(R.id.detailed_post_music_player_container,frag).commit()

        /** Comments List를 CommentsAdapter의 itemList에 할당. **/
        binding.post.comments?.let { commentsAdapter.itemList.addAll(it) }

        return binding.root
    }
}