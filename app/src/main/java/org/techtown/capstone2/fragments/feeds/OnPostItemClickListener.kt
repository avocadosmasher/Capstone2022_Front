package org.techtown.capstone2.fragments.feeds

import android.view.View

interface OnPostItemClickListener {
    fun onItemClick(holder:PostAdapter.ViewHolder?,view: View?,position:Int)
}