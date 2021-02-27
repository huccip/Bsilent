package com.bsilent.app.ui.frags

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsilent.app.R
import com.bsilent.app.adapters.ScheduleAdapter
import com.bsilent.app.database.AppDatabase
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.databinding.ScheduleFragmentBinding
import com.bsilent.app.viewmodels.ScheduleModelFactory
import com.bsilent.app.viewmodels.ScheduleViewModel

class ScheduleFragment : Fragment() {

    private lateinit var viewModel: ScheduleViewModel

    private var _binding:ScheduleFragmentBinding? = null
    private val binding:ScheduleFragmentBinding get() = _binding!!

    private lateinit var adapter:ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScheduleFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = ScheduleModelFactory(AppDatabase.getInstance(requireContext()).scheduleDao,
            requireNotNull(activity).application)
        viewModel = ViewModelProvider(this,factory).get(ScheduleViewModel::class.java)

        setupRv()
    }

    private fun setupRv() {
        adapter = ScheduleAdapter(listOf())
        viewModel.schedules.observe(this, Observer {
            adapter.schedules = it
            adapter.notifyDataSetChanged()
            if(it.isEmpty()){
                showEmpty()
            }else{
                hideEmpty()
            }
        })

        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScheduleFragment.adapter
        }
    }

    private fun showEmpty(){
        binding.rv.visibility = View.GONE
        binding.empty.visibility = View.VISIBLE
    }

    private fun hideEmpty(){
        binding.rv.visibility = View.VISIBLE
        binding.empty.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}