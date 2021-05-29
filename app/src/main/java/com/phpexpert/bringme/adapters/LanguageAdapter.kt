package com.phpexpert.bringme.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutChangeLanguageCellBinding
import com.phpexpert.bringme.databinding.LayoutNotificationCellBinding
import com.phpexpert.bringme.dtos.LanguageListList
import com.phpexpert.bringme.dtos.NotificationDtoList
import com.phpexpert.bringme.interfaces.LanguageInterface
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LanguageAdapter(var context: Context, var arrayList: ArrayList<LanguageListList>, var languageInterface: LanguageClick) : RecyclerView.Adapter<LanguageAdapter.NotificationFragmentViewHolder>() {

    private lateinit var notificationFragmentCellBinding: LayoutChangeLanguageCellBinding
    private var selectPosition: Int = -1

    interface LanguageClick {
        fun onclick(position: Int)
    }

    inner class NotificationFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationFragmentViewHolder {
        return NotificationFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_change_language_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationFragmentViewHolder, position: Int) {
        notificationFragmentCellBinding = holder.viewBinding as LayoutChangeLanguageCellBinding
        Glide.with(context).load(arrayList[position].lang_icon).into(notificationFragmentCellBinding.countryIcon)
        if ((context as BaseActivity).sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage).equals(arrayList[position].lang_code, true))
            selectPosition = position
        if (position == selectPosition) {
            notificationFragmentCellBinding.selectImage.setBackgroundResource(R.drawable.dot_selected)
        } else {
            notificationFragmentCellBinding.selectImage.setBackgroundResource(R.drawable.dot_unselected)
        }

        notificationFragmentCellBinding.countryName.text = arrayList[position].lang_name
        notificationFragmentCellBinding.root.setOnClickListener {
            selectPosition = 1
            languageInterface.onclick(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}