@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutCommentCellBinding
import com.phpexpert.bringme.databinding.TransactionFilterLayoutBinding
import com.phpexpert.bringme.dtos.CommentCalenderDto
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Calender12AdapterTransaction(
        private var context: Context,
        private var commentCalenderView: TransactionFilterLayoutBinding
) :
        RecyclerView.Adapter<Calender12AdapterTransaction.CanceledShipmentViewHolder>() {

    private lateinit var commentCellBinding: LayoutCommentCellBinding
    private var arrayListDate = ArrayList<CommentCalenderDto>()
    private var calenderInstance = Calendar.getInstance()
    private var selectedYearGlobal: Int = -1
    private var selectedMonthGlobal: Int = -1
    private var selectedPosition:Int = -1

    class CanceledShipmentViewHolder(var viewBinding: ViewDataBinding) :
            RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanceledShipmentViewHolder {
        commentCellBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_comment_cell,
                parent,
                false
        )
        return (CanceledShipmentViewHolder(commentCellBinding))
    }

    override fun getItemCount(): Int {
        return arrayListDate.size
    }

    override fun onBindViewHolder(holder: CanceledShipmentViewHolder, position: Int) {
        commentCellBinding = holder.viewBinding as LayoutCommentCellBinding
        commentCellBinding.dateCalender.text = arrayListDate[position].date

        if (selectedPosition == position) {
            (holder.viewBinding as LayoutCommentCellBinding).dateCalender.setTextColor(context.resources.getColor(R.color.white))
            (holder.viewBinding as LayoutCommentCellBinding).dateCalender.setBackgroundResource(R.drawable.date_oval)
        } else {
            (holder.viewBinding as LayoutCommentCellBinding).dateCalender.setTextColor(context.resources.getColor(R.color.text_color))
            (holder.viewBinding as LayoutCommentCellBinding).dateCalender.setBackgroundColor(context.resources.getColor(R.color.white))
        }
        commentCellBinding.root.setOnClickListener {
            if (arrayListDate[position].date != "") {
                selectedPosition = position
                commentCalenderView.endDate.text = Editable.Factory.getInstance().newEditable(changeDateTime("$selectedYearGlobal-${selectedMonthGlobal + 1}-${arrayListDate[position].date}"))
                commentCalenderView.calender2Layout.visibility = View.GONE
                notifyDataSetChanged()
            }
        }
    }
    fun printDate(
            selectedMonth: Int,
            selectedYear: Int,
            selectedFullDate: String
    ) {
        arrayListDate.clear()
        selectedPosition = -1
        selectedMonthGlobal = selectedMonth
        selectedYearGlobal = selectedYear
        calenderInstance = Calendar.getInstance()
        calenderInstance.set(Calendar.YEAR, selectedYear)
        calenderInstance.set(Calendar.MONTH, selectedMonth)
        if (calenderInstance.get(Calendar.DAY_OF_WEEK) -1 != 7) {
            for (i in 0 until (calenderInstance.get(Calendar.DAY_OF_WEEK) - 1) % 7) {
                val commentCalenderDto = CommentCalenderDto()
                commentCalenderDto.date = ""
                commentCalenderDto.dotVisible = false
                arrayListDate.add(commentCalenderDto)
            }
        }
        for (i in 1..calenderInstance.getActualMaximum(Calendar.DATE)) {
            val commentCalenderDto = CommentCalenderDto()
            commentCalenderDto.date = "$i"
            commentCalenderDto.dotVisible = false
            if (selectedFullDate == "") {
                selectedPosition = -1
            } else {
                val splitDate = changeDateTime2(selectedFullDate)?.split("-")!!
                if (selectedYear == splitDate[0].toInt()) {
                    if (selectedMonth == (splitDate[1].toInt() -1))
                        if (i == splitDate[2].toInt())
                            selectedPosition = i + ((calenderInstance.get(Calendar.DAY_OF_WEEK) + 1) % 7)
                }
            }
            arrayListDate.add(commentCalenderDto)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeDateTime(dateTime: String): String? {
        val inputPattern = "yyyy-M-dd"
        val outputPattern = "dd MMM yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        val date: Date?
        var str: String?

        try {
            date = inputFormat.parse(dateTime)
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            str = dateTime
        }

        return str
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeDateTime2(dateTime: String): String? {
        val inputPattern = "dd MMM yyyy"
        val outputPattern = "yyyy-MM-dd"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        val date: Date?
        var str: String?

        try {
            date = inputFormat.parse(dateTime)
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            str = dateTime
        }

        return str
    }
}