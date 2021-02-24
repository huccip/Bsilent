package com.bsilent.app.ui.activities


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bsilent.app.R
import com.bsilent.app.adapters.PagerAdapter
import com.bsilent.app.databinding.ActivityMainBinding
import com.bsilent.app.ui.frags.PlacesFragment
import com.bsilent.app.ui.frags.ScheduleFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var frags: List<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupTabs()
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
                    binding.locationFab.apply{
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
        TODO("Not yet implemented")
    }

    private fun startAddLocationActivity() {
        TODO("Not yet implemented")
    }


}