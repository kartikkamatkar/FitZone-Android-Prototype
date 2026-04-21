package com.example.fitzone;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostFeedAdapter extends RecyclerView.Adapter<PostFeedAdapter.PostViewHolder> {

    public interface OnPostActionListener {
        void onLike(PostItem post);
        void onComment(PostItem post);
    }

    private final List<PostItem> items = new ArrayList<>();
    private final OnPostActionListener actionListener;

    public PostFeedAdapter(OnPostActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<PostItem> posts) {
        items.clear();
        items.addAll(posts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_card, parent, false);
        return new PostViewHolder(view, actionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView userNameText;
        private final TextView dateText;
        private final TextView postText;
        private final ImageView postImage;
        private final ImageView avatarImage;
        private final TextView likeCountText;
        private final TextView commentCountText;
        private final ImageButton likeButton;
        private final ImageButton commentButton;
        private final OnPostActionListener actionListener;

        PostViewHolder(@NonNull View itemView, OnPostActionListener actionListener) {
            super(itemView);
            this.actionListener = actionListener;
            userNameText = itemView.findViewById(R.id.postUserNameText);
            dateText = itemView.findViewById(R.id.postDateText);
            postText = itemView.findViewById(R.id.postBodyText);
            postImage = itemView.findViewById(R.id.postImageView);
            avatarImage = itemView.findViewById(R.id.postAvatarImage);
            likeCountText = itemView.findViewById(R.id.postLikeCountText);
            commentCountText = itemView.findViewById(R.id.postCommentCountText);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
        }

        void bind(PostItem item) {
            userNameText.setText(item.getUserName());
            dateText.setText(item.getDate());
            postText.setText(item.getText());

            likeCountText.setText(String.format(Locale.US, "%d", item.getLikeCount()));
            commentCountText.setText(String.format(Locale.US, "%d", item.getCommentCount()));
            likeButton.setImageResource(item.isLikedByCurrentUser()
                    ? android.R.drawable.btn_star_big_on
                    : android.R.drawable.btn_star_big_off);

            likeButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onLike(item);
                }
            });
            commentButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onComment(item);
                }
            });

            bindImage(postImage, item.getImagePath(), R.drawable.exercise_placeholder);
            bindImage(avatarImage, item.getAvatarUri(), R.drawable.profile_placeholder);
        }

        private void bindImage(ImageView target, String uriString, int fallbackRes) {
            if (!TextUtils.isEmpty(uriString)) {
                try {
                    target.setImageURI(Uri.parse(uriString));
                    if (target.getDrawable() == null) {
                        target.setImageResource(fallbackRes);
                    }
                    return;
                } catch (Exception ignored) {
                    // Fall through to placeholder.
                }
            }
            target.setImageResource(fallbackRes);
        }
    }
}
