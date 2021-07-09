package com.bsilent.app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bsilent.app.R
import com.bsilent.app.database.entities.ScheduleWithDays
import com.bsilent.app.databinding.ScheduleItemBinding
import com.bsilent.app.ui.dialogs.AddScheduleDialog
import com.bsilent.app.utils.TimeUtils.daysListToStr
import com.bsilent.app.utils.TimeUtils.timeToString
import com.bsilent.app.viewmodels.ScheduleViewModel
import java.util.*

class ScheduleAdapter(
    var viewModel: ScheduleViewModel
) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder>() {

    private var differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<ScheduleWithDays>() {

            override fun areItemsTheSame(
                oldItem: ScheduleWithDays,
                newItem: ScheduleWithDays
            ): Boolean =
                oldItem.schedule.id == newItem.schedule.id

            override fun areContentsTheSame(
                oldItem: ScheduleWithDays,
                newItem: ScheduleWithDays
            ): Boolean = oldItem==newItem

        }
    )

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
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        val schedule = differ.currentList[position]

        holder.binding.apply {

            selectView.visibility =
                if (viewModel.selected.value?.contains(schedule.schedule.id) == true) View.VISIBLE
                else View.GONE

            timeSwith.isChecked = schedule.schedule.isEnabled

            timeSwith.setOnCheckedChangeListener { _, b ->
                viewModel.updateEnable(schedule.apply { this.schedule.isEnabled = b }, root.context)
            }

            startTimeTv.text = timeToString(schedule.schedule.hour, schedule.schedule.min)
            endTimeTv.text = timeToString(
                schedule.schedule.hour, schedule.schedule.min, schedule.schedule.duration
            )

            repeatTv.text = daysListToStr(schedule.days, root.context)

            modeImg.setImageResource(
                if (schedule.schedule.silent) R.drawable.ic_silent else R.drawable.ic_vibration
            )

            title.text = if (schedule.schedule.name.isNotEmpty())
                schedule.schedule.name.capitalize(Locale.getDefault()) else root.context.getString(R.string.no_name)

            root.setOnClickListener {

                if (!viewModel.selected.value.isNullOrEmpty()) {
                    viewModel.addOrRemoveSelected(schedule)
                    notifyItemChanged(position)

                } else {
                    AddScheduleDialog(root.context, schedule) {
                        viewModel.update(it, schedule, root.context)
                    }.show()
                }
            }
            root.setOnLongClickListener {
                viewModel.addOrRemoveSelected(schedule)
                notifyItemChanged(position)
                true
            }

        }

    }

    fun submitList(schedules: List<ScheduleWithDays>) = differ.submitList(schedules)
}