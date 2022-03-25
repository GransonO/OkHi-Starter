package com.granson.okhistarter.utilities

import android.content.Context
import android.content.SharedPreferences
import com.granson.okhistarter.R
import java.lang.Exception

class DataStore(context: Context) {

    private val prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    private val editor = prefs.edit()

    private fun applyEditor(dataCall: () -> SharedPreferences.Editor){
        (dataCall.invoke()).apply()
    }

    // Read Values
    fun readString(value: String): String = prefs.getString(value, "").toString()

    // Save Values
    fun saveEntry(key: String, value: Any): Boolean{
        return try {
            when(value){
                is String -> {
                    applyEditor { editor.putString(key, value) }
                }
            }
            true
        }catch (e: Exception){
            false
        }
    }
}