package com.bsilent.app.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bsilent.app.R
import com.bsilent.app.databinding.ActivityPickerBinding
import com.bsilent.app.ui.frags.AddLocationDialogFragment
import com.bsilent.app.viewmodels.AddLocationViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PickerActivity : AppCompatActivity() {
    private val CHECK_LOCATION_REQUEST: Int = 133
    private val LOCATION_PERMISSION_REQUEST = 912

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityPickerBinding

    private lateinit var frag: AddLocationDialogFragment

    private val viewModel: AddLocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        lifecycleScope.launch(Dispatchers.Main) {
            val mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit()

            frag = AddLocationDialogFragment()

            mapFragment.getMapAsync {
                binding.progress.visibility = View.GONE
                map = it
                if (ActivityCompat.checkSelfPermission(
                        this@PickerActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    map.isMyLocationEnabled = true
                }
            }
            checkLocation()
        }

        requestPermission()

        viewModel.isInserted.observe(this) {
            if (it) {
                onBackPressed()
            }
        }
        binding.done.setOnClickListener {
            viewModel.latlng.value = map.cameraPosition.target
            AddLocationDialogFragment().show(supportFragmentManager, "add_loc")
        }
    }

    private fun checkPermissions(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @SuppressLint("MissingPermission")
    private fun animateCamToCurrentPos() {

        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            it?.let {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude, it.longitude
                        ),
                        16.5f
                    )
                )
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.pick_place)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun requestPermission() {
        if(checkPermissions()) return

        var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions += Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }
        ActivityCompat.requestPermissions(
            this,
            permissions,
            LOCATION_PERMISSION_REQUEST
        )
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) onBackPressed()

        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.isMyLocationEnabled = true
                }
            }
        }
    }

    private fun checkLocation() {
        LocationServices.getSettingsClient(this).checkLocationSettings(
            LocationSettingsRequest.Builder().addLocationRequest(
                LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(5000)
            ).setAlwaysShow(true)
                .build()
        ).addOnSuccessListener {
            animateCamToCurrentPos()
        }
            .addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        it.startResolutionForResult(this, CHECK_LOCATION_REQUEST)
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }

                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECK_LOCATION_REQUEST && resultCode == RESULT_OK) {
            animateCamToCurrentPos()
        }
    }
}