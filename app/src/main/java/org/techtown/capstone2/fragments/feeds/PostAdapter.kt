package org.techtown.capstone2.fragments.feeds

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.apollo.GetAllPostsQuery
import org.techtown.apollo.GetSubscriberPostsQuery
import org.techtown.capstone2.databinding.PostItemLayoutBinding
import org.techtown.capstone2.viewmodel.MainViewModel
import kotlin.properties.Delegates

class PostAdapter() : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    lateinit var mainViewModel : MainViewModel
    val allList = ArrayList<GetAllPostsQuery.Post?>()
    val subList = ArrayList<GetSubscriberPostsQuery.GetSubscriberPost?>()
    lateinit var listener:PostAdapterListener

    fun getList() = if(mainViewModel.checkedLeft == true) allList else subList

    override fun getItemCount() = getList()?.size ?:0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == (itemCount - 1)){
            listener?.onReachedLastItem()
        }
        val item = getList()?.get(position)
        if(item != null) holder.bind(item)
    }

    inner class ViewHolder(private val binding: PostItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        var postId by Delegates.notNull<Int>()

        init {
            // cardView에 clickable 옵션을 줬기에 이렇게 해야함.
            binding.cardView.setOnClickListener {
                Log.d("PostItemClicked","Clicked1")
                postId?.let { it1 -> listener?.onItemClick(this,binding.cardView,adapterPosition, it1) }
            }
        }
        fun bind(item:Any){
            binding.apply {
                viewModel = mainViewModel
                if(item is GetAllPostsQuery.Post){
                    allPost = item
                    postId = item.id.toInt()
                }else if(item is GetSubscriberPostsQuery.GetSubscriberPost){
                    subPost = item
                    postId = item.id.toInt()
                }
            }
        }
    }
}