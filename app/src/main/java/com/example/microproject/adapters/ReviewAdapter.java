package com.example.microproject.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.fragments.ReviewEditorFragment;
import com.example.microproject.models.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private OnReviewClickListener listener;
    private OnReviewDeleteListener deleteListener;

    public interface OnReviewClickListener {
        void onReviewClick(Review review);
    }

    public interface OnReviewDeleteListener {
        void onReviewDelete(Review review);
    }

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public void setOnReviewClickListener(OnReviewClickListener listener) {
        this.listener = listener;
    }

    public void setOnReviewDeleteListener(OnReviewDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ImageView ivReview;
        private TextView tvDate;
        private RatingBar ratingBar;
        private View btnEdit;
        private View btnDelete;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvReviewTitle);
            ivReview = itemView.findViewById(R.id.ivReviewImage);
            tvDate = itemView.findViewById(R.id.tvReviewDate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReviewClick(reviewList.get(position));
                }
            });

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReviewClick(reviewList.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && deleteListener != null) {
                    deleteListener.onReviewDelete(reviewList.get(position));
                }
            });
        }

        public void bind(Review review) {
            tvTitle.setText(review.getTitle());
            
            // Set the rating
            ratingBar.setRating(review.getRating());

            // Format the date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String date = sdf.format(new Date(review.getTimestamp()));
            tvDate.setText(date);

            // Load the review image
            if (review.getReviewImagePath() != null && !review.getReviewImagePath().isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(review.getReviewImagePath());
                if (bitmap != null) {
                    ivReview.setImageBitmap(bitmap);
                    ivReview.setVisibility(View.VISIBLE);
                } else {
                    ivReview.setVisibility(View.GONE);
                }
            } else {
                ivReview.setVisibility(View.GONE);
            }
        }
    }
}
