package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.control.notifications.NotificationListener;
import com.ar.team.company.app.autordm.model.ARIcon;
import com.ar.team.company.app.autordm.model.Chat;
import com.ar.team.company.app.autordm.ui.activity.show.chat.ShowChatActivity;
import com.ar.team.company.app.autordm.ar.utils.ARUtils;
import com.ar.team.company.app.autordm.databinding.SingleChatItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    // Fields:
    private final Context context;
    private final List<Chat> chats;

    // Constructor:
    public ChatAdapter(Context context, List<Chat> chats) {
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
    public ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Initializing:
        LayoutInflater inflater = LayoutInflater.from(context);
        SingleChatItemBinding binding = SingleChatItemBinding.inflate(inflater, parent, false);
        // Returning:
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.ChatViewHolder holder, int position) {
        // Initializing:
        Chat chat = chats.get(position);
        Icon sendIcon = null;
        // Developing:
        holder.binding.senderNameTextView.setText(chat.getSender());
        holder.binding.dateTextView.setText(chat.getMessages().get(chat.getMessages().size() - 1).getMessageDate());
        holder.binding.lastMessageTextView.setText(chat.getMessages().get(chat.getMessages().size() - 1).getMessage());
        // PlaceHolderIcon:
        holder.binding.senderImageView.setImageResource(R.drawable.ic_placeholder);
        // Looping:
        for (ARIcon icon : NotificationListener.icons) {
            // Checking:
            if (icon.getId().contains(chat.getSender())) {
                // Checking:
                if (icon.getIcon() != null) {
                    // Initializing:
                    Drawable drawable = icon.getIcon().loadDrawable(context);
                    sendIcon = icon.getIcon();
                    // Developing:
                    holder.binding.senderImageView.setImageDrawable(drawable);
                }

            }
        }
        // OnClickChat:
        Icon finalSendIcon = sendIcon;
        holder.binding.messagesNumberTextView.setText(String.valueOf(chat.getMessages().size()));
        holder.binding.getRoot().setOnClickListener(v -> chatClicked(chat, finalSendIcon));
    }

    private void chatClicked(Chat chat, Icon icon) {
        // Initializing:
        Intent intent = new Intent(context, ShowChatActivity.class);
        // Developing:
        intent.putExtra("Icon", icon);
        intent.putExtra("Chat", ARUtils.fromChatToJson(chat));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    // ViewHolder:
    static class ChatViewHolder extends RecyclerView.ViewHolder {

        // Fields:
        private final SingleChatItemBinding binding;

        // Constructor:
        public ChatViewHolder(@NonNull @NotNull SingleChatItemBinding binding) {
            super(binding.getRoot());
            // Initializing:
            this.binding = binding;
        }

        // Getters:
        public SingleChatItemBinding getBinding() {
            return binding;
        }
    }

    // Getters:
    public Context getContext() {
        return context;
    }

    public List<Chat> getChats() {
        return chats;
    }
}