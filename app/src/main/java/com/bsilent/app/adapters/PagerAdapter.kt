package com.bsilent.app.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    fragmentActivity: FragmentActivity,
    private var frags: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = frags.size

    override fun createFragment(position: Int): Fragment {
        return frags[position]
    }
}