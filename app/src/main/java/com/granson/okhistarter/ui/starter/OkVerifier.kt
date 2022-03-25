package com.granson.okhistarter.ui.starter

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.granson.okhistarter.databinding.ActivityOkVerifierBinding
import com.granson.okhistarter.utilities.DataStore
import com.granson.okhistarter.utilities.Utilities
import com.granson.okhistarter.utilities.Utilities.showToast
import io.okhi.android_core.OkHi
import io.okhi.android_core.interfaces.OkHiRequestHandler
import io.okhi.android_core.models.OkHiException
import io.okhi.android_core.models.OkHiLocation
import io.okhi.android_core.models.OkHiUser
import io.okhi.android_okverify.OkVerify
import io.okhi.android_okverify.interfaces.OkVerifyCallback
import io.okhi.android_okverify.models.OkHiNotification


class OkVerifier : AppCompatActivity() {


    private val binding: ActivityOkVerifierBinding by lazy {
        ActivityOkVerifierBinding.inflate(layoutInflater)
    }

    private lateinit var context: Context
    private var okhi: OkHi? = null
    private var okVerify: OkVerify? = null
    private lateinit var workAddress: OkHiLocation
    private lateinit var dataStore: DataStore

    var firstname = ""
    var lastname = ""
    private var phone = ""
    private var location_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        context = this

        val dataStore = DataStore(this)
        location_id = dataStore.readString("location_id")


        workAddress = OkHiLocation(location_id, -1.313339237582541, 36.842414181487776)

        try {
            okhi = OkHi(this)
            okVerify = OkVerify.Builder(this).build()
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
        } catch (exception: OkHiException) {
            exception.printStackTrace()
        }

        binding.apply {
            stopVerification.setOnClickListener {
                stopAddressVerification()
            }

            stopForeground.setOnClickListener {
                stopForegroundVerification()
            }

            startVerification.setOnClickListener {
                startAddressVerification()
            }
        }
    }

    // Define a method you'll use to start okverify
    private fun startAddressVerification() {
        val canStartAddressVerification: Boolean = canStartAddressVerification()

        // If all the checks pass attempt to start okverify
        if (canStartAddressVerification) {
            // Create an okhi user

            firstname = dataStore.readString("firstname")
            lastname = dataStore.readString("lastname")
            phone = dataStore.readString("phone")

            val user = OkHiUser.Builder(phone)
                .withFirstName(firstname)
                .withLastName(lastname)
                .build()

            // Start verification
            okVerify!!.start(user, workAddress, object : OkVerifyCallback<String> {
                override fun onSuccess(result: String) {

                    if(!checkForegroundService())
                        showToast(context, "Successfully started verification for: $result")
                        startForegroundVerification()
                }

                override fun onError(e: OkHiException) {
                    showToast(context, "Something went wrong: " + e.code)
                }
            })
        }
    }

    internal class Handler : OkHiRequestHandler<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) OkVerifier().startAddressVerification()
        }

        override fun onError(exception: OkHiException) {
            Log.e("OkVerifier Error", exception.message.toString())
        }
    }

    // Define a method you'll use to check if conditions are met to start okverify - this method will be added in the lib on the next update
    private fun canStartAddressVerification(): Boolean {
        val requestHandler = Handler()
        // Check and request user to enable location services
        if (!OkHi.isLocationServicesEnabled(applicationContext)) {
            okhi!!.requestEnableLocationServices(requestHandler)
        } else if (!OkHi.isGooglePlayServicesAvailable(applicationContext)) {
            // Check and request user to enable google play services
            okhi!!.requestEnableGooglePlayServices(requestHandler)
        } else if (!OkHi.isBackgroundLocationPermissionGranted(applicationContext)) {
            // Check and request user to grant location permission
            okhi!!.requestBackgroundLocationPermission(
                "Hey we need  background location permissions",
                "Pretty please..",
                requestHandler
            )
        } else {
            return true
        }
        return false
    }

//    fun handleButtonTap(view: View?) {
//        startAddressVerification()
//    }

    private fun stopAddressVerification() {
        OkVerify.stop(applicationContext, workAddress.id)
    }

    private fun startForegroundVerification() {
        try {
            // start a foreground service that'll improve the stability and reliability of verification signals
            OkVerify.startForegroundService(applicationContext)
        } catch (e: OkHiException) {
            e.printStackTrace()
        }
    }

    private fun stopForegroundVerification() {
        // stops the running foreground service
        if (checkForegroundService())
            OkVerify.stopForegroundService(applicationContext)
    }

    private fun checkForegroundService(): Boolean {
        // checks if the foreground service is running
        return OkVerify.isForegroundServiceRunning(applicationContext)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        okhi!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        okhi!!.onActivityResult(requestCode, resultCode, data)
    }
}