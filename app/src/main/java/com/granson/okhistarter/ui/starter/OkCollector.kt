package com.granson.okhistarter.ui.starter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.granson.okhistarter.databinding.ActivityMainBinding
import com.granson.okhistarter.utilities.Utilities.showToast
import io.okhi.android_core.models.OkHiException
import io.okhi.android_core.models.OkHiLocation
import io.okhi.android_core.models.OkHiUser
import io.okhi.android_okcollect.OkCollect
import io.okhi.android_okcollect.callbacks.OkCollectCallback
import io.okhi.android_okcollect.utilities.OkHiConfig
import io.okhi.android_okcollect.utilities.OkHiTheme


class OkCollector : AppCompatActivity() {

    private var okCollect: OkCollect? = null
    private lateinit var context: Context

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide();

        context = this

        // define a theme that'll be applied to OkCollect
        val theme: OkHiTheme = OkHiTheme.Builder("#008080")
            .setAppBarLogo("https://cdn.okhi.co/icon.png")
            .setAppBarColor("#008080")
            .build()

        // configure any optional features you'd like
        val config: OkHiConfig = OkHiConfig.Builder()
            .withStreetView()
            .build()

        try {

            okCollect = OkCollect.Builder(this)
                .withTheme(theme)
                .withConfig(config)
                .build()

        } catch (exception: OkHiException) {
            exception.printStackTrace()
        }

        binding.apply {
            okCollectBtn.setOnClickListener {
                val name = nameInputPlace.text.toString()
                val phone = phoneInput.text.toString()

                if(name.isNotEmpty() && phone.isNotEmpty()){
                    if(phone.startsWith("+254")){
                        val names = nameManipulator(name)
                        launchOkCollect(
                            firstName = names[0],
                            lastName = if (names.size == 2) names[1] else " ",
                            phoneNo = phone
                        )
                    }else{
                        showToast(context,"Phone number should start with the country code")
                    }
                }else{
                    showToast(context,"Please fill all entries")
                }
            }
            openVerifier.setOnClickListener {
                val intent = Intent(context, OkVerifier::class.java)
                startActivity(intent)
            }
        }
    }

    private fun nameManipulator(name: String): List<String> {
        return name.split(" ")
    }

    private fun launchOkCollect(
        firstName: String,
        lastName: String,
        phoneNo: String,
    ) {
        // define a user
        val user = OkHiUser.Builder(phoneNo)
            .withFirstName(firstName)
            .withLastName(lastName)
            .build()


        okCollect!!.launch(user, object : OkCollectCallback<OkHiUser, OkHiLocation> {
            override fun onSuccess(user: OkHiUser, location: OkHiLocation) {
                showToast(context, "Address created Successfully" + user.phone + " " + location.id)
            }

            override fun onError(e: OkHiException) {
                showToast(context, "Error " + e.message)
            }
        })
    }
}