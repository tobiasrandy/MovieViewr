package com.app.movieviewr.ui.viewmodel

import MovieReviewsResponse
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

class UserReviewViewModel @Inject constructor(app: Application, private val repository: MainRepository) : AndroidViewModel(app) {

    val reviewsData: MutableLiveData<Resource<MovieReviewsResponse>> = MutableLiveData()
    private var reviewsResponse: MovieReviewsResponse? = null
    private var reviewsPage = 1
    var loadingType: LoadingType = LoadingType.INITIAL
    var movieId: Int = 0
        set(value) {
            field = value
            getReviewList()
        }

    fun getReviewList() = viewModelScope.launch {
        safeReviewListCall()
    }

    fun clearReviews() {
        loadingType = LoadingType.INITIAL
        reviewsPage = 1
        reviewsResponse = null
    }

    private suspend fun safeReviewListCall() {
        reviewsData.postValue(Resource.Loading(type = loadingType))
        try {
            if(NetworkManager(getApplication()).isNetworkAvailable()) {
                val response = repository.getMovieReviews(movieId, reviewsPage)
                reviewsData.postValue(handleReviewsResponse(response))
            } else {
                reviewsData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> reviewsData.postValue(Resource.Error("Network Failure"))
                else -> reviewsData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleReviewsResponse(response: Response<MovieReviewsResponse>) : Resource<MovieReviewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                reviewsPage++
                if(reviewsResponse == null) {
                    reviewsResponse = resultResponse
                } else {
                    val oldReviews = reviewsResponse?.results
                    val newReviews = resultResponse.results
                    oldReviews?.addAll(newReviews!!)
                }
                return Resource.Success(reviewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}