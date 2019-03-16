package com.example.team10.personalbest.ChatMessaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.team10.personalbest.FriendListPage;
import com.example.team10.personalbest.MessagePage;
import com.example.team10.personalbest.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "PersonalBestTeam10";
    private static final String TAG = "MyFirebaseMessagingService";
    private String userName;
    private String senderFragment;

    @Override
    public void onCreate() {
        super.onCreate();
        makeNotificationChannel();
        GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
        if(user != null) {
            String completeEmail = user.getEmail();
            userName = user.getDisplayName();
        }
        Log.d(TAG, "MyFirebaseMessagingService has been created");
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Ensures that messages from yourself will not give a notification
        String sender = remoteMessage.getNotification().getTitle();
        senderFragment = sender.substring(0, sender.indexOf(" sent"));
        super.onMessageReceived(remoteMessage);
        if(senderFragment.equals(userName)) {
            return;
        }
//        need to implement this if you want to do something when you receive a notification while app is in the foreground.
        Log.d(TAG, "Notification about to build");
        Log.d(TAG, userName + " is the userName");
        Log.d(TAG, senderFragment + " is the sender");
        Log.d(TAG, remoteMessage.getTo() + " is the receiver");

        //Set up intents
        Intent friendIntent = new Intent(this, FriendListPage.class);
        friendIntent.putExtra(getString(R.string.intent_email_key), "");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(friendIntent);
        PendingIntent friendPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(friendPendingIntent)
                .build();

        //TODO use PendingIntent.getActivity() with .setContentIntent(PendingIntent intent);
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(123, notification);
        Log.d(TAG, "Notification built");
    }

    private void makeNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notifications from Personal Best", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        Log.d(TAG, "A notification channel has been made");
    }
}
