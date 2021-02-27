package com.bsilent.app.ui.activities


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bsilent.app.R
import com.bsilent.app.adapters.PagerAdapter
import com.bsilent.app.database.AppDatabase
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.databinding.ActivityMainBinding
import com.bsilent.app.ui.frags.PlacesFragment
import com.bsilent.app.ui.frags.ScheduleFragment
import com.bsilent.app.viewmodels.ScheduleModelFactory
import com.bsilent.app.viewmodels.ScheduleViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var frags: List<Fragment>
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupTabs()
        //todo delete this
        val factory = ScheduleModelFactory(
            AppDatabase.getInstance(this).scheduleDao,
            application
        )
        viewModel = ViewModelProvider(this, factory).get(ScheduleViewModel::class.java)
    }

    private fun setupTabs() {
        frags = listOf(
            PlacesFragment(),
            ScheduleFragment()
        )
        binding.viewpager.adapter = PagerAdapter(this, frags)
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.locationFab.apply {
                        setImageResource(R.drawable.ic_add_location)
                        setOnClickListener {
                            startAddLocationActivity()
                        }
                    }
                } else {
                    binding.locationFab.apply {
                        setImageResource(R.drawable.ic_add_alarm)
                        setOnClickListener {
                            showAddScheduleDialog()
                        }
                    }
                }

            }
        })
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, pos ->
            if (pos == 0) {
                tab.apply {
                    text = getString(R.string.places)
                    icon = getDrawable(R.drawable.ic_location)
                }
            } else {
                tab.apply {
                    text = getString(R.string.schedules)
                    icon = getDrawable(R.drawable.ic_time)
                }
            }
        }.attach()
    }

    private fun showAddScheduleDialog() {
        viewModel.insert(
            Schedule(
                endTime = 213128,
                isEnabled = false
            )
        )
    }

    private fun startAddLocationActivity() {
        Intent(this, PickerActivity::class.java).also {
            startActivity(it)
        }
    }


}