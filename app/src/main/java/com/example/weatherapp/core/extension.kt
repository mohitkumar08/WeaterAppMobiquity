package com.example.weatherapp.core

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target

fun ImageView.setImage(
    url: String?,
    context: Context = AppObjectController.joshApplication
) {
    Glide.with(context)
        .load(url)
        .override(Target.SIZE_ORIGINAL)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}