package com.app.movieviewr.ui.viewmodel

import MovieListResponse
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.movieviewr.repository.MainRepository
import com.app.movieviewr.util.LoadingType
import com.app.movieviewr.util.NetworkManager
import com.app.movieviewr.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class MovieCatalogViewModel @Inject constructor(app: Application, private val repository: MainRepository) : AndroidViewModel(app) {

    val moviesData: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    private var moviesResponse: MovieListResponse? = null
    private var moviesPage = 1
    var loadingType: LoadingType = LoadingType.INITIAL
    var genreId: Int = 0
        set(value) {
            field = value
            getMovieList()
        }

    fun getMovieList() = viewModelScope.launch {
        safeMovieListCall()
    }

    fun clearMovies() {
        loadingType = LoadingType.INITIAL
        moviesPage = 1
        moviesResponse = null
    }

    private suspend fun safeMovieListCall() {
        moviesData.postValue(Resource.Loading(type = loadingType))
        try {
            if(NetworkManager(getApplication()).isNetworkAvailable()) {
                val response = repository.getMovieList(moviesPage, genreId.toString())
                moviesData.postValue(handleMoviesResponse(response))
            } else {
                moviesData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> moviesData.postValue(Resource.Error("Network Failure"))
                else -> moviesData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleMoviesResponse(response: Response<MovieListResponse>) : Resource<MovieListResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                moviesPage++
                if(moviesResponse == null) {
                    moviesResponse = resultResponse
                } else {
                    val oldMovies = moviesResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies!!)
                }
                return Resource.Success(moviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}