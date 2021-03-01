package com.bsilent.app.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bsilent.app.R
import com.bsilent.app.database.AppDatabase
import com.bsilent.app.databinding.ActivityPickerBinding
import com.bsilent.app.ui.frags.AddLocationDialogFragment
import com.bsilent.app.viewmodels.AddLocationViewModel
import com.bsilent.app.viewmodels.PlaceViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class PickerActivity : AppCompatActivity(), OnMapReadyCallback {
    private val LOCATION_PERMISSION_REQUEST = 912

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityPickerBinding

    private lateinit var frag: AddLocationDialogFragment

    private lateinit var viewModel: AddLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        frag = AddLocationDialogFragment()

        val factory = PlaceViewModelFactory(
            AppDatabase.getInstance(applicationContext).placesDao,
            application
        )
        viewModel = ViewModelProvider(this, factory).get(AddLocationViewModel::class.java)
        viewModel.isInserted.observe(this, Observer {
            if (it) {
                onBackPressed()
            }
        })
        binding.done.setOnClickListener {
            viewModel.latlng.value = map.cameraPosition.target
            AddLocationDialogFragment().show(supportFragmentManager, "add_loc")
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Pick Place"
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            requestPermission()
        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) onBackPressed()

        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_PERMISSION_REQUEST && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
        }
    }
}