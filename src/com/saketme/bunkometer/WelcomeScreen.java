package com.saketme.bunkometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeScreen extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.welcome_screen);

        final Button startTourButton = (Button)findViewById(R.id.welcome_next_start);
        final TextView startTourButtonBorder = (TextView)findViewById(R.id.start_button_border);

        if(savedInstanceState != null && !savedInstanceState.getBoolean("showAnimation", false)){
            startTourButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: Change the intent
                    Intent i = new Intent(WelcomeScreen.this, Tour.class);
                    startActivity(i);
                    //overridePendingTransition(R.anim.next_screen_enter, R.anim.welcome_screen_exit);
                }
            });
        }else{
            playAnim(500, startTourButtonBorder, 250, R.anim.fade_in);
            playAnim(500, startTourButton, 250, R.anim.fade_in);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //bunkometerLogo.setVisibility(View.VISIBLE);
                    startTourButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO: Change the intent
                            Intent i = new Intent(WelcomeScreen.this, Tour.class);
                            startActivity(i);
                            //overridePendingTransition(R.anim.next_screen_enter, R.anim.welcome_screen_exit);
                        }
                    });
                }
            }, 500);

        }

    }

    protected void onSaveInstanceState (Bundle outState){
        outState.putBoolean("showAnimation", false);
    }

    public void playAnim(final int offset, final View itemView, int duration, int anim_id) {

        if (itemView != null) {
            Animation animation = AnimationUtils.loadAnimation(this, anim_id);
            if(offset!=0)
                animation.setStartOffset(offset);
            if(duration!=0)
                animation.setDuration(duration);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation paramAnimation) {}

                public void onAnimationRepeat(Animation paramAnimation) {}

                public void onAnimationEnd(Animation paramAnimation) {

                }
            });

            itemView.startAnimation(animation);
        }

    }
}