package com.bsilent.app.ui.activities


import android.app.NotificationManager
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bsilent.app.R
import com.bsilent.app.adapters.PagerAdapter
import com.bsilent.app.databinding.ActivityMainBinding
import com.bsilent.app.ui.dialogs.AddScheduleDialog
import com.bsilent.app.ui.frags.ModifiePlaceDialog
import com.bsilent.app.ui.frags.PlacesFragment
import com.bsilent.app.ui.frags.ScheduleFragment
import com.bsilent.app.viewmodels.PlacesViewModel
import com.bsilent.app.viewmodels.ScheduleViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var frags: List<Fragment>

    private val schedViewModel: ScheduleViewModel by viewModels()

    private val placeViewModel: PlacesViewModel by viewModels()

    private lateinit var modifiePlaceDialog: ModifiePlaceDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_Bsilent)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupTabs()
        setupBottomFrag()
    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !(getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .isNotificationPolicyAccessGranted
        ) {
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_req)
                .setMessage(R.string.perm_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.allow){ _,_ ->
                    val intent = Intent(
                        Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                    )
                    startActivity(intent)
                }
                .setNegativeButton(R.string.cancel){ _,_ ->
                    finish()
                }
                .show()
        }
    }

    private fun setupBottomFrag() {
        lifecycleScope.launch(Dispatchers.Main) {
            modifiePlaceDialog = ModifiePlaceDialog()
        }
        placeViewModel.showDialogPlace.observe(this) {
            if (it) {
                placeViewModel.showDialogPlace.value = false
                modifiePlaceDialog.show(supportFragmentManager, "tag22")
            }
        }
    }

    private fun setupTabs() {
        frags = listOf(
            PlacesFragment(),
            ScheduleFragment()
        )
        binding.viewpager.adapter = PagerAdapter(this, frags)
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.locationFab.apply {
                        setImageResource(R.drawable.ic_add_location)
                        setOnClickListener {
                            startAddLocationActivity()
                        }
                    }
                } else {
                    binding.locationFab.apply {
                        setImageResource(R.drawable.ic_add_alarm)
                        setOnClickListener {
                            showAddScheduleDialog()
                        }
                    }
                }

            }
        })
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, pos ->
            if (pos == 0) {
                tab.apply {
                    text = getString(R.string.places)
                    icon = getDrawable(R.drawable.ic_location)
                }
            } else {
                tab.apply {
                    text = getString(R.string.schedules)
                    icon = getDrawable(R.drawable.ic_time)
                }
            }
        }.attach()
    }

    private fun showAddScheduleDialog() {
        lifecycleScope.launch(Dispatchers.Main){
            AddScheduleDialog(this@MainActivity) {
                schedViewModel.insert(it, this@MainActivity)
            }.show()
        }

    }

    private fun startAddLocationActivity() {
        Intent(this, PickerActivity::class.java).also {
            startActivity(it)
        }
    }

    override fun onStart() {
        super.onStart()
        requestPermission()
    }
}