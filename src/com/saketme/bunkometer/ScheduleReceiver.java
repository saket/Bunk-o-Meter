package com.saketme.bunkometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class ScheduleReceiver extends BroadcastReceiver {

    private PendingIntent pintent;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.i("Schedule Receiver", "Broadcast received.");

        Intent i = new Intent(context, TimedNotification.class);
        pintent = PendingIntent.getService(context, 0, i, 0);
        mContext = context;

        // Only schedule for weekdays, Mon-Fri
        // Extensible for custom days (better-pickers has a recurrence picker now
        for (int day = 2; day < 7; ++day) {
            scheduleAlarm(day);
        }
    }

    private void scheduleAlarm(int dayOfWeek) {
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        //Get time instances
        //Calendar mCurrentTime = Calendar.getInstance();
        Calendar mTargetTime = Calendar.getInstance();

        //Get notification time preferences
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int mNotificationTimeHour = mPreferences.getInt("notification_time_hour", 19);
        int mNotificationTimeMinute = mPreferences.getInt("notification_time_minute", 30);

        //set target time
        mTargetTime.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        mTargetTime.set(Calendar.HOUR_OF_DAY, mNotificationTimeHour);
        mTargetTime.set(Calendar.MINUTE, mNotificationTimeMinute);
        mTargetTime.set(Calendar.SECOND, 0);
        mTargetTime.set(Calendar.MILLISECOND, 0);

        long START_TIME = mTargetTime.getTimeInMillis();

        long TIME_DELAY = 86400000;

        //Postpone notification if it's Sunday or the alarm time has passed
        if (START_TIME < System.currentTimeMillis() || mTargetTime.get(Calendar.DAY_OF_WEEK) == 1){
            /*
            if(mTargetTime.get(Calendar.DAY_OF_WEEK)==1)
                Log.w("Broadcast Receiver", "No classes on Sunday! Notification postponed to tomorrow");
            else
                Log.w("Broadcast Receiver", "Notification postponed to tomorrow.");
            */

            START_TIME += TIME_DELAY;
        }

        //Log.i("Schedule Receiver", "Broadcast received. Notification service will start at " + START_TIME);

        //TODO: Use RTC instead of RTC_WAKEUP
        alarm.setRepeating(AlarmManager.RTC, START_TIME, TIME_DELAY, pintent);

        //Log.i("Schedule Receiver", "Broadcast ending. Set alarm time: " + START_TIME);
    }
}
