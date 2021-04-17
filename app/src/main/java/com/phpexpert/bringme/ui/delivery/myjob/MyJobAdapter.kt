package com.phpexpert.bringme.ui.delivery.myjob

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutJobCellBinding

class MyJobAdapter(var context: Context, var arrayList: ArrayList<String>) : RecyclerView.Adapter<MyJobAdapter.MyJobViewModel>() {

    private lateinit var jobCellBinding: LayoutJobCellBinding

    inner class MyJobViewModel(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobViewModel {
        return MyJobViewModel(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_job_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyJobViewModel, position: Int) {
        jobCellBinding = holder.viewBinding as LayoutJobCellBinding
        if (position == 0) {
            jobCellBinding.completeCancelStatus.text = "Complete"
            jobCellBinding.completeCancelStatus.setBackgroundResource(R.drawable.job_complete_bg)
            jobCellBinding.orderTimeLayout.visibility = View.GONE
            jobCellBinding.orderTimeView.visibility = View.GONE
            jobCellBinding.chargesLayout.visibility = View.VISIBLE
            jobCellBinding.totalAmount.visibility = View.VISIBLE
            jobCellBinding.orderAmountLayout.visibility = View.GONE
            jobCellBinding.completeCancelText.text = "Complete Time :"
        } else {
            jobCellBinding.completeCancelStatus.text = "Cancel"
            jobCellBinding.completeCancelStatus.setBackgroundResource(R.drawable.job_cancel_bg)
            jobCellBinding.orderTimeLayout.visibility = View.VISIBLE
            jobCellBinding.orderTimeView.visibility = View.VISIBLE
            jobCellBinding.chargesLayout.visibility = View.GONE
            jobCellBinding.totalAmount.visibility = View.GONE
            jobCellBinding.orderAmountLayout.visibility = View.VISIBLE
            jobCellBinding.completeCancelText.text = "Cancel Time :"
        }


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}