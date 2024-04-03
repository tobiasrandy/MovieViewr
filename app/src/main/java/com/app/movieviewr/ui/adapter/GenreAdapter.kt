package com.app.movieviewr.ui.adapter

import Genre
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.movieviewr.databinding.ItemGenreBinding

class GenreAdapter : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    inner class GenreViewHolder(val binding: ItemGenreBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Int, String) -> Unit)? = null

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = differ.currentList[position]
        val binding = holder.binding

        binding.apply {

            tvGenre.text = genre.name

            root.setOnClickListener {
                onItemClickListener?.let { it(genre.id ?: -1, genre.name ?: "") }
            }
        }
    }

    fun setOnItemClickListener(listener: (Int, String) -> Unit) {
        onItemClickListener = listener
    }
}