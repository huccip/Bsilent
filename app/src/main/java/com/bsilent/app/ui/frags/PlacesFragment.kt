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
import com.bsilent.app.viewmodels.MainViewModelFactory
import com.bsilent.app.viewmodels.PlacesViewModel

class PlacesFragment : Fragment() {

    private lateinit var viewModel: PlacesViewModel

    private lateinit var places: List<Place>
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

        val mainViewModelFactory = MainViewModelFactory(
            AppDatabase.getInstance(requireContext().applicationContext).placesDao,
            requireNotNull(activity).application
        )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(PlacesViewModel::class.java)

        setupRv()

    }

    private fun setupRv() {
        places = listOf()

        viewModel.places.observe(this, Observer {
            places = it
            adapter.notifyDataSetChanged()

            if (places.isEmpty()) {
                showEmptyView()
            } else {
                hideEmptyView()
                if (viewModel.isActivated()) {
                    showTurnOnView()
                } else {
                    hideTurnOnView()
                }
            }
        })

        adapter = PlacesAdapter(places)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}