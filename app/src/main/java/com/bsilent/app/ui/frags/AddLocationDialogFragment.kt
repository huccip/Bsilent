package com.bsilent.app.ui.frags

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bsilent.app.R
import com.bsilent.app.database.AppDatabase
import com.bsilent.app.databinding.FragmentAddLocationDialogBinding
import com.bsilent.app.viewmodels.AddLocationViewModel
import com.bsilent.app.viewmodels.PlaceViewModelFactory
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddLocationDialogFragment : BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var viewModel: AddLocationViewModel

    private var _binding: FragmentAddLocationDialogBinding? = null
    private val binding: FragmentAddLocationDialogBinding get() = _binding!!

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddLocationDialogBinding.inflate(inflater, container, false)

        setupViewModel()
        setupMap(savedInstanceState)
        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {
            cancel.setOnClickListener {
                dismiss()
            }
            val p = viewModel.radius.value?:70
            radiusSeekbar.progress = p
            radiusNum.text = "${p}m"
            radiusSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
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
            if(viewModel.isSilent.value==true){
                modeGrp.check(R.id.mode_silent)
            }else{
                modeGrp.check(R.id.mode_viber)
            }
            modeSilent.setOnCheckedChangeListener { _, b ->
                viewModel.isSilent.value = b
            }

            titleEt.setText(viewModel.placeName.value)
            titleEt.doOnTextChanged { text, start, before, count ->
                viewModel.placeName.value = text.toString()
            }

            binding.save.setOnClickListener {
                map.snapshot {
                    viewModel.savePlace(it)
                }

            }
        }
    }

    private fun setupMap(bundle: Bundle?) {
        binding.mapView.onCreate(bundle)
        binding.mapView.getMapAsync(this)
    }

    private fun setupViewModel() {
        val factory = PlaceViewModelFactory(
            AppDatabase.getInstance(requireContext().applicationContext).placesDao,
            requireNotNull(activity).application
        )
        viewModel = ViewModelProvider(requireActivity(), factory).get(AddLocationViewModel::class.java)
    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.uiSettings.setAllGesturesEnabled(false)
        viewModel.latlng.observe(this, Observer {
            map.clear()
            map.addMarker(MarkerOptions().position(it))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it,15f))
        })

        viewModel.radius.observe(this, Observer {
            map.clear()
            viewModel.latlng.value?.let {
                map.addMarker(MarkerOptions().position(it))
            }

            map.addCircle(CircleOptions().center(viewModel.latlng.value)
                .radius(it.toDouble())
                .fillColor(R.color.purple_500)
                .strokeWidth(2.5f)
                .strokeColor(R.color.purple_500))
        })
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        _binding = null

    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}