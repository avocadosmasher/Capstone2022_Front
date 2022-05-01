package org.techtown.capstone2.fragments.feeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.apollo.GetAllPostsQuery
import org.techtown.capstone2.databinding.FragmentAllFeedBinding
import org.techtown.capstone2.viewmodel.MainViewModel

class AllFeedFragment : Fragment() {

    private lateinit var binding : FragmentAllFeedBinding
    private val viewModel: MainViewModel by activityViewModels()

    private var postAdapter = PostAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If there is no change after the initialization, that must be coded here!
        postAdapter.listener = object : PostAdapterListener{
            override fun onItemClick(holder: PostAdapter.ViewHolder?, view: View?, position: Int) {
                TODO("Not yet implemented")
            }

            override fun onReachedLastItem(items: ArrayList<GetAllPostsQuery.Post?>) {
                TODO("Not yet implemented")
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllFeedBinding.inflate(inflater,container,false)

        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter = postAdapter

        getDataSetFromServer(0,10)

        return binding.root
    }

    private fun addArrayListItem(posts: List<GetAllPostsQuery.Post?>?){
        if(posts != null){
            postAdapter.items.addAll(posts)
        }
    }

    fun getDataSetFromServer(off : Int, size : Int){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetAllPostsQuery(off, size)).execute()
            addArrayListItem(response.data?.posts)
            postAdapter.notifyDataSetChanged()
        }
    }

    private fun setNewArrayList(){
    }
}