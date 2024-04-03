package com.app.movieviewr.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class GlideImageLoader {

    fun loadImage(
        context: Context,
        imageUrl: String?,
        imageView: ImageView,
        placeholderResId: Int,
        errorResId: Int
    ) {
        val fullImageUrl = Constants.IMAGE_PATH + imageUrl
        Glide.with(context)
            .load(fullImageUrl)
            .apply(
                RequestOptions()
                    .placeholder(placeholderResId)
                    .error(errorResId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(imageView)
    }

    fun loadImage(
        view: View,
        imageUrl: String?,
        imageView: ImageView,
        placeholderResId: Int,
        errorResId: Int
    ) {
        val fullImageUrl = Constants.IMAGE_PATH + imageUrl
        Glide.with(view)
            .load(fullImageUrl)
            .apply(
                RequestOptions()
                    .placeholder(placeholderResId)
                    .error(errorResId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(imageView)
    }
}