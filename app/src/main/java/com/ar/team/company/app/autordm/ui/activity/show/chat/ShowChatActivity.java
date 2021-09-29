package com.ar.team.company.app.autordm.ui.activity.show.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.control.adapter.ShowChatAdapter;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.databinding.ActivityShowChatBinding;
import com.ar.team.company.app.autordm.model.Chat;
import com.ar.team.company.app.autordm.ar.utils.ARUtils;
import com.ar.team.company.app.autordm.ui.interfaces.OnChatButtonClicked;

import java.util.List;

import es.dmoral.toasty.Toasty;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ShowChatActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // This For Control The XML-Main Views:
    private ActivityShowChatBinding binding;
    // Adapter:
    private Chat chat;
    private Icon icon;
    private LinearLayoutManager layoutManager;
    private ShowChatAdapter adapter;
    private ARPreferencesManager manager;
    // TAGS:
    private static final String TAG = "ShowChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowChatBinding.inflate(getLayoutInflater()); // INFLATE THE LAYOUT.
        View view = binding.getRoot(); // GET ROOT [BY DEF(CONSTRAINT LAYOUT)].
        setContentView(view); // SET THE VIEW CONTENT TO THE (VIEW).
        // Initializing:
        manager = new ARPreferencesManager(this);
        chat = ARUtils.fromJsonToChat(getIntent().getExtras().getString("Chat"));
        icon = getIntent().getParcelableExtra("Icon");
        layoutManager = new LinearLayoutManager(this);
        // Preparing:
        layoutManager.setStackFromEnd(true);
        // Developing(Main-UI):
        binding.senderNameTextView.setText(chat.getSender());
        binding.backButton.setOnClickListener(v -> finish());
        // Developing(Icon):
        if (icon != null) binding.senderImageView.setImageDrawable(icon.loadDrawable(this));
        else binding.senderImageView.setImageResource(R.drawable.ic_placeholder);
        // Developing(RecyclerView):
        if (chat != null) {
            adapter = new ShowChatAdapter(this, chat);
            binding.showChatRecyclerView.setAdapter(adapter);
            binding.showChatRecyclerView.setLayoutManager(layoutManager);
        }
        // Developing(Manager):
        manager.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    // OnChatReSend:
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Initializing:
        List<Chat> chats = ARUtils.fromJsonToChats(manager.getStringPreferences(ARPreferencesManager.WHATSAPP_CHATS));
        Chat refreshingChat = null;
        // Getting(RefreshingChat):
        for (Chat chat : chats) {
            // Checking:
            if (chat.getSender().equals(this.chat.getSender())) refreshingChat = chat;
        }
        // Trying:
        try {
            // Checking:
            if (refreshingChat != null && refreshingChat.getMessages() != null) {
                // Refreshing(Adapter):
                adapter = new ShowChatAdapter(this, refreshingChat);
                // Refreshing(RecyclerView):
                binding.showChatRecyclerView.setAdapter(adapter);
                binding.showChatRecyclerView.setLayoutManager(layoutManager);
            }
        } catch (Exception e) {
            // Debug:
            Log.d(TAG, "onSharedPreferenceChanged: " + e.toString());
        }
    }

    // OnDestroy:
    @Override
    protected void onDestroy() {
        manager.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}