package com.phpexpert.bringme.activities.employee

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityCreateJobBinding
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.utilities.BaseActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@Suppress("DEPRECATION")
class CreateJobActivity : BaseActivity() {

    private var counting: Int = 10
    private var formatChange: Boolean = true

    private lateinit var createJobBinding: ActivityCreateJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createJobBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_job)
        createJobBinding.languageModel = sharedPrefrenceManager.getLanguageData()

        createJobBinding.jobTotalAmount.hint = "${sharedPrefrenceManager.getLanguageData().job_total_amount_in} (${getCurrencySymbol()})"
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
                if (p0!!.isNotEmpty() && formatChange) {
                    if (p0.contains(".") || p0.contains(",")) {
                        try {
                            if (p0.split(".")[1].isNotEmpty()) {
                                formatChange = false
                                createJobBinding.totalAmount.text = Editable.Factory.getInstance().newEditable(p0.toString().formatChange())
                                createJobBinding.totalAmount.setSelection(createJobBinding.totalAmount.length())
                            } else {
                                formatChange = true
                            }
                        } catch (e: java.lang.Exception) {

                        }
                    } else {
                        formatChange = true
                    }
                } else {
                    formatChange = true
                }
            }

        })
        createJobBinding.submitButton.setOnClickListener {
            if (checkValidation()) {
//                createJobBinding.submitButton.startAnimation()
                val postJobPostDto = PostJobPostDto()
                this@CreateJobActivity.hideKeyboard()
                postJobPostDto.jobAmount = createJobBinding.totalAmount.text.toString().formatChangeData()
                postJobPostDto.jobDescription = createJobBinding.postInfo.text.toString().trim()
                postJobPostDto.jobTime = createJobBinding.mintsTextView.text.toString()
//                Handler().postDelayed({
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("postValue", postJobPostDto)
                startActivity(intent)
//                }, 1000)
            }
        }

        createJobBinding.backArrow.setOnClickListener {
            finish()
        }

        createJobBinding.postInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().trim() != "") {
                    createJobBinding.maxCharactersLimitData.hint = ""
                    createJobBinding.maxCharactersLimitData.text = "${sharedPrefrenceManager.getLanguageData().characters_limit} ${2000 - (p0.toString().length)}"
                } else
                    createJobBinding.maxCharactersLimitData.text = "${sharedPrefrenceManager.getLanguageData().characters_limit} ${2000 - (p0.toString().length)}"
            }

        })
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

    @SuppressLint("SetTextI18n")
    private fun checkValidation(): Boolean {
        return when {
            createJobBinding.postInfo.text.toString().trim().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().post_description_is_mandatory
                bottomSheetDialogHeadingText.visibility = View.GONE
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
                bottomSheetDialogHeadingText.visibility = View.GONE
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            createJobBinding.totalAmount.text.toString().formatChangeData()?.toFloat()!! < 1f -> {
                bottomSheetDialogMessageText.text = "${sharedPrefrenceManager.getLanguageData().total_amount_should_be_more_than_0} ${getCurrencySymbol()}1."
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogHeadingText.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            createJobBinding.totalAmount.text.toString().formatChangeData()?.toFloat()!! >= 1000000 -> {
                bottomSheetDialogMessageText.text = "${sharedPrefrenceManager.getLanguageData().enter_job_limit_amount} ${getCurrencySymbol()}1000000."
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogHeadingText.visibility = View.GONE
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
        super.onPause()
        createJobBinding.submitButton.revertAnimation()
    }


    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun String?.formatChangeData() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale(sharedPrefrenceManager.getAuthData().lang_code!!, "DE"))
//            formatter.format(this?.toFloat())
            val new = this?.replace(",", ".")
            /*
            val symbols = DecimalFormatSymbols(Locale("en", "IN"))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(new?.toBigDecimalOrNull().toString())*/
            return new
        } catch (e: Exception) {
            this
        }
    }
}