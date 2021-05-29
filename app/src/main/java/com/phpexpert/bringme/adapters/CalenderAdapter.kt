package com.phpexpert.bringme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutCommentCellBinding
import com.phpexpert.bringme.dtos.CommentCalenderDto
import com.phpexpert.bringme.ui.delivery.myjob.MyJobFragment
import java.util.*
import kotlin.collections.ArrayList

class CalenderAdapter(
        private var context: Context,
        private var commentCalenderView: MyJobFragment
) :
        RecyclerView.Adapter<CalenderAdapter.CanceledShipmentViewHolder>() {

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

        /*if (position % 7 == 0) {
            commentCellBinding.weakTv.visibility = View.VISIBLE
            val calender = Calendar.getInstance()
            calender.set(Calendar.YEAR, selectedYearGlobal)
            calender.set(Calendar.MONTH, selectedMonthGlobal)
            calender.set(Calendar.DAY_OF_MONTH, position + 1)
            commentCellBinding.weakTv.text = (calender.get(Calendar.WEEK_OF_YEAR) + 1).toString()
        } else commentCellBinding.weakTv.visibility = View.GONE*/

       /* if (position / 7 == calenderInstance.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
            commentCellBinding.weekLayout.visibility = View.GONE
        } else {
            commentCellBinding.weekLayout.visibility = View.VISIBLE
        }*/

        /*if (arrayListDate[position].dotVisible)
            commentCellBinding.eventDotIv.visibility = View.VISIBLE
        else
            commentCellBinding.eventDotIv.visibility = View.INVISIBLE*/

        /*if (selectedPosition==position){
            (holder.viewBinding as LayoutCommentCellBinding).dateCalender.setBackgroundResource(R.drawable.event_dot_drawable)
        }else{
            (holder.viewBinding as LayoutCommentCellBinding).dateCalender.setBackgroundResource(R.drawable.event_dot_drawable_white)
        }*/
        commentCellBinding.root.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
//            (holder.viewBinding as LayoutCommentCellBinding).dateCalender.setBackgroundResource(R.drawable.event_dot_drawable)
//            commentCalenderView.setCommentListAdapter(arrayListDate[position].date.toInt())
        }
    }

    fun printDate(
            selectedMonth: Int,
            selectedYear: Int
    ) {
        arrayListDate.clear()
        selectedMonthGlobal = selectedMonth
        selectedYearGlobal = selectedYear
        calenderInstance = Calendar.getInstance()
        calenderInstance.set(Calendar.YEAR, selectedYear)
        calenderInstance.set(Calendar.MONTH, selectedMonth)
        if (calenderInstance.get(Calendar.DAY_OF_WEEK) != 7) {
            for (i in 0 until calenderInstance.get(Calendar.DAY_OF_WEEK)) {
                val commentCalenderDto = CommentCalenderDto()
                commentCalenderDto.date = ""
                commentCalenderDto.dotVisible = false
                arrayListDate.add(commentCalenderDto)
            }
        }
        for (i in 1..calenderInstance.getActualMaximum(Calendar.DATE)) {
            /*val commentData: ArrayList<ReportDto> = if ((selectedMonth + 1) < 10) {
                if (i < 10)
                    ReportPaymentDatabase(context).getAllData("$selectedYear-0${selectedMonth + 1}-0$i")
                else
                    ReportPaymentDatabase(context).getAllData("$selectedYear-0${selectedMonth + 1}-$i")
            } else {
                if (i < 10)
                    ReportPaymentDatabase(context).getAllData("$selectedYear-${selectedMonth + 1}-0$i")
                else
                    ReportPaymentDatabase(context).getAllData("$selectedYear-${selectedMonth + 1}-$i")
            }*/
            val commentCalenderDto = CommentCalenderDto()
            commentCalenderDto.date = "$i"
            commentCalenderDto.dotVisible = /*!commentData.isNullOrEmpty()*/false
            arrayListDate.add(commentCalenderDto)
        }
        notifyDataSetChanged()
    }
}