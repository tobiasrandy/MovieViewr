package com.app.movieviewr.network

import MovieDetailResponse
import MovieGenresResponse
import MovieListResponse
import MovieReviewsResponse
import MovieVideosResponse
import com.app.movieviewr.util.Constants.Companion.API_KEY
import com.app.movieviewr.util.Constants.Companion.DOMAIN_MOVIE

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    companion object {
        fun create(): ApiService{
            val okHttpClientBuilder = OkHttpClient.Builder()

            var interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)

            val authInterceptor = Interceptor { chain ->
                val originalRequest: Request = chain.request()
                val newRequest: Request = originalRequest.newBuilder()
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer $API_KEY")
                    .build()
                chain.proceed(newRequest)
            }
            okHttpClientBuilder.addInterceptor(authInterceptor)

            val client = okHttpClientBuilder
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(DOMAIN_MOVIE)
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    @GET("3/discover/movie")
    suspend fun getMovieList(@Query("page") id: Int, @Query("with_genres") genres: String) : Response<MovieListResponse>

    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") id: Int) : Response<MovieDetailResponse>

    @GET("3/movie/{movie_id}/videos")
    suspend fun getMovieVideos(@Path("movie_id") id: Int) : Response<MovieVideosResponse>

    @GET("3/movie/{movie_id}/reviews")
    suspend fun getMovieReviewList(@Path("movie_id") id: Int, @Query("page") page: Int) : Response<MovieReviewsResponse>

    @GET("3/genre/movie/list")
    suspend fun getGenres() : Response<MovieGenresResponse>
}