package org.techtown.capstone2.fragments.feeds

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.capstone2.R
import org.techtown.capstone2.data.Post

class PostAdapter() : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    var items = ArrayList<Post>()
    lateinit var listener:OnPostItemClickListener

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                listener?.onItemClick(this,itemView,adapterPosition)
            }
        }
        fun setItem(item:Post){
            // Need to code here
        }
    }
}