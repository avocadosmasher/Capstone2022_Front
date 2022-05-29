package org.techtown.capstone2.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.apollo.GetProfileQuery
import org.techtown.capstone2.databinding.ProfilePostItemLayoutBinding

class ProfilePostListAdapter() : RecyclerView.Adapter<ProfilePostListAdapter.ViewHolder>() {

    val itemList = ArrayList<GetProfileQuery.Post>()
    lateinit var listener:ProfilePostListAdapterListener

    override fun getItemCount() = itemList.size ?:0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProfilePostItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        item?.let { holder.bind(item) }
    }

    inner class ViewHolder(private val binding: ProfilePostItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.profilePostItemTitle.setOnClickListener {
                listener?.onItemClick(this,binding.root,adapterPosition,binding.post.id.toInt())
            }
        }
        fun bind(item:GetProfileQuery.Post){
            binding.post = item
        }
    }
}