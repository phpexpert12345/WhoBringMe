package com.phpexpert.bringme.ui.employee.home

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.HomeFragmentCellBinding
import com.phpexpert.bringme.dtos.OrderListData
import java.lang.Exception

class HomeFragmentAdapter(var context: Context, var arrayList: ArrayList<OrderListData>, var onClickListener: OnClickView) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>() {

    private lateinit var homeFragmentCellBinding: HomeFragmentCellBinding

    inner class HomeFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)
    interface OnClickView {
        fun onClick(textInput: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        return HomeFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.home_fragment_cell, parent, false))
    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        homeFragmentCellBinding = holder.viewBinding as HomeFragmentCellBinding
        try {
            arrayList[position].job_total_amount = String.format("%.2f", arrayList[position].job_total_amount?.toFloat())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        homeFragmentCellBinding.model = arrayList[position]
        if (arrayList[position].order_status_msg == "Accepted") {
            homeFragmentCellBinding.csImage.setImageResource(R.drawable.cs1)
        } else {
            homeFragmentCellBinding.csImage.setImageResource(R.drawable.cs)
        }
        try {
            homeFragmentCellBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        homeFragmentCellBinding.viewJobImageView.setOnClickListener {
            onClickListener.onClick("viewData", position)
        }

        homeFragmentCellBinding.writeReview.setOnClickListener {
            onClickListener.onClick("writeReview", position)
        }
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
        Glide.with(context).load(arrayList[position].Client_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(homeFragmentCellBinding.userImage)

        Glide.with(context)
                .load(arrayList[position].Delivery_Employee_photo)
                .placeholder(R.drawable.user_placeholder)
                .apply(requestOptions)
                .into(homeFragmentCellBinding.deliveryImageView)

        if (arrayList[position].job_rating!! != "") {
            homeFragmentCellBinding.ratingValue.rating = arrayList[position].job_rating!!.toFloat()
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}