package com.test.progressbartest

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.TextView
import org.techtown.capstone2.R


class ProgressDialog(context: Context){
    private val dialog = Dialog(context)
    fun start(content:String?){
        Log.d("Dialog","Invoked")
        // dialog with no title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Setting layout of the dialog
        dialog.setContentView(R.layout.dialog_progress)
        // Unable to cancel the dialog by clicking space out of the dialog
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        content?.let { dialog.findViewById<TextView>(R.id.progress_content).text = it}

        dialog.show()
    }
    fun dismiss(){
        dialog.dismiss()
    }
}