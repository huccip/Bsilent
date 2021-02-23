package com.bsilent.app.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bsilent.app.R
import com.bsilent.app.adapters.PagerAdapter
import com.bsilent.app.databinding.ActivityMainBinding
import com.bsilent.app.ui.frags.PlacesFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var frags:List<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupTabs()
    }

    private fun setupTabs() {
        frags = listOf(
            PlacesFragment(),
            PlacesFragment()
        )
        PagerAdapter(this,frags)
        TabLayoutMediator(binding.tabs,binding.viewpager){ tab,pos ->
            if(pos==0){
                tab.apply {
                    title = getString(R.string.places)
                    icon = getDrawable(R.drawable.ic_location)
                }
            }else{
                tab.apply {
                    title = getString(R.string.schedules)
                    icon = getDrawable(R.drawable.ic_time)
                }
            }
        }.attach()
    }


}