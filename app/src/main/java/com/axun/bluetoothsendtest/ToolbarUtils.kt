package com.axun.bluetoothsendtest

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.widget.TextView

object ToolbarUtils {

    fun setToolbar(activity: AppCompatActivity, toolbar:Toolbar){
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            activity.finish()
        }
    }

    fun addMiddleTitle(context: Context,title:CharSequence,toolbar: Toolbar){
        val textView = TextView(context)
        textView.text = title
        textView.setTextColor(Color.parseColor("#ffffff"))
        textView.textSize = 20f
        val params:Toolbar.LayoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        toolbar.addView(textView, params)
    }
}