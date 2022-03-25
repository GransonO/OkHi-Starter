package com.granson.okhistarter.ui.starter

import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.granson.okhistarter.databinding.ActivityOkVerifierBinding
import io.okhi.android_core.models.OkHiException
import io.okhi.android_okverify.OkVerify
import io.okhi.android_okverify.models.OkHiNotification


class OkVerifier : AppCompatActivity() {

    private val binding: ActivityOkVerifierBinding by lazy {
        ActivityOkVerifierBinding.inflate(layoutInflater)
    }

    private var okVerify: OkVerify? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        try {
            okVerify = OkVerify.Builder(this).build()
        } catch (exception: OkHiException) {
            exception.printStackTrace()
        }

        val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) NotificationManager.IMPORTANCE_DEFAULT else 3
        OkVerify.init(
            applicationContext, OkHiNotification(
                "Verifying your address",
                "We're currently verifying your address. This won't take long",
                "OkHi",
                "OkHi Address Verification",
                "Alerts related to any address verification updates",
                importance,
                1,  // notificationId
                2 // notification request code
            )
        )
    }
}