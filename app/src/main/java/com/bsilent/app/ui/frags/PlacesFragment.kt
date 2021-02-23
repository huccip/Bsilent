package com.bsilent.app.ui.frags

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bsilent.app.R
import com.bsilent.app.database.AppDatabase
import com.bsilent.app.database.entities.Place
import com.bsilent.app.databinding.PlacesFragmentBinding
import com.bsilent.app.viewmodels.MainViewModelFactory
import com.bsilent.app.viewmodels.PlacesViewModel

class PlacesFragment : Fragment() {

    private lateinit var viewModel: PlacesViewModel

    private lateinit var places:List<Place>
//    private lateinit var adapter
    //binding
    private var _binding:PlacesFragmentBinding? = null
    private val binding:PlacesFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PlacesFragmentBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mainViewModelFactory = MainViewModelFactory(
            AppDatabase.getInstance(requireContext().applicationContext).placesDao,
            requireNotNull(activity).application
        )
        viewModel = ViewModelProvider(this,mainViewModelFactory).get(PlacesViewModel::class.java)

        viewModel.places.observe(this, Observer {

        })

        setupRv()
    }

    private fun setupRv() {
        binding.rv.apply {

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}