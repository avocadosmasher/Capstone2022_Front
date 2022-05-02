package org.techtown.capstone2.fragments.feeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.polyak.iconswitch.IconSwitch
import com.polyak.iconswitch.IconSwitch.Checked
import org.techtown.apollo.GetAllPostsQuery
import org.techtown.capstone2.databinding.FragmentAllFeedBinding
import org.techtown.capstone2.viewmodel.MainViewModel

class AllFeedFragment : Fragment() {

    private lateinit var binding : FragmentAllFeedBinding
    private var offset: Int = 0

    private val viewModel: MainViewModel by activityViewModels()
    private var postAdapter = PostAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        offset = 0

        // If there is no change after the initialization, that must be coded here!
        postAdapter.listener = object : PostAdapterListener{
            override fun onItemClick(holder: PostAdapter.ViewHolder?, view: View?, position: Int) {
                TODO("Not yet implemented")
            }
            override fun onReachedLastItem(items: ArrayList<GetAllPostsQuery.Post?>) {
                getDataSetFromServer(offset,10,true)
            }
        }
        viewModel.iconSwitchListener = object : IconSwitchListener{
            override fun onIconSwitchChanged(state: Checked) {
                when(state){
                    Checked.LEFT -> {}
                    Checked.RIGHT ->{}
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllFeedBinding.inflate(inflater,container,false)

        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter = postAdapter

        getDataSetFromServer(0,10,true)

        return binding.root
    }

    private fun addArrayListItem(posts: List<GetAllPostsQuery.Post?>?){
        // Can be used to initialize or just add more data to the list.
        if(posts != null){
            postAdapter.items.addAll(posts)
        }
    }

    fun getDataSetFromServer(off : Int, size : Int, addTsetF: Boolean){
        lifecycleScope.launchWhenResumed {
            val response = viewModel.apolloClient.query(GetAllPostsQuery(off, size)).execute()
            when(addTsetF){
                true -> addArrayListItem(response.data?.posts)
                false -> setNewArrayList(response.data?.posts)
            }
            postAdapter.notifyDataSetChanged()
        }
    }

    private fun setNewArrayList(posts: List<GetAllPostsQuery.Post?>?){
        // Can be used to refresh the arraylist
        if(posts != null){
            postAdapter.items.clear()
            postAdapter.items.addAll(posts)
        }
    }
}