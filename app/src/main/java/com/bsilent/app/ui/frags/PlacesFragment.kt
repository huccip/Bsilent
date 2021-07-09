package com.bsilent.app.ui.frags

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsilent.app.R
import com.bsilent.app.adapters.PlacesAdapter
import com.bsilent.app.databinding.PlacesFragmentBinding
import com.bsilent.app.viewmodels.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlacesFragment : Fragment() {

    private val viewModel: PlacesViewModel by activityViewModels()

    private lateinit var adapter: PlacesAdapter


    private var _binding: PlacesFragmentBinding? = null
    private val binding: PlacesFragmentBinding get() = _binding!!

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlacesFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        initRv()
        initSwitch()
        initMenu()
    }

    private fun initMenu() {
        viewModel.selected.observe(viewLifecycleOwner){
            setMenuDeleteVisibility(!it.isNullOrEmpty())
        }
    }

    private fun setMenuDeleteVisibility(vis: Boolean) {
        menu?.findItem(R.id.menu_delete)?.isVisible = vis
        requireActivity().invalidateOptionsMenu()
    }

    private fun initSwitch() {
        viewModel.isEnabled.observe(viewLifecycleOwner, {
            if (adapter.differ.currentList.isNotEmpty()) {
                setSwitchOnOff(it)
            }
        })
        binding.switchLoc.setOnCheckedChangeListener { s, b ->
            if (b) {
                viewModel.enableAll(requireContext())
                s.text = getString(R.string.on)
            } else {
                viewModel.disableAll(requireContext())
                s.text = getString(R.string.off)
            }

        }
    }

    private fun initRv() {
        adapter = PlacesAdapter(viewModel = viewModel)
        viewModel.places.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            viewModel.isEnabled.value?.let { on ->
                setSwitchOnOff(on)
            }

            if (it.isEmpty()) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.schedule_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        setMenuDeleteVisibility(!viewModel.selected.value.isNullOrEmpty())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete -> viewModel.deleteSelected(requireContext())
        }
        return super.onOptionsItemSelected(item)
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

    private fun setSwitchOnOff(on: Boolean) {
        if (on) {
            binding.switchLoc.apply {
                text = getString(R.string.on)
                isChecked = true
            }
            hideTurnOnView()
        } else {
            binding.switchLoc.apply {
                text = getString(R.string.off)
                isChecked = false
            }
            showTurnOnView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}