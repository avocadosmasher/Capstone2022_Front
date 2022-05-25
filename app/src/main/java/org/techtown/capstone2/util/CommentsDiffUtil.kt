package org.techtown.capstone2.util

import androidx.recyclerview.widget.DiffUtil
import org.techtown.apollo.GetCommentsQuery

class CommentsDiffUtil(private val oldList:List<GetCommentsQuery.GetCommentsByPostId>,private val curList:List<GetCommentsQuery.GetCommentsByPostId>): DiffUtil.Callback(){
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = curList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == curList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == curList[newItemPosition]
    }
}