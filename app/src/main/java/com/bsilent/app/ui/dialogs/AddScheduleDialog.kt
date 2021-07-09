package com.bsilent.app.ui.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import com.bsilent.app.R
import com.bsilent.app.database.entities.Day
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.database.entities.ScheduleWithDays
import com.bsilent.app.databinding.AddScheduleDialogBinding
import java.text.SimpleDateFormat
import java.util.*

class AddScheduleDialog(
    context: Context,
    private val initData: ScheduleWithDays? = null,
    val save: (ScheduleWithDays) -> Unit
) : Dialog(context) {

    private lateinit var binding: AddScheduleDialogBinding

    private var days = mutableSetOf(1, 2, 3, 4, 5)
    private var time = Calendar.getInstance()

    private var scheduleWithDays = ScheduleWithDays(
        Schedule(
            name = "",
            hour = time.get(Calendar.HOUR_OF_DAY),
            min = time.get(Calendar.MINUTE),
            silent = true,
            duration = 1
        ), days.map {
            Day(day = it)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddScheduleDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * .9f).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupSpinner()
        setupTimePicker()
        setupListeners()
        setInitValues()
    }

    private fun setInitValues() {
        initData?.let { data ->
            scheduleWithDays = data
            binding.apply {
                if (!data.schedule.silent) modeViber.isChecked = true
                titleEt.setText(data.schedule.name)
                hourSpinner.setSelection(data.schedule.duration-1)
                time = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY,data.schedule.hour)
                    set(Calendar.MINUTE,data.schedule.min)
                }
                timeBtn.text = getFormattedTime()
                days = data.days.map {
                    it.day
                }.toMutableSet()

                sun.isChecked = days.contains(0)
                mon.isChecked = days.contains(1)
                turs.isChecked = days.contains(2)
                wedn.isChecked = days.contains(3)
                thurs.isChecked = days.contains(4)
                fri.isChecked = days.contains(5)
                sat.isChecked = days.contains(6)

                add.text = context.getString(R.string.update)
            }

        }
    }

    private fun setupTimePicker() {
        time = Calendar.getInstance()
        binding.timeBtn.apply {
            text = getFormattedTime()

            setOnClickListener {
                TimePickerDialog(context,
                    { _, h, m ->
                        scheduleWithDays.schedule.hour = h
                        scheduleWithDays.schedule.min = m
                        time.set(Calendar.HOUR_OF_DAY, h)
                        time.set(Calendar.MINUTE, m)
                        text = getFormattedTime()

                    }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true
                ).show()
            }
        }
    }

    private fun setupListeners() {
        binding.apply {

            //add / cancel buttons
            cancel.setOnClickListener {
                dismiss()
            }
            add.setOnClickListener {
                val schedId = if(initData!=null) initData.schedule.id?:-1 else -1
                scheduleWithDays.days = days.map {
                    Day(schedId, it)
                }
                save(scheduleWithDays)
                dismiss()
            }

            //radio btns
            radioGrp.setOnCheckedChangeListener { _, i ->
                scheduleWithDays.schedule.silent = when (i) {
                    R.id.mode_silent -> true
                    else -> false
                }
            }

            //days checkboxes
            sun.setOnCheckedChangeListener { _, b ->
                if (b) days.add(0) else days.remove(0)
                add.isEnabled = days.size!=0
            }
            mon.setOnCheckedChangeListener { _, b ->
                if (b) days.add(1) else days.remove(1)
                add.isEnabled = days.size!=0
            }
            turs.setOnCheckedChangeListener { _, b ->
                if (b) days.add(2) else days.remove(2)
                add.isEnabled = days.size!=0
            }
            wedn.setOnCheckedChangeListener { _, b ->
                if (b) days.add(3) else days.remove(3)
                add.isEnabled = days.size!=0
            }
            thurs.setOnCheckedChangeListener { _, b ->
                if (b) days.add(4) else days.remove(4)
                add.isEnabled = days.size!=0
            }
            fri.setOnCheckedChangeListener { _, b ->
                if (b) days.add(5) else days.remove(5)
                add.isEnabled = days.size!=0
            }
            sat.setOnCheckedChangeListener { _, b ->
                if (b) days.add(6) else days.remove(6)
                add.isEnabled = days.size!=0
            }

            //title
            titleEt.doOnTextChanged { text, _, _, _ ->
                scheduleWithDays.schedule.name = text.toString()
            }

        }
    }

    private fun setupSpinner() {
        val hourStr = context.getString(R.string.hour)
        val hourList = mutableListOf<String>()
        for (i in 1..8) hourList.add("$i $hourStr")

        val arrayAdapter = ArrayAdapter(context, R.layout.spinner_item, hourList)
        binding.hourSpinner.apply {
            adapter = arrayAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    scheduleWithDays.schedule.duration = p2 + 1
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun getFormattedTime() = SimpleDateFormat("HH:mm").format(time.timeInMillis)
}