package com.app.movieviewr.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.movieviewr.R
import com.app.movieviewr.databinding.ActivityUserReviewBinding
import com.app.movieviewr.ui.adapter.UserReviewAdapter
import com.app.movieviewr.ui.viewmodel.UserReviewViewModel
import com.app.movieviewr.ui.viewmodelfactory.MovieViewModelProviderFactory
import com.app.movieviewr.util.EndlessRecyclerViewListener
import com.app.movieviewr.util.LoadingType
import com.app.movieviewr.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserReviewActivity : BaseActivity() {
    private lateinit var binding: ActivityUserReviewBinding

    @Inject
    lateinit var viewModelFactory: MovieViewModelProviderFactory
    private lateinit var viewModel: UserReviewViewModel

    private lateinit var userReviewAdapter: UserReviewAdapter
    private lateinit var scrollListener: EndlessRecyclerViewListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[UserReviewViewModel::class.java]

        setupRecyclerView()

        val movieId = intent.getIntExtra("movie_id", 0)
        viewModel.movieId = movieId
        viewModel.reviewsData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    showRefreshLoading(false)
                    response.data?.let { reviewsResponse ->
                        userReviewAdapter.differ.submitList(reviewsResponse.results!!.toList())
                    }
                    showEmptyState(userReviewAdapter.itemCount == 0)
                }

                is Resource.Error -> {
                    showRefreshLoading(false)
                    response.message?.let { message ->
                        showSnackbar(binding.root, getString(R.string.error_alert, message), true)
                    }
                    showEmptyState(userReviewAdapter.itemCount == 0)
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
            viewModel.clearReviews()
            viewModel.getReviewList()
        }
    }

    private fun setupRecyclerView() {
        userReviewAdapter = UserReviewAdapter(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        scrollListener = object : EndlessRecyclerViewListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.loadingType = LoadingType.PAGINATION
                viewModel.getReviewList()
            }
        }

        binding.rvReview.apply {
            adapter = userReviewAdapter
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(scrollListener)
        }
    }

    private fun showRefreshLoading(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.emptyReview.visibility = if(isEmpty) View.VISIBLE else View.GONE
        binding.tvEmptyReview.visibility = if(isEmpty) View.VISIBLE else View.GONE
        binding.rvReview.visibility = if(isEmpty) View.GONE else View.VISIBLE
    }
}