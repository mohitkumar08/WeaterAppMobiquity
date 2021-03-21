package com.example.weatherapp.ui.addlocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.core.USER_ADD_LOCATION_OBJ
import com.example.weatherapp.databinding.ActivityAddLocationBinding
import com.example.weatherapp.repository.local.entity.FavoriteLocation
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.patloew.colocation.CoLocation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*

const val REQUEST_SHOW_SETTINGS = 123

@ExperimentalCoroutinesApi
class AddLocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private var locationUpdatesJob: Job? = null
    private var reverseGeocode: Job? = null
    private var favoriteLocation: FavoriteLocation? = null
    private lateinit var binding:ActivityAddLocationBinding

    private val coLocation: CoLocation by lazy {
        CoLocation.from(applicationContext)
    }
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_location)
        binding.handler = this

        checkLocationPermission()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun add() {
        val intent = Intent()
        intent.putExtra(USER_ADD_LOCATION_OBJ, favoriteLocation)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMinZoomPreference(6.0f)
        mMap.setMaxZoomPreference(14.0f)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
    }


    private fun checkLocationPermission() {
        val permission = arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        Dexter.withContext(this)
            .withPermissions(permission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.areAllPermissionsGranted()?.let { flag ->
                        if (flag) {
                            fetchUserLocation()
                            return@let
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {

                            return
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }


    @SuppressLint("MissingPermission")
    private fun fetchUserLocation() {
        lifecycleScope.launchWhenStarted {
            when (val settingsResult = coLocation.checkLocationSettings(locationRequest)) {
                CoLocation.SettingsResult.Satisfied -> {
                    val location = coLocation.getLastLocation()
                    if (null == location) {
                        startLocationUpdates()
                    } else {
                        onUpdateLocation(location)
                    }
                }
                is CoLocation.SettingsResult.Resolvable -> {
                    settingsResult.resolve(this@AddLocationActivity, REQUEST_SHOW_SETTINGS)
                }
                else -> { /* Ignore for now, we can't resolve this anyway */
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                coLocation.getLocationUpdates(locationRequest).collectLatest {
                    onUpdateLocation(it)
                    locationUpdatesJob?.cancel()
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
            }
        }
    }

    private fun onUpdateLocation(location: Location) {
        if (this::mMap.isInitialized) {
            val loc = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(loc))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
            initFavoriteLocationObject(loc)
            mMap.setOnMapClickListener(this)
        }
    }

    private fun initFavoriteLocationObject(loc: LatLng) {
        reverseGeocode = CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                geocoder.getFromLocation(loc.latitude, loc.longitude, 1).getOrNull(0)?.let {
                    Log.e("aa", it.locality + "   " + it.adminArea + "   " + it.getAddressLine(0))

                    when {
                        it.locality.isNullOrEmpty().not() -> {
                            favoriteLocation = FavoriteLocation(
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                cityName = it.locality
                            )
                        }
                        it.adminArea.isNullOrEmpty().not() -> {
                            favoriteLocation = FavoriteLocation(
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                cityName = it.adminArea
                            )
                        }
                        it.subAdminArea.isNullOrEmpty().not() -> {
                            favoriteLocation = FavoriteLocation(
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                cityName = it.subAdminArea
                            )
                        }
                        else -> {
                            favoriteLocation = FavoriteLocation(
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                cityName = it.getAddressLine(0)
                            )
                        }
                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    companion object {
        fun addLocationIntent(activity: Activity): Intent {
            return Intent(activity, AddLocationActivity::class.java)
        }
    }

    override fun onMapClick(p0: LatLng) {
        initFavoriteLocationObject(p0)
    }

}