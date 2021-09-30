package com.ar.team.company.app.autordm.control.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.model.ARIcon;
import com.ar.team.company.app.autordm.model.Chat;
import com.ar.team.company.app.autordm.ar.utils.ARUtils;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;
import com.ar.team.company.app.autordm.ui.activity.show.chat.ShowChatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class NotificationListener extends NotificationListenerService {

    // StaticFields:
    public static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    public static final List<Notification.Action> finalActions = new ArrayList<>();
    public static final List<ARIcon> icons = new ArrayList<>();
    // Channels:
    public static final String CHANNEL_ID = "AutoRDMDeletedMessage";
    // TAGS:
    private static final String TAG = "NotificationListener";
    private static final String NP_FIELD = "onNotificationPosted: ";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        // Checking:
        if (sbn.getPackageName() != null) {
            // Checking(WhatsAppPackageName):
            if (sbn.getPackageName().equals(WHATSAPP_PACKAGE_NAME)) {
                // Initializing(Date):
                String date = getCurrentDate();
                // Creating(Chats):
                List<Chat> chats;
                // Initializing(Data):
                String sender = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
                // Checking:
                if (sender != null) {
                    // CheckingFields:
                    boolean s1 = sbn.getPackageName().equals(WHATSAPP_PACKAGE_NAME);
                    boolean s2 = !sender.equals("WhatsApp") && !sender.equals("WhatsApp Web") && !sender.equals("WhatsApp Desktop");
                    boolean s3 = !sender.equals("Me") && !sender.equals("You");
                    boolean s4 = !sender.equals("Deleting messages...");
                    boolean state = s1 && s2 && s3 && s4;
                    // Fields:
                    ARPreferencesManager manager = new ARPreferencesManager(getApplicationContext());
                    String currentPackages = manager.getStringPreferences(ARPreferencesManager.PACKAGE_APP_NAME);
                    List<Chat.Messages> messages = new ArrayList<>();
                    // Initializing(Data):
                    String msg = sbn.getNotification().extras.getString(Notification.EXTRA_TEXT) + "";
                    String currentSenders = manager.getStringPreferences(ARPreferencesManager.SENDER_NAME);
                    // Checking:
                    boolean deletedMsgState = !msg.equals("This message was deleted");
                    // Design:
                    String firstChar;
                    // Trying:
                    try {
                        // Initializing:
                        firstChar = msg.split(" ")[0];
                    } catch (Exception e) {
                        // Trying:
                        try {
                            // Initializing:
                            firstChar = msg.substring(0, 1);
                        } catch (NullPointerException nullPointerException) {
                            // Initializing:
                            firstChar = "A";
                            // Debug:
                            Log.d(TAG, "onNotificationPosted: " + nullPointerException.toString());
                        }
                    }
                    // CheckingStatusBarNotification:
                    if (state && !msg.equals(firstChar + " new messages") && deletedMsgState) {
                        // Initializing(Replay):
                        Notification.WearableExtender extender = new Notification.WearableExtender(sbn.getNotification());
                        List<Notification.Action> actions = new ArrayList<>(extender.getActions());
                        // AddingAll:
                        finalActions.addAll(actions);
                        // Filtering(FinalActions):
                        List<Notification.Action> filteringAction = new ArrayList<>(new HashSet<>(finalActions));
                        // Clearing(FinalAction):
                        finalActions.clear();
                        // AddingAll(FilteringActions):
                        finalActions.addAll(filteringAction);
                        // Icons:
                        icons.add(new ARIcon(sender, sbn.getNotification().getLargeIcon()));
                        // AddingData:
                        messages.add(new Chat.Messages(msg.trim(), date, true));
                        // Developing:
                        if (manager.getPreferences().contains(ARPreferencesManager.WHATSAPP_CHATS)) {
                            // Initializing:
                            chats = ARUtils.fromJsonToChats(manager.getStringPreferences(ARPreferencesManager.WHATSAPP_CHATS));
                            // Developing:
                            for (Chat chat : chats) {
                                if (chat.getSender().equals(sender)) {
                                    // AddingTheNewMessage:
                                    if (!chat.getMessages().isEmpty()) {
                                        // Checking(MessagesSizeForRemovingDuplicates):
                                        if (!chat.getMessages().get(chat.getMessages().size() - 1).getMessage().equals(messages.get(0).getMessage())) {
                                            // Adding:
                                            chat.getMessages().addAll(messages);
                                            // SettingNewMessage:
                                            chat.setHasNewMessage(true);
                                            chat.setNewMessage(true);
                                            ARPreferencesManager.sender = chat.getSender();
                                        }
                                    } else {
                                        // Adding:
                                        chat.getMessages().addAll(messages);
                                        chat.setHasNewMessage(true);
                                    }
                                    // AddingSender($Preferences):
                                    if (!currentSenders.contains(sender)) {
                                        // Initializing:
                                        manager.setStringPreferences(ARPreferencesManager.SENDER_NAME, sender + ",");
                                        // Refreshing:
                                        currentSenders = manager.getStringPreferences(ARPreferencesManager.SENDER_NAME);
                                    }
                                }
                            }
                            // CheckingSenders:
                            if (!currentSenders.contains(sender)) {
                                // Initializing:
                                Chat chat = new Chat(sender, getCurrentDate(), messages);
                                chat.setHasNewMessage(true);
                                // AddingTheNewChat:
                                chats.add(chat);
                            }
                        } else {
                            // Initializing:
                            chats = new ArrayList<>();
                            Chat chat = new Chat(sender, getCurrentDate(), messages);
                            chat.setHasNewMessage(true);
                            // Developing:
                            chats.add(chat);
                        }
                        // SettingPreferences:
                        manager.setStringPreferences(ARPreferencesManager.WHATSAPP_CHATS, ARUtils.fromChatsToJson(chats));
                        // Debugging:
                        ARUtils.debug(TAG, NP_FIELD, manager.getStringPreferences(ARPreferencesManager.WHATSAPP_CHATS));
                    } else if (msg.equals("This message was deleted")) {
                        // Creating:
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        createNotificationChannel(notificationManager);
                        // Preparing:
                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(this, HomeActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification_small_icon)
                                .setContentTitle("AutoRDM")
                                .setContentText("Message Deleted Was Found")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(881231, builder.build());
                    }
                    // Debugging:
                    ARUtils.debug(TAG, NP_FIELD, "Whatsapp Package Was Founded In Preferences");
                    // Debugging:
                    ARUtils.debug(TAG, NP_FIELD, "Start");
                }
            }
        }
    }

    // Method(Notification):
    private void createNotificationChannel(NotificationManager notificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method(Date):
    private String getCurrentDate() {
        // Initializing(Calendar):
        Calendar calendar = Calendar.getInstance();
        // Preparing(Calendar):
        int hour24hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        String dayState = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM";
        // Setting(Date):
        return hour24hrs + ":" + minutes + dayState;
    }
}