package com.app.movieviewr.ui.viewmodel

import MovieDetailResponse
import MovieReviewsResponse
import MovieVideosResponse
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

class MovieDetailViewModel @Inject constructor(app: Application, private val repository: MainRepository) : AndroidViewModel(app) {

    val movieDetailData: MutableLiveData<Resource<MovieDetailResponse>> = MutableLiveData()
    val movieVideosData: MutableLiveData<Resource<MovieVideosResponse>> = MutableLiveData()
    val movieReviewsData: MutableLiveData<Resource<MovieReviewsResponse>> = MutableLiveData()

    private var loadingType: LoadingType = LoadingType.INITIAL

    var movieId: Int = 0
        set(value) {
            field = value
            getDetail()
        }

    private fun getDetail() {
        viewModelScope.launch {
            safeMovieDetailCall()
            safeMovieVideosCall()
            safeMovieReviewsCall()
        }
    }

    private suspend fun safeMovieDetailCall() {
        movieDetailData.postValue(Resource.Loading(type = loadingType))
        try {
            if(NetworkManager(getApplication()).isNetworkAvailable()) {
                val response = repository.getMovieDetail(movieId)
                movieDetailData.postValue(handleMovieDetailResponse(response))
            } else {
                movieDetailData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> movieDetailData.postValue(Resource.Error("Network Failure"))
                else -> movieDetailData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeMovieVideosCall() {
        try {
            if(NetworkManager(getApplication()).isNetworkAvailable()) {
                val response = repository.getMovieVideos(movieId)
                movieVideosData.postValue(handleMovieVideosResponse(response))
            } else {
                movieVideosData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> movieVideosData.postValue(Resource.Error("Network Failure"))
                else -> movieVideosData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeMovieReviewsCall() {
        try {
            if(NetworkManager(getApplication()).isNetworkAvailable()) {
                val response = repository.getMovieReviews(movieId)
                movieReviewsData.postValue(handleMovieReviewsResponse(response))
            } else {
                movieReviewsData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> movieReviewsData.postValue(Resource.Error("Network Failure"))
                else -> movieReviewsData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleMovieDetailResponse(response: Response<MovieDetailResponse>) : Resource<MovieDetailResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleMovieVideosResponse(response: Response<MovieVideosResponse>) : Resource<MovieVideosResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleMovieReviewsResponse(response: Response<MovieReviewsResponse>) : Resource<MovieReviewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}