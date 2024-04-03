package com.app.movieviewr.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.movieviewr.R
import com.app.movieviewr.databinding.ActivityMovieCatalogBinding
import com.app.movieviewr.ui.adapter.MovieAdapter
import com.app.movieviewr.ui.viewmodel.MovieCatalogViewModel
import com.app.movieviewr.ui.viewmodelfactory.MovieViewModelProviderFactory
import com.app.movieviewr.util.EndlessRecyclerViewListener
import com.app.movieviewr.util.LoadingType
import com.app.movieviewr.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MovieCatalogActivity : BaseActivity() {
    private lateinit var binding: ActivityMovieCatalogBinding

    @Inject
    lateinit var viewModelFactory: MovieViewModelProviderFactory
    private lateinit var viewModel: MovieCatalogViewModel

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var scrollListener: EndlessRecyclerViewListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[MovieCatalogViewModel::class.java]

        setupRecyclerView()

        val genreId = intent.getIntExtra("genre_id", 0)
        val movieName = intent.getStringExtra("genre_name")

        binding.toolbar.title = movieName

        viewModel.genreId = genreId
        viewModel.moviesData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    showRefreshLoading(false)
                    response.data?.let { moviesResponse ->
                        movieAdapter.differ.submitList(moviesResponse.results!!.toList())
                    }
                    showEmptyState(movieAdapter.itemCount == 0)
                }

                is Resource.Error -> {
                    showRefreshLoading(false)
                    response.message?.let { message ->
                        showSnackbar(binding.root, getString(R.string.error_alert, message), true)
                    }
                    showEmptyState(movieAdapter.itemCount == 0)
                }

                is Resource.Loading -> {
                    when (response.loadingType) {
                        LoadingType.PAGINATION -> {
                            // Handle pagination loading
                        }

                        LoadingType.REFRESH -> {
                            showRefreshLoading(true)
                        }

                        else -> {
                            showRefreshLoading(true)
                        }
                    }
                    showEmptyState(false)
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.clearMovies()
            viewModel.getMovieList()
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter()

        val gridLayoutManager = GridLayoutManager(this, 2)

        scrollListener = object : EndlessRecyclerViewListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.loadingType = LoadingType.PAGINATION
                viewModel.getMovieList()
            }
        }

        binding.rvMovie.apply {
            adapter = movieAdapter
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(scrollListener)
        }

        movieAdapter.setOnItemClickListener { movieId ->
            val intent = Intent(activity, MovieDetailActivity::class.java)
            intent.putExtra("movie_id", movieId)
            startActivity(intent)
        }
    }

    private fun showRefreshLoading(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.emptyMovie.visibility = if(isEmpty) View.VISIBLE else View.GONE
        binding.tvEmptyMovie.visibility = if(isEmpty) View.VISIBLE else View.GONE
        binding.rvMovie.visibility = if(isEmpty) View.GONE else View.VISIBLE
    }
}