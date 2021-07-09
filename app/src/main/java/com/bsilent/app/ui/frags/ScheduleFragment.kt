package com.bsilent.app.ui.frags

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsilent.app.R
import com.bsilent.app.adapters.ScheduleAdapter
import com.bsilent.app.databinding.ScheduleFragmentBinding
import com.bsilent.app.viewmodels.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private val viewModel: ScheduleViewModel by activityViewModels()

    private var _binding: ScheduleFragmentBinding? = null
    private val binding: ScheduleFragmentBinding get() = _binding!!

    private lateinit var adapter: ScheduleAdapter

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScheduleFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        setHasOptionsMenu(true)
        initMenu()
    }

    private fun initMenu() {
        viewModel.selected.observe(viewLifecycleOwner){
                setMenuItemsVisibility(it.isNotEmpty())
        }
    }

    private fun setMenuItemsVisibility(notEmpty: Boolean) {
        menu?.let {
            it.findItem(R.id.menu_delete).isVisible = notEmpty
            it.findItem(R.id.menu_delete_all).isVisible = notEmpty
            requireActivity().invalidateOptionsMenu()
        }
    }

    private fun initRv() {
        adapter = ScheduleAdapter(viewModel)
        viewModel.schedules.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
            }
        }

        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScheduleFragment.adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.schedule_menu,menu)
        this.menu = menu

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        setMenuItemsVisibility(!viewModel.selected.value.isNullOrEmpty())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete -> viewModel.deleteSelected(requireContext())
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onPause() {
        super.onPause()
        viewModel.clearSelected()
        adapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.rv.visibility = View.GONE
        binding.empty.visibility = View.VISIBLE
    }

    private fun hideEmpty() {
        binding.rv.visibility = View.VISIBLE
        binding.empty.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}