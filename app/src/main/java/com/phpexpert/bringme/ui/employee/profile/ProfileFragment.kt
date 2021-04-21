package com.phpexpert.bringme.ui.employee.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.ChangePasswordActivity
import com.phpexpert.bringme.activities.LoginActivity
import com.phpexpert.bringme.activities.delivery.UploadDocumentSelectActivity
import com.phpexpert.bringme.activities.employee.DashboardActivity
import com.phpexpert.bringme.activities.employee.ProfileEditActivity
import com.phpexpert.bringme.databinding.EmployeeProfileFragmentBinding
import com.phpexpert.bringme.utilities.BaseActivity

class ProfileFragment : Fragment() {
    private var mViewModel: ProfileViewModel? = null
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
            (activity as DashboardActivity).sharedPrefrenceManager.clearData()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }
    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return requireActivity().checkSelfPermission(permission) === PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

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
                    Toast.makeText(requireActivity(), "Please allow all permission", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}