package com.app.movieviewr.ui.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.movieviewr.repository.MainRepository
import com.app.movieviewr.ui.viewmodel.GenreViewModel
import com.app.movieviewr.ui.viewmodel.MovieCatalogViewModel
import com.app.movieviewr.ui.viewmodel.MovieDetailViewModel
import com.app.movieviewr.ui.viewmodel.UserReviewViewModel
import javax.inject.Inject

class MovieViewModelProviderFactory @Inject constructor (
    val app: Application,
    private val mainRepository: MainRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MovieCatalogViewModel::class.java) -> {
                MovieCatalogViewModel(app, mainRepository) as T
            }
            modelClass.isAssignableFrom(MovieDetailViewModel::class.java) -> {
                MovieDetailViewModel(app, mainRepository) as T
            }
            modelClass.isAssignableFrom(UserReviewViewModel::class.java) -> {
                UserReviewViewModel(app, mainRepository) as T
            }
            modelClass.isAssignableFrom(GenreViewModel::class.java) -> {
                GenreViewModel(app, mainRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}