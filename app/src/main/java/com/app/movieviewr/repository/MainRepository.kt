package com.app.movieviewr.repository

import com.app.movieviewr.network.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor (private val apiService: ApiService) {

    suspend fun getMovieList(page: Int, genres: String) = apiService.getMovieList(page, genres)

    suspend fun getGenres() = apiService.getGenres()

    suspend fun getMovieDetail(id: Int) = apiService.getMovieDetail(id)

    suspend fun getMovieVideos(id: Int) = apiService.getMovieVideos(id)

    suspend fun getMovieReviews(id: Int) = apiService.getMovieReviewList(id, 1)

    suspend fun getMovieReviews(id: Int, page: Int) = apiService.getMovieReviewList(id, page)

}