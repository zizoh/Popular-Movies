package com.zizohanto.popularmovies.ui.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.video.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {
    private static final String YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/%s/hqdefault.jpg";
    private static final String VIDEO_TYPE_TRAILER = "Trailer";
    private List<Video> mVideos;
    private Context mContext;
    private VideoItemClickListener mVideoItemClickListener;

    VideoAdapter(Context context, VideoItemClickListener videoItemClickListener) {
        mContext = context;
        mVideoItemClickListener = videoItemClickListener;
    }

    @NonNull
    @Override
    public VideoAdapter.VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.videos_list_item, parent, false);

        return new VideoAdapterViewHolder(view);

    }

    @Override
    public int getItemCount() {
        if (null == mVideos) {
            return 0;
        } else {
            return getNumberOfTrailers(mVideos);
        }
    }

    private int getNumberOfTrailers(List<Video> videos) {
        int count = 0;
        for (int i = 0; i < videos.size(); i++) {
            Video video = videos.get(i);
            if (isVideoTrailer(video)) {
                count++;
            }
        }
        return count;
    }

    private boolean isVideoTrailer(Video video) {
        String videoType = video.getType();
        return videoType.equals(VIDEO_TYPE_TRAILER);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoAdapterViewHolder holder, int position) {
        Video video = mVideos.get(position);
        holder.bind(video);
    }

    public void setVideoData(List<Video> newVideos) {
        // If there was no video data, then recreate all of the list
        if (mVideos == null) {
            mVideos = newVideos;
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mVideos.size();
                }

                @Override
                public int getNewListSize() {
                    return newVideos.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mVideos.get(oldItemPosition).getId() ==
                            newVideos.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Video newVideo = newVideos.get(newItemPosition);
                    Video oldVideo = newVideos.get(oldItemPosition);
                    return newVideo.getId() == oldVideo.getId()
                            && newVideo.getKey().equals(oldVideo.getKey());
                }
            });
            mVideos = newVideos;
            result.dispatchUpdatesTo(this);
        }
        notifyDataSetChanged();
    }

    public interface VideoItemClickListener {
        void onVideoClick(Video clickedVideo);
    }

    class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mVideoThumbnail;

        private VideoAdapterViewHolder(View itemView) {
            super(itemView);

            mVideoThumbnail = itemView.findViewById(R.id.iv_video_thumbnail);
            itemView.setOnClickListener(this);
        }

        void bind(Video video) {
            Picasso.with(mContext)
                    .load(buildVideoThumbnailUrl(video.getKey()))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.im_video_placeholder))
                    .into(mVideoThumbnail);

        }

        private String buildVideoThumbnailUrl(String videoKey) {
            return String.format(YOUTUBE_THUMBNAIL, videoKey);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mVideoItemClickListener.onVideoClick(mVideos.get(clickedPosition));
        }
    }
}
