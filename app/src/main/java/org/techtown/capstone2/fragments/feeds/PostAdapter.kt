package org.techtown.capstone2.fragments.feeds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.apollo.GetAllPostsQuery
import org.techtown.capstone2.databinding.PostItemLayoutBinding

class PostAdapter() : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    val items = ArrayList<GetAllPostsQuery.Post?>()
    lateinit var listener:PostAdapterListener

    override fun getItemCount() = items?.size ?:0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == (itemCount - 1)){
            listener?.onReachedLastItem(items)
        }
        val item = items?.get(position)
        if(item != null) holder.bind(item)
    }

    inner class ViewHolder(private val binding: PostItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener?.onItemClick(this,itemView,adapterPosition)
            }
        }
        fun bind(item:GetAllPostsQuery.Post){
            binding.post = item
        }
    }
}