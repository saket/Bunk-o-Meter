package com.saketme.bunkometer;
//Localizing strings complete.

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

public class TimedNotification extends IntentService {

    public TimedNotification(){
        super("TimedNotificationIntentService");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notificationsToggle = mPreferences.getBoolean("notification_enabled", true);

        if (notificationsToggle) {
            //Log.i("Notification","Notification is enabled. Service started");

            //Gets data from the incoming Intent
            Boolean isWelcomeNotification = intent.getBooleanExtra("Is_Welcome_Notification", false);
            Intent i;

            String mNotificationText, mNotificationTitle;
            int mNotificationLargeIcon;
            int mNotificationIcon = R.drawable.notif_icon;
            int mPriority = -3;
            String mNotificationTicker;

            Boolean vibChecked = mPreferences.getBoolean("notification_setting_vibration", true);
            Boolean soundChecked = mPreferences.getBoolean("notification_setting_sound", false);
            Boolean showIcon = mPreferences.getBoolean("notification_setting_icon", true);

            //TODO: FOR ANDROID 4.0??!?!

            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                mPriority = showIcon ? Notification.PRIORITY_DEFAULT : Notification.PRIORITY_MIN;

            if(isWelcomeNotification){
                i = new Intent(this, NotificationSettings.class);

                mNotificationTitle = getResources().getString(R.string.notification_title_welcome);
                mNotificationText = getResources().getString(R.string.notification_text_welcome);
                mNotificationTicker = getResources().getString(R.string.notification_ticker_welcome);
                mNotificationLargeIcon = R.drawable.notif_icon_large;
            }
            else{
                i = new Intent(this, MainActivity.class);

                mNotificationTitle = getResources().getString(R.string.notification_title);
                mNotificationText = getResources().getString(R.string.notification_text);
                mNotificationTicker = getResources().getString(R.string.notification_ticker);
                mNotificationLargeIcon = R.drawable.notif_icon_large_minimal;

            }

            // Prepare intent which is triggered if the notification is selected
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), mNotificationLargeIcon);

            // Build notification
            Notification notif;
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                notif = new Notification.Builder(this)
                        .setContentTitle(mNotificationTitle)
                        .setContentText(mNotificationText)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(mNotificationIcon)
                        .setPriority(mPriority)
                        .setTicker(mNotificationTicker)
                        .setContentIntent(pIntent).build();
            } else {
                notif = new Notification.Builder(this)
                        .setContentTitle(mNotificationTitle)
                        .setContentText(mNotificationText)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(mNotificationIcon)
                        .setTicker(mNotificationTicker)
                        .setContentIntent(pIntent).getNotification();
            }

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

            // Hide the notification after its selected
            notif.flags |= Notification.FLAG_AUTO_CANCEL;

            if(vibChecked && showIcon)
                notif.defaults |= Notification.DEFAULT_VIBRATE;

            if(soundChecked && showIcon)
                notif.defaults |= Notification.DEFAULT_SOUND;

            notificationManager.notify(0, notif);
        }
    }

    @Override
    public void onDestroy(){
        //Log.i("Intent Serivce", "Destroying service");
    }

}