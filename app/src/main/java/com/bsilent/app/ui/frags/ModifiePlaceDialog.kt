package com.bsilent.app.ui.frags

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bsilent.app.R
import com.bsilent.app.database.entities.Place
import com.bsilent.app.databinding.FragmentAddLocationDialogBinding
import com.bsilent.app.viewmodels.PlacesViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

class ModifiePlaceDialog : BottomSheetDialogFragment() {

    private lateinit var mapView: MapView

    private var _binding: FragmentAddLocationDialogBinding? = null
    private val binding: FragmentAddLocationDialogBinding get() = _binding!!

    private lateinit var map: GoogleMap

    private val viewModel: PlacesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLocationDialogBinding.inflate(inflater, container, false)
        setupMap(savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            dialogTitle.text = getString(R.string.modifie_location)
            cancel.setImageResource(R.drawable.ic_delete)
            cancel.setOnClickListener {
                viewModel.delete(requireContext(),viewModel.dialogPlace.value)
                dismiss()
            }
            val p = viewModel.dialogPlace.value?.radius ?: 70
            radiusSeekbar.progress = p
            radiusNum.text = "${p}m"
            radiusSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val progress = radiusSeekbar.progress
                    viewModel.dialogPlace.value?.radius = progress
                    radiusNum.text = "${progress}m"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })
            if (viewModel.dialogPlace.value?.silent == true) {
                modeGrp.check(R.id.mode_silent)
            } else {
                modeGrp.check(R.id.mode_viber)
            }
            modeSilent.setOnCheckedChangeListener { _, b ->
                viewModel.dialogPlace.value?.silent = b
            }

            titleEt.setText(viewModel.dialogPlace.value?.name)
            titleEt.doOnTextChanged { text, _, _, _ ->
                viewModel.dialogPlace.value?.name = text.toString()
                titleIL.error = null
            }

            save.setOnClickListener {
                if(titleEt.text?.toString().isNullOrEmpty()){
                    titleEt.error = getString(R.string.field_required)
                }else{
                    map.snapshot {
                        viewModel.updatePlace(requireContext(),it)
                        dismiss()
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

            viewModel.dialogPlace.value?.let {
                    val latlng = LatLng(it.lat,it.lng)
                    map.clear()
                    map.addMarker(MarkerOptions().position(latlng))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
                }

            viewModel.dialogPlace.observe(viewLifecycleOwner) { place ->
                place?.let {
                    val latlng = LatLng(it.lat,it.lng)
                    map.clear()
                    map.addMarker(MarkerOptions().position(latlng))
                    map.addCircle(
                        CircleOptions().center(latlng)
                            .radius(it.radius.toDouble())
                            .fillColor(requireContext().getColor(R.color.accentTransparent))
                            .strokeWidth(2.5f)
                            .strokeColor(requireContext().getColor(R.color.accent))
                    )
                }

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