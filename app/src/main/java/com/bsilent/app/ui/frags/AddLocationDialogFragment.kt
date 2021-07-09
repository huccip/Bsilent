package com.bsilent.app.ui.frags

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bsilent.app.R
import com.bsilent.app.database.entities.Place
import com.bsilent.app.databinding.FragmentAddLocationDialogBinding
import com.bsilent.app.viewmodels.AddLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddLocationDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mapView: MapView

    private val viewModel: AddLocationViewModel by activityViewModels()

    private var _binding: FragmentAddLocationDialogBinding? = null
    private val binding: FragmentAddLocationDialogBinding get() = _binding!!

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLocationDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap(savedInstanceState)
        setupViews()
    }
    private fun setupViews() {
        binding.apply {
            cancel.setOnClickListener {
                dismiss()
            }
            val p = viewModel.radius.value ?: 70
            radiusSeekbar.progress = p
            radiusNum.text = "${p}m"
            radiusSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val progress = radiusSeekbar.progress
                    viewModel.radius.value = progress
                    radiusNum.text = "${progress}m"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })
            if (viewModel.isSilent.value == true) {
                modeGrp.check(R.id.mode_silent)
            } else {
                modeGrp.check(R.id.mode_viber)
            }
            modeSilent.setOnCheckedChangeListener { _, b ->
                viewModel.isSilent.value = b
            }

            titleEt.setText(viewModel.placeName.value)
            titleEt.doOnTextChanged { text, start, before, count ->
                viewModel.placeName.value = text.toString()
                titleIL.error = null
            }

            save.setOnClickListener {
                if(titleEt.text?.toString().isNullOrEmpty()){
                    titleEt.error = getString(R.string.field_required)
                }else {
                    map.snapshot {
                        viewModel.savePlace(requireContext(),it)
                    }
                }
            }

        }
    }

    private fun setupMap(bundle: Bundle?) {
        mapView = MapView(requireContext())
        mapView.onCreate(bundle)
        binding.mapContainer.addView(mapView)
        mapView.getMapAsync { gMap ->
            map = gMap

            map.uiSettings.setAllGesturesEnabled(false)

            viewModel.latlng.observe(viewLifecycleOwner) {
                map.clear()
                map.addMarker(MarkerOptions().position(it))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            }

            viewModel.radius.observe(viewLifecycleOwner) { r ->
                map.clear()
                viewModel.latlng.value?.let {
                    map.addMarker(MarkerOptions().position(it))
                }

                map.addCircle(
                    CircleOptions().center(viewModel.latlng.value)
                        .radius(r.toDouble())
                        .fillColor(requireContext().getColor(R.color.accentTransparent))
                        .strokeWidth(2.5f)
                        .strokeColor(requireContext().getColor(R.color.accent))
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        _binding = null

    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}