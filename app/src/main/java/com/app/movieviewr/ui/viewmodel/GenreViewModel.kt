package com.app.movieviewr.ui.viewmodel

import MovieGenresResponse
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

class GenreViewModel @Inject constructor(app: Application, private val repository: MainRepository) : AndroidViewModel(app) {

    val genresData: MutableLiveData<Resource<MovieGenresResponse>> = MutableLiveData()
    var loadingType: LoadingType = LoadingType.INITIAL

    init {
        getGenreList()
    }

    fun getGenreList() = viewModelScope.launch {
        safeGenreListCall()
    }

    private suspend fun safeGenreListCall() {
        genresData.postValue(Resource.Loading(type = loadingType))
        try {
            if(NetworkManager(getApplication()).isNetworkAvailable()) {
                val response = repository.getGenres()
                genresData.postValue(handleGenresResponse(response))
            } else {
                genresData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> genresData.postValue(Resource.Error("Network Failure"))
                else -> genresData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleGenresResponse(response: Response<MovieGenresResponse>) : Resource<MovieGenresResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}