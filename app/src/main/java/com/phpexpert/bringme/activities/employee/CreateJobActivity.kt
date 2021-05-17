package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityCreateJobBinding
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.SoftInputAssist

@Suppress("DEPRECATION")
class CreateJobActivity : BaseActivity() {

    private var counting: Int = 10
    private lateinit var softInputAssist: SoftInputAssist

    private lateinit var createJobBinding: ActivityCreateJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createJobBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_job)
        createJobBinding.languageModel = sharedPrefrenceManager.getLanguageData()
        softInputAssist = SoftInputAssist(this)

        Glide.with(this).load(sharedPrefrenceManager.getProfile().login_photo)
                .circleCrop()
                .placeholder(R.drawable.user_placeholder)
                .into(createJobBinding.userImage)

        createJobBinding.userName.text = sharedPrefrenceManager.getProfile().login_name
        setActions()
    }

    private fun setActions() {

        createJobBinding.totalAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().trim() == "(${getCurrencySymbol()})") {
                    createJobBinding.totalAmount.text = Editable.Factory.getInstance().newEditable("")
                } else {
                    if (!createJobBinding.totalAmount.text.toString().contains(getCurrencySymbol()!!) && p0.toString().trim() != "") {
                        createJobBinding.totalAmount.text = Editable.Factory.getInstance().newEditable("(${getCurrencySymbol()}) $p0")
                        createJobBinding.totalAmount.setSelection(createJobBinding.totalAmount.text.toString().length)
                    }

                }
            }
        })
        createJobBinding.submitButton.setOnClickListener {
            if (checkValidation()) {
                createJobBinding.submitButton.startAnimation()
                val postJobPostDto = PostJobPostDto()
                postJobPostDto.jobAmount = createJobBinding.totalAmount.text.toString().split(" ")[1].toFloat().toString()
                postJobPostDto.jobDescription = createJobBinding.postInfo.text.toString()
                postJobPostDto.jobTime = createJobBinding.mintsTextView.text.toString()
                Handler().postDelayed({
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("postValue", postJobPostDto)
                    startActivity(intent)
                }, 1000)
            }
        }

        createJobBinding.closeIcon.setOnClickListener {
            finish()
        }
        createJobBinding.plusIcon.setOnClickListener {
            if (counting < 170) {
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
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().post_description_is_mandatory
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            createJobBinding.totalAmount.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().total_amount_is_mandatory
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            createJobBinding.totalAmount.text.toString().split(" ")[1].toFloat() == 0.0f -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().total_amount_should_be_more_than_0
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
        createJobBinding.submitButton.revertAnimation()
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()

    }
}