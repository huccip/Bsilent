package com.bsilent.app.ui.frags

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsilent.app.adapters.PlacesAdapter
import com.bsilent.app.database.AppDatabase
import com.bsilent.app.database.entities.Place
import com.bsilent.app.databinding.PlacesFragmentBinding
import com.bsilent.app.viewmodels.PlaceViewModelFactory
import com.bsilent.app.viewmodels.PlacesViewModel

class PlacesFragment : Fragment() {

    private lateinit var viewModel: PlacesViewModel

    private lateinit var adapter: PlacesAdapter

    //binding
    private var _binding: PlacesFragmentBinding? = null
    private val binding: PlacesFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PlacesFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mainViewModelFactory = PlaceViewModelFactory(
            AppDatabase.getInstance(requireContext().applicationContext).placesDao,
            requireNotNull(activity).application
        )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(PlacesViewModel::class.java)

        setupRv()
        setupSwitch()
    }

    private fun setupSwitch() {
        viewModel.isEnabled.observe(this, Observer {
            if(adapter.places.isNotEmpty()){
                if(it){
                    hideTurnOnView()
                }else{
                    showTurnOnView()
                }
            }
        })
        binding.switchLoc.setOnCheckedChangeListener { _, b ->
            if(b){
                viewModel.enableAll()
            }else{
                viewModel.disableAll()
            }

        }
    }

    private fun setupRv() {
        adapter = PlacesAdapter()
        viewModel.places.observe(this, Observer {
            adapter.places = it
            adapter.notifyDataSetChanged()

            if (adapter.places.isEmpty()) {
                showEmptyView()
            } else {
                hideEmptyView()
            }
        })

        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlacesFragment.adapter
        }

    }

    private fun showTurnOnView() {
        binding.turnedOffLayout.visibility = View.VISIBLE
        binding.rv.visibility = View.GONE
    }

    private fun hideTurnOnView() {
        binding.turnedOffLayout.visibility = View.GONE
        binding.rv.visibility = View.VISIBLE
    }

    private fun showEmptyView() {
        binding.empty.visibility = View.VISIBLE
        binding.main.visibility = View.GONE

    }

    private fun hideEmptyView() {
        binding.empty.visibility = View.GONE
        binding.main.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        activity?.menuInflater.inflate(R.menu.)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}