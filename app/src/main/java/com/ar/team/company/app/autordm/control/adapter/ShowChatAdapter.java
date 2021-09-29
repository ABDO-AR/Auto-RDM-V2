package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.databinding.ShowChatItemViewBinding;
import com.ar.team.company.app.autordm.databinding.ShowChatItemViewMeBinding;
import com.ar.team.company.app.autordm.model.Chat;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ShowChatAdapter extends RecyclerView.Adapter {

    // Fields:
    private final Context context;
    private Chat chats;
    // ViewTypes:
    private static final int VIEW_TYPE_RECEIVED = 0;
    private static final int VIEW_TYPE_SENT = 1;

    // Constructor:
    public ShowChatAdapter(Context context, Chat chats) {
        // Initializing:
        this.context = context;
        this.chats = chats;
        // Notify:
        notifyDataSetChanged();
    }

    // Adapter:
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Initializing:
        LayoutInflater inflater = LayoutInflater.from(context);
        // Checking(Returning):
        if (viewType == VIEW_TYPE_RECEIVED) {
            // Initializing:
            ShowChatItemViewBinding binding = ShowChatItemViewBinding.inflate(inflater, parent, false);
            // Returning:
            return new ShowChatAdapter.ShowChatViewHolder(binding);
        } else {
            // Initializing:
            ShowChatItemViewMeBinding binding = ShowChatItemViewMeBinding.inflate(inflater, parent, false);
            // Returning:
            return new ShowChatAdapter.ShowChatViewHolderMe(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        // Initializing:
        Chat.Messages messages = chats.getMessages().get(position);
        String messageDate = messages.getMessageDate();
        // Checking:
        if (holder.getItemViewType() == VIEW_TYPE_RECEIVED) {
            // Developing:
            ((ShowChatViewHolder) holder).binding.chatMes.setText(messages.getMessage());
            ((ShowChatViewHolder) holder).binding.dateTextView.setText(messageDate);
        } else {

            // Developing:
            ((ShowChatViewHolderMe) holder).binding.chatMes.setText(messages.getMessage());
            ((ShowChatViewHolderMe) holder).binding.dateTextView.setText(messageDate);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Initializing:
        boolean sender = chats.getMessages().get(position).isSender();
        // Checking(Returning):
        if (sender) return VIEW_TYPE_RECEIVED;
        else return VIEW_TYPE_SENT;
    }

    @Override
    public int getItemCount() {
        return chats.getMessages().size();
    }

    // ViewHolders:
    static class ShowChatViewHolder extends RecyclerView.ViewHolder {

        // Fields:
        private final ShowChatItemViewBinding binding;

        // Constructor:
        public ShowChatViewHolder(@NonNull @NotNull ShowChatItemViewBinding binding) {
            super(binding.getRoot());
            // Initializing:
            this.binding = binding;
        }

        // Getters:
        public ShowChatItemViewBinding getBinding() {
            return binding;
        }
    }

    static class ShowChatViewHolderMe extends RecyclerView.ViewHolder {

        // Fields:
        private final ShowChatItemViewMeBinding binding;

        // Constructor:
        public ShowChatViewHolderMe(@NonNull @NotNull ShowChatItemViewMeBinding binding) {
            super(binding.getRoot());
            // Initializing:
            this.binding = binding;
        }

        // Getters:
        public ShowChatItemViewMeBinding getBinding() {
            return binding;
        }
    }

    // Getters:
    public Context getContext() {
        return context;
    }

    public Chat getChats() {
        return chats;
    }

}
