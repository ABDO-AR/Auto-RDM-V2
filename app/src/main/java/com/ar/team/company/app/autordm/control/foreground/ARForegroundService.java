package com.ar.team.company.app.autordm.control.foreground;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

public class ARForegroundService extends Service {

    // Fields:
    public static final String CHANNEL_ID = "AutoRDMForeground";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Creating Notifications:
        createNotificationChannel();
        // Initializing:
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("AutoRDM").setContentText("AutoRDM service is running").setSmallIcon(R.drawable.ic_notification_small_icon).setContentIntent(pendingIntent).build();
        // StartingOurService:
        startForeground(1, notification);
        // Retaining:
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Methods(CreateNotificationsChannels):
    private void createNotificationChannel() {
        // Checking:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Initializing:
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "AutoRDM Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }

    public class CallBinder extends Binder {
        public ARForegroundService getService() {
            return ARForegroundService.this;
        }
    }
}
