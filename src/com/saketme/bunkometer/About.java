package com.saketme.bunkometer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class About extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        //Set font
        Typeface font = Typeface.createFromAsset(getAssets(), "RobotoSlab-Light.ttf");
        TextView aboutSaket = (TextView)findViewById(R.id.about_saket_narayan);
        aboutSaket.setTypeface(font);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            ImageView coverImage = (ImageView)findViewById(R.id.about_us_cover);
            Drawable coverImageDrawable = getResources().getDrawable(R.drawable.about_activity_land);
            coverImage.setImageDrawable(coverImageDrawable);
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClick(View v){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "saket@saket.me", null));

        switch(v.getId()){
            case R.id.developer_icon:
                    DeveloperContact newObject = DeveloperContact.getInstance(this);
                    newObject.show(getFragmentManager(), "Developer Contact Dialog");
                    break;

            case R.id.rate_on_play_store_button:
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.saketme.bunkometer"));
                    startActivity(intent);
                    break;

            case R.id.about_submit_feedback_button:

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    String debug_info = "\n\n\n Device information \n -------------------------------";
                    try{
                        debug_info += "\n Bunk-o-Meter version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    }catch (PackageManager.NameNotFoundException nne){
                        Log.e("About", "Name not found exception");
                    }
                    debug_info += "\n Android Version: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT+ ") \n Model (and product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ") \n Device: " + android.os.Build.DEVICE;

                emailIntent.putExtra(Intent.EXTRA_TEXT, debug_info);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bunk-o-Meter: Feedback / bug report");
                    startActivity(Intent.createChooser(emailIntent, "Send feedback / bug report"));
                    break;

            case R.id.gplus_container:
                    String URL = "https://plus.google.com/u/0/105212495641541796066/posts";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
                    break;

            case R.id.email_container:
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n - Bunk-o-Meter user");
                    startActivity(Intent.createChooser(emailIntent, "Send email"));
                    break;

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