package com.granson.okhistarter.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.granson.okhistarter.R
import com.granson.okhistarter.ui.starter.OkCollector
import com.granson.okhistarter.utilities.Utilities.navigateTo

class LauncherScreen: AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_OkHiStarter)
        super.onCreate(savedInstanceState)

        navigateTo(this, OkCollector::class.java)
    }
}