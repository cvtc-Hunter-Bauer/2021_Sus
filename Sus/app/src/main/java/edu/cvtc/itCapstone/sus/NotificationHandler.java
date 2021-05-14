package edu.cvtc.itCapstone.sus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHandler extends BroadcastReceiver {
    //Member Constraints
    public static final String NOTIFICATION_TITLE = "edu.cvtc.itCapstone.sus_NOTIFICATION_TITLE";
    public static final String NOTIFICATION_TEXT = "edu.cvtc.itCapstone.sus_NOTIFICATION_TEXT";
    public static final String NOTIFICATION_ID = "edu.cvtc.itCapstone.sus_NOTIFICATION_ID";
    public static final String CHANNEL_ID = "channel_payments";

    // Member variables
    public String notificationTitle;
    public String notificationText;
    public int notificationId;


    @Override
    public void onReceive(Context context, Intent intent) {
        // Get all of the information sent in by our intent
        notificationTitle = intent.getStringExtra(NOTIFICATION_TITLE);
        notificationText = intent.getStringExtra(NOTIFICATION_TEXT);
        notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);

        // Build our notification using the provided information
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_payments);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationText);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);

        // Create the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Display the notification
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }
}
