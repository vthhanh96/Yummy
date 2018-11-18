package com.example.thesis.yummy.controller.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.loading.LoadingActivity;
import com.example.thesis.yummy.controller.meeting.MeetingDetailActivity;
import com.example.thesis.yummy.controller.post.PostDetailActivity;
import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Notification;
import com.example.thesis.yummy.restful.model.NotificationMeetingData;
import com.example.thesis.yummy.restful.model.NotificationPostData;
import com.squareup.moshi.JsonAdapter;

import java.io.IOException;

import static com.example.thesis.yummy.AppConstants.NOTIFICATION_MEETING;
import static com.example.thesis.yummy.AppConstants.NOTIFICATION_POST;

public class NotificationHandler {

    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";

    public static void createNotification(Context context, String data) {

        JsonAdapter<Notification> jsonAdapter = ServiceGenerator.getMoshiWithoutType(Notification.class).adapter(Notification.class);
        try {
            Notification notification = jsonAdapter.fromJson(data);
            if (notification == null || notification.notificationData == null) return;

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(notification.mTitle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setSound(defaultSoundUri);

            Intent intent = new Intent(context, LoadingActivity.class);
            switch (notification.notificationData.mType) {
                case NOTIFICATION_POST:
                    NotificationPostData postData = (NotificationPostData) notification.notificationData;
                    if (postData.mPost == null) break;
                    intent = PostDetailActivity.getPostDetailIntent(context, postData.mPost.mId);
                    break;
                case NOTIFICATION_MEETING:
                    NotificationMeetingData notificationMeetingData = (NotificationMeetingData) notification.notificationData;
                    if (notificationMeetingData.mMeeting == null) break;
                    intent = MeetingDetailActivity.getMeetingDetailIntent(context, notificationMeetingData.mMeeting.mId);
                    break;
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentIntent(pendingIntent);

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

                if (notificationManager == null) return;
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(notification.mId, builder.build());

            } else {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(notification.mId, builder.build());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
