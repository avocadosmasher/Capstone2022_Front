package org.techtown.capstone2.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.apollo.GetPostQuery
import org.techtown.capstone2.databinding.CommentItemLayoutBinding
import org.techtown.capstone2.viewmodel.MainViewModel

class CommentsAdapter() : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    lateinit var mainViewModel:MainViewModel
    lateinit var listener:CommentsAdapterListener
    val itemList = ArrayList<GetPostQuery.Comment>()

    override fun getItemCount() = itemList?.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommentItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList.get(position)
        item?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding:CommentItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.detailedPostCommentDelete.setOnClickListener {
                listener?.onDeleteClick(binding.comments.id.toInt())
            }
        }
        fun bind(item:GetPostQuery.Comment){
            binding.apply {
                viewModel = mainViewModel
                comments = item
                if(comments.member.id.toInt() != mainViewModel.getUserId()){
                    binding.detailedPostCommentDelete.visibility = View.GONE
                }
            }
        }
    }
}