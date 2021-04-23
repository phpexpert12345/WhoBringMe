package com.phpexpert.bringme.ui.employee.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.ChangePasswordActivity
import com.phpexpert.bringme.activities.LoginActivity
import com.phpexpert.bringme.activities.employee.DashboardActivity
import com.phpexpert.bringme.activities.employee.ProfileEditActivity
import com.phpexpert.bringme.databinding.EmployeeProfileFragmentBinding
import com.phpexpert.bringme.utilities.BaseActivity

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private lateinit var profileFragmentBinding: EmployeeProfileFragmentBinding

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        profileFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.employee_profile_fragment, container, false)
        setActions()

        return profileFragmentBinding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setActions() {
        profileFragmentBinding.editImage.setOnClickListener {
            if ((activity as BaseActivity).isCheckPermissions(requireActivity(), perission)) {
                startActivity(Intent(requireActivity(), ProfileEditActivity::class.java))
            }
        }

        profileFragmentBinding.changeMPinLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
        }

        profileFragmentBinding.logoutLayout.setOnClickListener {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = "Are you sure you want to logout"
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Yes"
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.text = "No"
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.VISIBLE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as DashboardActivity).bottomSheetDialog.dismiss()
                (activity as DashboardActivity).sharedPrefrenceManager.clearData()
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finishAffinity()
            }
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.setOnClickListener {
                (activity as DashboardActivity).bottomSheetDialog.dismiss()
            }
            (activity as DashboardActivity).bottomSheetDialog.show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setObserver()
        ProfileViewModel.changeModel.postValue(false)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        ProfileViewModel.getChangeModel().observe(viewLifecycleOwner, {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(this).load((activity as BaseActivity).sharedPrefrenceManager.getProfile().login_photo)
                    .apply(requestOptions)
                    .into(profileFragmentBinding.userImage)

            profileFragmentBinding.userName.text = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_name
            profileFragmentBinding.userEmailTV.text = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_email
            profileFragmentBinding.userMobileNo.text = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_phone_code + (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_phone
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(requireActivity(), ProfileEditActivity::class.java))
                } else {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = "Please allow all permission"
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Pk"
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as DashboardActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as DashboardActivity).bottomSheetDialog.show()
                }
            }
        }
    }
}