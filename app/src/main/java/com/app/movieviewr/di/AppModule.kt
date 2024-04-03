package com.app.movieviewr.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.app.movieviewr.network.ApiService
import com.app.movieviewr.repository.MainRepository
import com.app.movieviewr.ui.viewmodelfactory.MovieViewModelProviderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return ApiService.create()
    }

    @Singleton
    @Provides
    fun provideMovieRepository(apiService: ApiService): MainRepository {
        return MainRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideMovieCatalogViewModelFactory(app: Application, mainRepository: MainRepository): ViewModelProvider.Factory {
        return MovieViewModelProviderFactory(app, mainRepository)
    }
}