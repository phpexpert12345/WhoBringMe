package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityCreateJobBinding
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.utilities.BaseActivity

class CreateJobActivity : BaseActivity() {

    private var counting: Int = 10

    private lateinit var createJobBinding: ActivityCreateJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        createJobBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_job)

        Glide.with(this).load(sharedPrefrenceManager.getProfile().login_photo)
                .placeholder(R.drawable.user_placeholder)
                .circleCrop()
                .into(createJobBinding.userImage)

        createJobBinding.userName.text = sharedPrefrenceManager.getProfile().login_name
        setActions()
    }

    private fun setActions() {
        createJobBinding.submitButton.setOnClickListener {
            if (checkValidation()) {
                createJobBinding.submitButton.startAnimation()
                val postJobPostDto = PostJobPostDto()
                postJobPostDto.jobAmount = createJobBinding.totalAmount.text.toString()
                postJobPostDto.jobDescription = createJobBinding.postInfo.text.toString()
                postJobPostDto.jobTime = createJobBinding.mintsTextView.text.toString()
                Handler().postDelayed({
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("postValue", postJobPostDto)
                    startActivity(intent)
                }, 1000)
            }else{
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_LONG).show()
            }
        }

        createJobBinding.closeIcon.setOnClickListener {
            finish()
        }
        createJobBinding.plusIcon.setOnClickListener {
            if (counting<170) {
                counting += 10
                createJobBinding.mintsTextView.text = counting.toString()
            }
        }

        createJobBinding.minusIcon.setOnClickListener {
            if (counting != 10) {
                counting -= 10
                createJobBinding.mintsTextView.text = counting.toString()
            }
        }
    }

    private fun checkValidation(): Boolean {
        return when {
            createJobBinding.postInfo.text.toString().isEmpty() -> {
                false
            }
            createJobBinding.totalAmount.text.toString().isEmpty() -> {
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    override fun onPause() {
        super.onPause()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        createJobBinding.submitButton.revertAnimation()
    }
}