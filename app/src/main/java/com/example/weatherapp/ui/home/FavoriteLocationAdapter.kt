package com.example.weatherapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.FavoriteLocationItemBinding
import com.example.weatherapp.repository.local.entity.FavoriteLocation
import java.util.*

class FavoriteLocationAdapter(private val listener: FavoriteLocationItemListener) :
    RecyclerView.Adapter<FavoriteLocationAdapter.FavoriteLocationViewHolder>() {
    private var items: ArrayList<FavoriteLocation> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    fun addItems(newList: List<FavoriteLocation>) {
        if (newList.isEmpty()) {
            return
        }
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteLocationViewHolder {
        val binding =
            FavoriteLocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteLocationViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: FavoriteLocationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class FavoriteLocationViewHolder(private val binding: FavoriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteLocation: FavoriteLocation) {
            with(binding) {
                tvLocation.text = favoriteLocation.cityName
                rootView.setOnClickListener {
                    listener.onItemClick(favoriteLocation)
                }
            }
        }
    }
}

interface FavoriteLocationItemListener {
    fun onItemClick(favoriteLocation: FavoriteLocation)
}

