package com.bsilent.app.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.databinding.ScheduleItemBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(var schedules:List<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder>() {

    inner class ScheduleHolder(var binding: ScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder =
        ScheduleHolder(
            ScheduleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int {
        Log.e("testcount","${schedules.size}")
        return schedules.size
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        val schedule = schedules[position]
        Log.e("testbind","$itemCount")
        holder.binding.apply {
            timeSwith.isChecked = schedule.isEnabled

            startTimeTv.text = timeToString(schedule.startTime)
            endTimeTv.text = timeToString(schedule.endTime)
            //todo change this
            repeatTv.text = " Every day"


        }

    }

    private fun timeToString(time:Long) = SimpleDateFormat("HH:mm").format(Date(time))

}