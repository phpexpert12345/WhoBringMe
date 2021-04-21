package com.phpexpert.bringme.ui.delivery.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.ChangePasswordActivity
import com.phpexpert.bringme.activities.LoginActivity
import com.phpexpert.bringme.activities.delivery.DashboardActivity
import com.phpexpert.bringme.activities.delivery.UploadDocumentSelectActivity
import com.phpexpert.bringme.activities.employee.ProfileEditActivity
import com.phpexpert.bringme.databinding.DeliveryProfileFragmentBinding

class ProfileFragment : Fragment() {
    private var mViewModel: ProfileViewModel? = null
    private lateinit var profileFragmentBinding: DeliveryProfileFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        profileFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.delivery_profile_fragment, container, false)
        setActions()
        return profileFragmentBinding.root
    }

    private fun setActions() {
        profileFragmentBinding.editImage.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileEditActivity::class.java))
        }
        profileFragmentBinding.documentUploadLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), UploadDocumentSelectActivity::class.java))
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
}