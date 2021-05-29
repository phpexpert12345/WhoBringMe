package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.adapters.LanguageAdapter
import com.phpexpert.bringme.databinding.LayoutChangeLanguageBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.LanguageListList
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.interfaces.LanguageInterface
import com.phpexpert.bringme.models.AuthModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS

class ChangeLanguageActivity : BaseActivity(), AuthInterface, LanguageAdapter.LanguageClick, LanguageInterface {

    private lateinit var changePasswordActivity: LayoutChangeLanguageBinding
    private lateinit var changePasswordViewModel: AuthModel
    private lateinit var languageDtoData: LanguageDtoData
    private lateinit var arrayList: ArrayList<LanguageListList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordActivity = DataBindingUtil.setContentView(this, R.layout.layout_change_language)
        changePasswordActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        changePasswordViewModel = ViewModelProvider(this).get(AuthModel::class.java)
        languageDtoData = sharedPrefrenceManager.getLanguageData()

        arrayList = ArrayList()
        changePasswordActivity.languageRv.layoutManager = LinearLayoutManager(this)
        changePasswordActivity.languageRv.isNestedScrollingEnabled = false
        changePasswordActivity.languageRv.adapter = LanguageAdapter(this, arrayList, this)
        setObserver()
        changePasswordActivity.continueButton.setOnClickListener {
            changePasswordActivity.continueButton.startAnimation()
            hitLanguageApi(this)
        }

        changePasswordActivity.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun setObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                changePasswordActivity.continueButton.revertAnimation()
                changePasswordViewModel.getLanguageList(mapData()).observe(this, {
                    if (it.status_code == "2") {
                        hitAuthApi(this)
                    } else {
                        changePasswordActivity.continueButton.revertAnimation()
                        arrayList.clear()
                        arrayList.addAll(it.LanguageListList)
                        changePasswordActivity.languageRv.adapter?.notifyDataSetChanged()
                    }
                })
            } else {
                hitAuthApi(this)
            }
        } else {
            changePasswordActivity.continueButton.revertAnimation()
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun mapData(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataValue["lang_cone"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapDataValue
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            setObserver()
        } else {
            changePasswordActivity.continueButton.revertAnimation()
            bottomSheetDialogMessageText.text = message
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    override fun onclick(position: Int) {
        sharedPrefrenceManager.savePrefrence(CONSTANTS.changeLanguage, arrayList[position].lang_code)
    }

    override fun getLanguageData(boolean: Boolean, message: String) {
        if (boolean) {
            if (sharedPrefrenceManager.getProfile().account_type == "Client / Receiver" || sharedPrefrenceManager.getProfile().account_type == "1") {
                val intent = Intent(this@ChangeLanguageActivity, com.phpexpert.bringme.activities.employee.DashboardActivity::class.java)
                startActivity(intent)
                finishAffinity()
            } else {
                val intent = Intent(this@ChangeLanguageActivity, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java)
//                    val intent = Intent(this@SplashActivity, TransactionActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        } else {
            changePasswordActivity.continueButton.revertAnimation()
            bottomSheetDialogMessageText.text = message
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

}