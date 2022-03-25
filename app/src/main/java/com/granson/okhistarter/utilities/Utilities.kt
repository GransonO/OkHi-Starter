package com.granson.okhistarter.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast

import com.granson.okhistarter.utilities.Constants.DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*


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

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(milliSeconds: Long): String? {
        val formatter = SimpleDateFormat(DATE_FORMAT)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}