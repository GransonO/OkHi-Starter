package com.granson.okhistarter.utilities

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast

object Utilities {

    fun navigateTo(context: Context, clazz: Class<*>){
        val intent = Intent(context, clazz)
        context.startActivity(intent)
    }

    fun View.hide(){
        this.visibility = View.GONE
    }

    fun View.show(){
        this.visibility = View.VISIBLE
    }


    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}