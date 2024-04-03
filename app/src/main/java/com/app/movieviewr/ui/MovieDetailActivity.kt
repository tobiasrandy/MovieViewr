package com.app.movieviewr.ui

import MovieDetailResponse
import MovieTrailer
import MovieVideosResponse
import ReviewItem
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.app.movieviewr.R
import com.app.movieviewr.databinding.ActivityMovieDetailBinding
import com.app.movieviewr.ui.viewmodel.MovieDetailViewModel
import com.app.movieviewr.ui.viewmodelfactory.MovieViewModelProviderFactory
import com.app.movieviewr.util.GlideImageLoader
import com.app.movieviewr.util.Resource
import com.app.movieviewr.util.convertDate
import com.app.movieviewr.util.getFormattedRating
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ceil

@AndroidEntryPoint
class MovieDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityMovieDetailBinding

    @Inject
    lateinit var viewModelFactory: MovieViewModelProviderFactory
    private lateinit var viewModel: MovieDetailViewModel

    private lateinit var youTubePlayer: YouTubePlayer
    private var isFullscreen = false
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullscreen) {
                youTubePlayer.toggleFullscreen()
            } else {
                finish()
            }
        }
    }
    private var movieId: Int = 0
    private lateinit var decorView: View
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        binding.collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.dark_shade))

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        movieId = intent.getIntExtra("movie_id", 0)
        setupViewModel(movieId)
    }

    private fun setupViewModel(movieId: Int) {
        viewModel = ViewModelProvider(this, viewModelFactory)[MovieDetailViewModel::class.java]
        viewModel.movieId = movieId

        viewModel.movieDetailData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideLoadingDialog()
                    response.data?.let { movieDetailResponse ->
                        updateView(movieDetailResponse)
                    }
                }

                is Resource.Error -> {
                    hideLoadingDialog()
                    response.message?.let { message ->
                        showSnackbar(binding.root, getString(R.string.error_alert, message), true)
                    }
                }

                is Resource.Loading -> {
                    showLoadingDialog()
                }
            }
        }

        viewModel.movieVideosData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { movieVideosResponse ->
                        setupYoutubePlayer(movieVideosResponse)
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        showSnackbar(binding.root, getString(R.string.error_alert, message), true)
                    }
                }

                is Resource.Loading -> {

                }
            }
        }

        viewModel.movieReviewsData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { movieReviewsResponse ->
                        setupReviewSection(movieReviewsResponse.results as ArrayList<ReviewItem>)
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        showSnackbar(binding.root, getString(R.string.error_alert, message), true)
                    }
                }

                is Resource.Loading -> {

                }
            }
        }
    }

    private fun setupYoutubePlayer(response: MovieVideosResponse) {
        handler = Handler(Looper.getMainLooper())
        val trailers: List<MovieTrailer> = response.results?.filter { it.type == "Trailer" } ?: emptyList()
        if (trailers.isNotEmpty()) {
            val iFramePlayerOptions = IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1)
                .build()

            binding.youtubePlayerView.enableAutomaticInitialization = false
            decorView = window.decorView
            binding.youtubePlayerView.addFullscreenListener(object : FullscreenListener {
                override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                    isFullscreen = true
                    binding.youtubePlayerView.visibility = View.GONE
                    binding.fullScreenViewContainer.visibility = View.VISIBLE
                    binding.fullScreenViewContainer.addView(fullscreenView)

                    decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                        val isStatusBarVisible = visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0
                        val isNavBarVisible = visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
                        if ((isStatusBarVisible || isNavBarVisible) && isFullscreen) {
                            handler.postDelayed({
                                hideSystemUI()
                            }, 3000)
                        }
                    }

                    hideSystemUI()
                    binding.appBarLayout.setExpanded(true)
                    setScrollingEnabled(false)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

                override fun onExitFullscreen() {
                    isFullscreen = false
                    binding.youtubePlayerView.visibility = View.VISIBLE
                    binding.fullScreenViewContainer.visibility = View.GONE
                    binding.fullScreenViewContainer.removeAllViews()

                    handler.removeCallbacksAndMessages(null)

                    showSystemUI()
                    setScrollingEnabled(true)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            })

            binding.tvTrailer.visibility = View.VISIBLE
            binding.youtubePlayerView.visibility = View.VISIBLE
            try {
                binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        this@MovieDetailActivity.youTubePlayer = youTubePlayer
                        youTubePlayer.cueVideo(trailers[0].key!!, 0f)
                    }
                }, iFramePlayerOptions)
            } catch (e: IllegalStateException) {
                binding.youtubePlayerView.visibility = View.GONE
            }

            lifecycle.addObserver(binding.youtubePlayerView)
        }
    }

    private fun hideSystemUI() {
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }

    private fun showSystemUI() {
        decorView.setOnSystemUiVisibilityChangeListener(null)
        val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
        decorView.systemUiVisibility = uiOptions
    }

    private fun setToolbarListener() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val scrollPercentage = -verticalOffset.toFloat() / totalScrollRange.toFloat()
            binding.scoreContainer.visibility = if (scrollPercentage == 1.0f) View.GONE else View.VISIBLE
        }
    }

    private fun setScrollingEnabled(enabled: Boolean) {
        val appBarParams = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val appBarBehavior = appBarParams.behavior as AppBarLayout.Behavior
        appBarBehavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return enabled
            }
        })

        binding.scrollView.isNestedScrollingEnabled = enabled
    }

    private fun convertDuration(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return if (hours > 0) {
            hours.toString() + "h " + remainingMinutes + "m"
        } else {
            "$remainingMinutes m"
        }
    }

    private fun updateView(response: MovieDetailResponse) {
        GlideImageLoader().loadImage(this, response.backdropPath, binding.expandedImage, R.drawable.movie_placeholder, R.drawable.movie_placeholder)
        binding.tvMovieTitle.text = response.title
        binding.tvSynopsis.text = response.overview

        val releaseDate = if(!response.releaseDate.isNullOrEmpty()) response.releaseDate.split("-")[0] else ""
        val duration = if(response.runtime != 0) convertDuration(response.runtime!!) else ""
        binding.tvMovieInfo.text = "$releaseDate • $duration"

        if(response.voteAverage != null) {
            val score = ceil(response.voteAverage * 10).toInt()
            binding.scoreBar.progress = score
            binding.tvScore.text = "$score"
            binding.scoreContainer.visibility = View.VISIBLE
            setToolbarListener()
        }

        for (genre in response.genres!!) {
            val chip = Chip(this)
            chip.text = genre.name
            binding.chipGroup.addView(chip)
        }
    }

    private fun setupReviewSection(reviewList: ArrayList<ReviewItem>) {
        if(reviewList.isNotEmpty()) {
            val firstReview = reviewList[0]
            binding.tvReview.visibility = View.VISIBLE
            binding.itemReview.root.visibility = View.VISIBLE

            if(reviewList.size > 1) {
                binding.btnReview.visibility = View.VISIBLE
                binding.btnReview.setOnClickListener {
                    val intent = Intent(this, UserReviewActivity::class.java)
                    intent.putExtra("movie_id", movieId)
                    startActivity(intent)
                }
            }

            if(firstReview.authorDetails!!.rating != null) {
                binding.itemReview.tvRating.text = getFormattedRating(this, firstReview.authorDetails.rating.toString())
            } else {
                binding.itemReview.star.visibility = View.GONE
            }

            binding.itemReview.tvDate.text = convertDate(firstReview.createdAt!!, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMMM yyyy")
            binding.itemReview.tvTitle.text = getString(R.string.review_by, firstReview.author)
            binding.itemReview.tvUser.text = firstReview.author
            binding.itemReview.tvReviewComment.text = firstReview.content

            binding.itemReview.root.setPadding(0, 0, 0, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}