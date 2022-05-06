package org.techtown.capstone2.fragments.feeds

import android.view.View
import org.techtown.apollo.GetAllPostsQuery

interface PostAdapterListener {
    fun onItemClick(holder:PostAdapter.ViewHolder?, view: View?, position:Int)
    fun onReachedLastItem()
}