package com.saketme.bunkometer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;

public class NotificationSettings extends Activity {

    //Get shared preferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;
    private CheckBox soundSetting;
    private CheckBox vibrationSetting;
    private RelativeLayout notificationsContainer;
    private Toast toast;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPreferences.edit();
        notificationsContainer = (RelativeLayout)findViewById(R.id.notifications_container);

        //Get preferences
        Boolean vibChecked = mPreferences.getBoolean("notification_setting_vibration", true);
        vibrationSetting = (CheckBox)findViewById(R.id.notification_setting_vibration);
        vibrationSetting.setChecked(vibChecked);

        Boolean iconChecked = mPreferences.getBoolean("notification_setting_icon", true);
        CheckBox iconSetting = (CheckBox)findViewById(R.id.notification_setting_icon);
        iconSetting.setChecked(iconChecked);

        //Hide show icon settings if User is on < Jelly Bean
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            iconSetting.setVisibility(View.GONE);

        Boolean soundChecked = mPreferences.getBoolean("notification_setting_sound", false);
        soundSetting = (CheckBox)findViewById(R.id.notification_setting_sound);
        soundSetting.setChecked(soundChecked);
        soundSetting.setEnabled(iconChecked);
        vibrationSetting.setEnabled(iconChecked);

        Boolean notificationsToggle = mPreferences.getBoolean("notification_enabled", true);
        if(notificationsToggle){
            notificationsContainer.setVisibility(View.VISIBLE);
            findViewById(R.id.notification_turned_off).setVisibility(View.INVISIBLE);
        }
        else{
            notificationsContainer.setVisibility(View.INVISIBLE);
            findViewById(R.id.notification_turned_off).setVisibility(View.VISIBLE);
        }

        int mNotificationTimeHour = mPreferences.getInt("notification_time_hour", 19);
        int mNotificationTimeMinute = mPreferences.getInt("notification_time_minute", 30);

        //settings notification time
        setNotificationTime(mNotificationTimeHour, mNotificationTimeMinute);

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void onCheckboxClicked(View view){

        Boolean notificationsToggle = mPreferences.getBoolean("notification_enabled", true);
        if(notificationsToggle){

            //is the view now checked?
            boolean checked = ((CheckBox)view).isChecked();

            // Check which checkbox was clicked
            switch(view.getId()){

                case R.id.notification_setting_vibration:
                    if(checked){
                        editor.putBoolean("notification_setting_vibration", true);
                        editor.commit();
                    }
                    else{
                        editor.putBoolean("notification_setting_vibration", false);
                        editor.commit();
                    }
                    break;

                case R.id.notification_setting_sound:
                    if(checked){
                        editor.putBoolean("notification_setting_sound", true);
                        editor.commit();
                    }
                    else{
                        editor.putBoolean("notification_setting_sound", false);
                        editor.commit();
                    }
                    break;

                case R.id.notification_setting_icon:
                    if(checked){
                        editor.putBoolean("notification_setting_icon", true);
                        editor.commit();
                        soundSetting.setEnabled(true);
                        vibrationSetting.setEnabled(true);
                    }
                    else{
                        editor.putBoolean("notification_setting_icon", false);
                        editor.commit();
                        soundSetting.setEnabled(false);
                        vibrationSetting.setEnabled(false);
                    }
                    break;
            }
        }
    }

    public void testNotification(View view){
        Boolean notificationsToggle = mPreferences.getBoolean("notification_enabled", true);
        if(notificationsToggle){

            if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
                toast = Toast.makeText(this, "Showing test notification", Toast.LENGTH_SHORT);
                toast.show();
            }

            Intent serviceIntent = new Intent(this, TimedNotification.class);
            startService(serviceIntent);

        }
    }

    public void notificationTimePicker(View view){

        Boolean notificationsToggle = mPreferences.getBoolean("notification_enabled", true);

        if(notificationsToggle){

            TimePickerBuilder tpb = new TimePickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            tpb.addTimeSetListener(timeListener); //add here any listener implementing TimePickerDialogHandler
            tpb.show();

        }
    }

    private TimePickerDialogFragment.TimePickerDialogHandler timeListener = new TimePickerDialogFragment.TimePickerDialogHandler() {
        public void onDialogTimeSet(int selectedHour, int selectedMinute) {

                setNotificationTime(selectedHour, selectedMinute);

                /** Setting new alarm **/
                if (mPreferences.getInt("notification_time_hour", 19) != selectedHour || mPreferences.getInt("notification_time_minute", 30) != selectedMinute) {
                    Intent intent = new Intent();
                    intent.setAction("com.saketme.bunkometer.START_NOTIFICATION");
                    sendBroadcast(intent);
                }

                /** Setting new alarm time **/
                editor.putInt("notification_time_hour", selectedHour);
                editor.putInt("notification_time_minute", selectedMinute);
                editor.commit();

        }

        public void onDialogCancel(){
            //
        }

    };


    void setNotificationTime(int hourOfDay, int minute){
        TextView notificationTimeView = (TextView)findViewById(R.id.notification_time);

        int hourOfDay12Hours = hourOfDay;

        TextView notificationTimeMeridiem = (TextView)findViewById(R.id.notification_time_meridiem);
        if(hourOfDay>11){
            if(hourOfDay != 12)
                hourOfDay12Hours = hourOfDay - 12;
            notificationTimeMeridiem.setText(getResources().getString(R.string.pm));
        }
        else{
            hourOfDay12Hours = hourOfDay;
            notificationTimeMeridiem.setText(getResources().getString(R.string.am));
        }
        if(minute < 10){
            notificationTimeView.setText(hourOfDay12Hours + ":0" + minute);
        }
        else
            notificationTimeView.setText(hourOfDay12Hours + ":" + minute);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_notification_settings, menu);
        final Switch toggleSwitch = (Switch)((LinearLayout)menu.findItem(R.id.toggle_notification).getActionView()).getChildAt(0);

        Boolean notificationsToggle = mPreferences.getBoolean("notification_enabled", true);
        toggleSwitch.setChecked(notificationsToggle);

        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(checked){
                    editor.putBoolean("notification_enabled", true);
                    editor.commit();

                    playAnim(0, notificationsContainer, 400, R.anim.notification_enabled);
                    playAnim(0, findViewById(R.id.notification_turned_off), 200, R.anim.fade_out);

                }
                else{
                    editor.putBoolean("notification_enabled", false);
                    editor.commit();

                    playAnim(0, notificationsContainer, 400, R.anim.notification_disabled);
                    playAnim(250, findViewById(R.id.notification_turned_off), 300, R.anim.fade_in);

                }

            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    void playAnim(final int offset, final View itemView, int duration, int anim_id) {

        if (itemView != null) {
            Animation animation = AnimationUtils.loadAnimation(this, anim_id);
            animation.setDuration(duration);
            animation.setStartOffset(offset);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            itemView.startAnimation(animation);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

/*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.welcome_screen_enter, R.anim.prev_screen_exit);
    }
*/


}