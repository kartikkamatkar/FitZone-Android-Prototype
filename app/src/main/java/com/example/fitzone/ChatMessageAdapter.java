package com.example.fitzone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private final List<ChatMessage> messages = new ArrayList<>();

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class ChatMessageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView botAvatar;
        private final MaterialCardView messageBubble;
        private final TextView messageText;

        ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            botAvatar = itemView.findViewById(R.id.botAvatar);
            messageBubble = itemView.findViewById(R.id.messageBubble);
            messageText = itemView.findViewById(R.id.messageText);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getText());
            int sideOffset = dpToPx(52);
            int smallGap = dpToPx(8);
            int maxBubbleWidth = dpToPx(280);

            ConstraintLayout.LayoutParams bubbleParams =
                    (ConstraintLayout.LayoutParams) messageBubble.getLayoutParams();
            bubbleParams.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_WRAP;
            bubbleParams.matchConstraintMaxWidth = maxBubbleWidth;
            bubbleParams.width = 0;

            if (message.getSender() == ChatMessage.Sender.USER) {
                botAvatar.setVisibility(View.GONE);
                messageBubble.setCardBackgroundColor(itemView.getContext().getColor(R.color.fitzone_primary));
                messageText.setTextColor(itemView.getContext().getColor(android.R.color.white));
                bubbleParams.startToEnd = ConstraintLayout.LayoutParams.UNSET;
                bubbleParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                bubbleParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                bubbleParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
                bubbleParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                bubbleParams.horizontalBias = 1f;
                bubbleParams.setMarginStart(sideOffset);
                bubbleParams.leftMargin = sideOffset;
                bubbleParams.rightMargin = 0;
            } else {
                botAvatar.setVisibility(View.VISIBLE);
                messageBubble.setCardBackgroundColor(itemView.getContext().getColor(R.color.fitzone_chat_bot_bubble));
                messageText.setTextColor(itemView.getContext().getColor(R.color.fitzone_on_surface));
                bubbleParams.startToEnd = R.id.botAvatar;
                bubbleParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
                bubbleParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                bubbleParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
                bubbleParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                bubbleParams.horizontalBias = 0f;
                bubbleParams.setMarginStart(smallGap);
                bubbleParams.leftMargin = 0;
                bubbleParams.rightMargin = sideOffset;
            }

            messageBubble.setLayoutParams(bubbleParams);
        }

        private int dpToPx(int dp) {
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    itemView.getResources().getDisplayMetrics());
        }
    }
}

