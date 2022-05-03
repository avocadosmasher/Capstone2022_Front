package org.techtown.capstone2.fragments.feeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.polyak.iconswitch.IconSwitch.Checked
import org.techtown.apollo.GetAllPostsQuery
import org.techtown.capstone2.databinding.FragmentAllFeedBinding
import org.techtown.capstone2.viewmodel.MainViewModel
import kotlin.collections.ArrayList

class AllFeedFragment : Fragment() {

    private lateinit var binding : FragmentAllFeedBinding
    private lateinit var offset: Array<Int>
    private var offsetIndex = 0
    private val importSize = 10

    private val viewModel: MainViewModel by activityViewModels()
    private var postAdapter = PostAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        offsetIndex = 0
        offset = arrayOf(0,0)
        // If there is no change after the initialization, that must be coded here!
        postAdapter.listener = object : PostAdapterListener{
            override fun onItemClick(holder: PostAdapter.ViewHolder?, view: View?, position: Int) {
                TODO("Not yet implemented")
            }
            override fun onReachedLastItem(items: ArrayList<GetAllPostsQuery.Post?>) {
                getDataSetFromServer(offset[offsetIndex],importSize,true)
                offset[offsetIndex] += 1
            }
        }
        viewModel.iconSwitchListener = object : IconSwitchListener{
            override fun onIconSwitchChanged(state: Checked) {
                when(state){
                    Checked.LEFT -> {
                        getDataSetFromServer(offset[offsetIndex],importSize,false)
                        offsetIndex = 0
                    }
                    Checked.RIGHT ->{
                        TODO("현재 계정의 ID를 기준으로 POST들을 불러와야함.")
                        //getDataSetFromServer(offset[offsetIndex],importSize,false)
                        //offsetIndex = 1
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

        getDataSetFromServer(offset[offsetIndex],importSize,true)

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
            val response = viewModel.apolloClient.query(GetAllPostsQuery(toOptionalInt(off), toOptionalInt(size))).execute()
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

    private fun toOptionalInt(num : Int) = Optional.Present(num)
}