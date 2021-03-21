package com.example.weatherapp.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.core.USER_ADD_LOCATION_OBJ
import com.example.weatherapp.databinding.ActivityHomeBinding
import com.example.weatherapp.repository.local.entity.FavoriteLocation
import com.example.weatherapp.ui.addlocation.AddLocationActivity
import com.example.weatherapp.ui.city.WeatherDetailActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest


@ExperimentalCoroutinesApi
class HomeActivity : AppCompatActivity(), FavoriteLocationItemListener {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    private val favoriteLocationAdapter: FavoriteLocationAdapter by lazy {
        FavoriteLocationAdapter(
            this
        )
    }

    private var addLocationActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                USER_ADD_LOCATION_OBJ
                val location =
                    result.data?.getParcelableExtra(USER_ADD_LOCATION_OBJ) as FavoriteLocation?
                location?.let {
                    viewModel.addLocationInFavorite(it)
                }
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.handler = this
        supportActionBar?.title="Weather App"
        initRV()
        addObservable()
        viewModel.getFavoriteLocation()
    }

    fun addLocation() {
        addLocationActivityResult.launch(AddLocationActivity.addLocationIntent(this))
    }

    private fun initRV() {
        binding.favoriteLocationRv.apply {
            layoutManager = LinearLayoutManager(applicationContext).apply {
                isSmoothScrollbarEnabled = true
            }
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(
                    applicationContext,
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = favoriteLocationAdapter
        }
    }

    private fun addObservable() {
        lifecycleScope.launchWhenStarted {
            viewModel.favoriteLocationList.collectLatest {
                favoriteLocationAdapter.addItems(it)
            }
        }
    }

    override fun onItemClick(favoriteLocation: FavoriteLocation) {
        WeatherDetailActivity.weatherDetailsActivity(this, favoriteLocation)
    }


}