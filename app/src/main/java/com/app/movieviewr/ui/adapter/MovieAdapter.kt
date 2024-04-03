package com.app.movieviewr.ui.adapter

import Movie
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.movieviewr.R
import com.app.movieviewr.databinding.ItemMovieBinding
import com.app.movieviewr.util.GlideImageLoader
import com.app.movieviewr.util.convertDate
import kotlin.math.ceil

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = differ.currentList[position]
        val binding = holder.binding

        binding.apply {
            GlideImageLoader().loadImage(this.root, movie.posterPath, moviePoster, R.drawable.movie_placeholder, R.drawable.movie_placeholder)

            if(movie.voteAverage != null) {
                val score = ceil(movie.voteAverage * 10).toInt()
                scoreBar.progress = score
                tvScore.text = "$score"
                scoreContainer.visibility = View.VISIBLE
            }

            tvMovieTitle.text = movie.title
            tvDate.text = convertDate(movie.releaseDate!!, "yyyy-MM-dd", "MMM d, yyyy")

            root.setOnClickListener {
                onItemClickListener?.let { it(movie.id ?: -1) }
            }
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }
}