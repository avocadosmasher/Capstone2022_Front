package org.techtown.capstone2.fragments

import android.view.View

interface ProfilePostListAdapterListener {
    fun onItemClick(holder:ProfilePostListAdapter.ViewHolder?, view: View?,position:Int,postId:Int)
}