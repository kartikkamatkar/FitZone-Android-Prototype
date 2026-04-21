package com.example.fitzone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ChatFragment extends Fragment {

    private ChatMessageAdapter chatMessageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        EditText chatInputEditText = view.findViewById(R.id.chatInputEditText);
        MaterialButton sendButton = view.findViewById(R.id.sendButton);

        chatMessageAdapter = new ChatMessageAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatRecyclerView.setAdapter(chatMessageAdapter);

        chatMessageAdapter.addMessage(new ChatMessage(
                getString(R.string.chat_bot_welcome),
                ChatMessage.Sender.BOT));

        sendButton.setOnClickListener(v -> {
            String question = chatInputEditText.getText().toString().trim();
            if (TextUtils.isEmpty(question)) {
                return;
            }

            chatMessageAdapter.addMessage(new ChatMessage(question, ChatMessage.Sender.USER));
            chatMessageAdapter.addMessage(new ChatMessage(getBotReply(question), ChatMessage.Sender.BOT));
            chatInputEditText.setText("");
            chatRecyclerView.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
        });

        return view;
    }


    private String getBotReply(String question) {
        String lower = question.toLowerCase(Locale.US);

        if (containsAny(lower, "hi", "hello", "hey", "good morning", "good evening")) {
            return getString(R.string.chat_reply_greeting);
        }
        if (containsAny(lower, "protein", "whey", "casein")) {
            return getString(R.string.chat_reply_protein);
        }
        if (containsAny(lower, "calorie", "calories", "deficit", "surplus")) {
            return getString(R.string.chat_reply_calories);
        }
        if (containsAny(lower, "meal", "breakfast", "lunch", "dinner", "snack", "eat before", "eat after")) {
            return getString(R.string.chat_reply_meal_timing);
        }
        if (containsAny(lower, "water", "hydration", "electrolyte")) {
            return getString(R.string.chat_reply_water);
        }
        if (containsAny(lower, "sleep", "rest", "recovery", "sore", "doms")) {
            return getString(R.string.chat_reply_recovery);
        }
        if (containsAny(lower, "bmi")) {
            return getString(R.string.chat_reply_bmi);
        }
        if (containsAny(lower, "weight", "fat", "lose", "cut")) {
            return getString(R.string.chat_reply_weight_loss);
        }
        if (containsAny(lower, "gain", "bulk", "muscle")) {
            return getString(R.string.chat_reply_muscle_gain);
        }
        if (containsAny(lower, "workout", "exercise", "training", "plan", "split")) {
            return getString(R.string.chat_reply_workout);
        }
        if (containsAny(lower, "steps", "walk", "cardio")) {
            return getString(R.string.chat_reply_steps);
        }
        if (containsAny(lower, "supplement", "creatine", "preworkout", "omega")) {
            return getString(R.string.chat_reply_supplements);
        }
        if (containsAny(lower, "injury", "pain", "knee", "back pain", "shoulder pain")) {
            return getString(R.string.chat_reply_injury);
        }
        if (containsAny(lower, "motivation", "tired", "lazy", "can't", "stuck")) {
            return getString(R.string.chat_reply_motivation);
        }
        if (containsAny(lower, "thank", "thanks")) {
            return getString(R.string.chat_reply_thanks);
        }
        return getString(R.string.chat_reply_default);
    }

    private boolean containsAny(String source, String... keywords) {
        for (String keyword : keywords) {
            if (source.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}

