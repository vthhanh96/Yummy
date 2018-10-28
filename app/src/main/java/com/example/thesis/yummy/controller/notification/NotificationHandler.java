package com.example.thesis.yummy.controller.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Notification;
import com.squareup.moshi.JsonAdapter;

import java.io.IOException;

public class NotificationHandler {

    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";

    public static void createNotification(Context context, String data) {

        JsonAdapter<Notification> jsonAdapter = ServiceGenerator.getMoshiWithoutType(Notification.class).adapter(Notification.class);
        try {
            Notification notification = jsonAdapter.fromJson(data);
            if(notification == null) return;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(notification.mTitle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

                CharSequence name = "my_channel";
                String description = "This is my channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                channel.setShowBadge(true);

                if(notificationManager == null) return;
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(100, builder.build());

            } else {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(100, builder.build());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
