package com.app.movieviewr.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.movieviewr.R
import com.app.movieviewr.databinding.ActivityGenreBinding
import com.app.movieviewr.ui.adapter.GenreAdapter
import com.app.movieviewr.ui.viewmodel.GenreViewModel
import com.app.movieviewr.ui.viewmodelfactory.MovieViewModelProviderFactory
import com.app.movieviewr.util.LoadingType
import com.app.movieviewr.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GenreActivity : BaseActivity() {
    private lateinit var binding: ActivityGenreBinding

    @Inject
    lateinit var viewModelFactory: MovieViewModelProviderFactory
    private lateinit var viewModel: GenreViewModel

    private lateinit var genreAdapter: GenreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory)[GenreViewModel::class.java]

        setupRecyclerView()

        viewModel.genresData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideLoadingDialog()
                    response.data?.let { reviewsResponse ->
                        genreAdapter.differ.submitList(reviewsResponse.genres!!.toList())
                    }
                    showEmptyState(genreAdapter.itemCount == 0)
                }

                is Resource.Error -> {
                    hideLoadingDialog()
                    response.message?.let { message ->
                        showSnackbar(binding.root, getString(R.string.error_alert, message), true)
                    }
                    showEmptyState(genreAdapter.itemCount == 0)
                }

                is Resource.Loading -> {
                    when (response.loadingType) {
                        LoadingType.PAGINATION -> {

                        }

                        LoadingType.REFRESH -> {

                        }

                        else -> {
                            showLoadingDialog()
                        }
                    }
                    showEmptyState(false)
                }
            }
        }

        binding.btnRetry.setOnClickListener {
            viewModel.getGenreList()
            showEmptyState(true)
        }
    }

    private fun setupRecyclerView() {
        genreAdapter = GenreAdapter()

        val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.rvGenre.apply {
            adapter = genreAdapter
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
        }

        genreAdapter.setOnItemClickListener { id, name ->
            val intent = Intent(activity, MovieCatalogActivity::class.java)
            intent.putExtra("genre_id", id)
            intent.putExtra("genre_name", name)
            startActivity(intent)
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.btnRetry.visibility = if(isEmpty) View.VISIBLE else View.GONE
        binding.rvGenre.visibility = if(isEmpty) View.GONE else View.VISIBLE
    }
}