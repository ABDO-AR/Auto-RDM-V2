package com.ar.team.company.app.autordm.control.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

@SuppressWarnings("unused")
public class ARNotificationManager {

    // Channels:
    public static final String CHANNEL_IMAGES_ID = "AutoRDMDeletedImagesMessage";
    public static final String CHANNEL_VIDEOS_ID = "AutoRDMDeletedVideosMessage";
    public static final String CHANNEL_VOICES_ID = "AutoRDMDeletedVoicesMessage";
    public static final String CHANNEL_DOCUMENTS_ID = "AutoRDMDeletedDocumentsMessage";

    // Method(ShowNotification)
    public static void showNotification(Context context, @StringRes int channelDes, String id) {
        // Creating:
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        createNotificationChannel(context, notificationManager, channelDes, id);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.drawable.ic_notification_small_icon)
                .setContentTitle(context.getString(R.string.channel_name))
                .setContentText(context.getString(channelDes))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(881231, builder.build());
    }

    // Method(Notification):
    public static void createNotificationChannel(Context context, NotificationManager notificationManager, @StringRes int channelDes, String id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(channelDes);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

}
