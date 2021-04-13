package com.phpexpert.bringme.ui.employee.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.employee.ProfileEditActivity
import com.phpexpert.bringme.databinding.EmployeeProfileFragmentBinding

class ProfileFragment : Fragment() {
    private var mViewModel: ProfileViewModel? = null
    private lateinit var profileFragmentBinding: EmployeeProfileFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        profileFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.employee_profile_fragment, container, false)
        setActions()
        return profileFragmentBinding.root
    }

    private fun setActions(){
        profileFragmentBinding.editImage.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileEditActivity::class.java))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }
}