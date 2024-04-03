package com.app.movieviewr.ui.adapter

import ReviewItem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.movieviewr.R
import com.app.movieviewr.databinding.ItemReviewBinding
import com.app.movieviewr.util.convertDate
import com.app.movieviewr.util.getFormattedRating

class UserReviewAdapter(private val context: Context) : RecyclerView.Adapter<UserReviewAdapter.UserReviewViewHolder>() {

    inner class UserReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<ReviewItem>() {
        override fun areItemsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserReviewViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: UserReviewViewHolder, position: Int) {
        val review = differ.currentList[position]
        val binding = holder.binding

        binding.apply {
            if(review.authorDetails!!.rating != null) {
                binding.star.visibility = View.VISIBLE
                binding.tvRating.visibility = View.VISIBLE
                binding.tvRating.text = getFormattedRating(context, review.authorDetails.rating.toString())
            } else {
                binding.star.visibility = View.GONE
                binding.tvRating.visibility = View.GONE
            }
            binding.tvDate.text = convertDate(review.createdAt!!, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMMM yyyy")
            binding.tvTitle.text = context.getString(R.string.review_by, review.author)
            binding.tvUser.text = review.author
            binding.tvReviewComment.text = review.content
        }
    }
}