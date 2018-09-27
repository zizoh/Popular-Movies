package com.zizohanto.popularmovies.ui.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.review.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private List<Review> mReviews;
    private Context mContext;

    ReviewAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.reviews_list_item, parent, false);

        return new ReviewAdapter.ReviewAdapterViewHolder(view);

    }

    @Override
    public int getItemCount() {
        if (null == mReviews) {
            return 0;
        } else {
            return mReviews.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.bind(review);
    }

    public void setReviewData(List<Review> newReviews) {
        // If there was no review data, then recreate all of the list
        if (mReviews == null) {
            mReviews = newReviews;
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mReviews.size();
                }

                @Override
                public int getNewListSize() {
                    return newReviews.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mReviews.get(oldItemPosition).getId() ==
                            newReviews.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Review newReview = newReviews.get(newItemPosition);
                    Review oldReview = newReviews.get(oldItemPosition);
                    return newReview.getId() == oldReview.getId()
                            && newReview.getContent().equals(oldReview.getContent());
                }
            });
            mReviews = newReviews;
            result.dispatchUpdatesTo(this);
        }
        notifyDataSetChanged();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView mReviewAuthor;
        private ExpandableTextView mReviewContent;

        private ReviewAdapterViewHolder(View itemView) {
            super(itemView);

            mReviewAuthor = itemView.findViewById(R.id.tv_review_author);
            mReviewContent = itemView.findViewById(R.id.tv_review_content);
        }

        void bind(Review review) {
            mReviewAuthor.setText(review.getAuthor());
            mReviewContent.setText(review.getContent());
        }
    }
}
