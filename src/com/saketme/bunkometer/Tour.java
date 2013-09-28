package com.saketme.bunkometer;
//String localization complete

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class Tour extends Activity {

    private ViewFlipper viewFlipper, viewFlipperCover;
    private int flip_count, readyColor, nextColor, flipperChildCount;
    private Button nextButton, prevButton;
    private String prevButtonLabel, nextButtonLabel, readyButtonLabel;
    private ImageView noteImage;
    private Typeface robotoCondensed = null;

    public Tour() {
        flip_count = 1;
        viewFlipperCover = null;
        viewFlipper = null;
        nextButton = null;
        prevButton = null;
        noteImage = null;
        prevButtonLabel = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour);

        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper_main);
        flipperChildCount = viewFlipper.getChildCount();
        viewFlipperCover = (ViewFlipper)findViewById(R.id.viewFlipper_cover);
        prevButton = (Button)findViewById(R.id.flipper_prev_button);
        nextButton = (Button)findViewById(R.id.flipper_next_button);
        noteImage = (ImageView)findViewById(R.id.flipper_note);


        //Set Roboto-condensed font for Android 4.0 users
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){

            if(robotoCondensed!=null) robotoCondensed = Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf");
            prevButton.setTypeface(robotoCondensed);
            nextButton.setTypeface(robotoCondensed);
        }

        prevButtonLabel = prevButton.getText().toString();
        nextButtonLabel = nextButton.getText().toString();
        readyButtonLabel = getResources().getString(R.string.tour_im_ready_button_label);

        prevButton.setVisibility(View.INVISIBLE);
        prevButton.setClickable(false);

        //Animating the first flipper view
        animateCover(350, 1, true);

        readyColor = getResources().getColor(R.color.ready_color);
        nextColor = getResources().getColor(R.color.subject_bg);
    }

    @SuppressWarnings("deprecation")
    void animateCover(final int offset, int coverID, Boolean direction){

        /** direction = true -> Forward
         *            = false -> Reverse */

        if(direction){
            switch(coverID){
                //Yo!
                case 1:

                    Drawable notes = this.getResources().getDrawable(R.drawable.note1);
                    noteImage.setImageDrawable(notes);
                    playAnim(offset, R.id.flipper_note, false, 500, R.anim.flipper_enter, 2);

                    break;

                //"Bunk"
                case 2:

                    playAnim(offset, R.id.flipper_sub_phy, false, 500, R.anim.flipper_enter, 3);
                    playAnim(offset, R.id.flipper_sub_maths, false, 500, R.anim.flipper_enter, 3);
                    playAnim(offset, R.id.flipper_sub_history, false, 500, R.anim.flipper_enter, 3);

                    break;

                //Budvisor
                case 3:
                    playAnim(offset, R.id.budvisor_hand, false, 500, R.anim.flipper_enter, 0);
                    playAnim(offset, R.id.budvisor_face, false, 500, R.anim.flipper_enter, 0);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(flip_count==3)
                                playAnim(0, R.id.budvisor_hand, true, 0, R.anim.budvisor_hand, 0);
                        }
                    },offset+650);


                    break;

                //Reminder
                case 4:
                    playAnim(offset, R.id.flipper_reminder, false, 500, R.anim.flipper_enter, 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(flip_count==4)
                                playAnim(0, R.id.flipper_reminder, false, 20, R.anim.reminder_vibrate, 0);
                        }
                    }, 900);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(flip_count==4)
                                playAnim(0, R.id.flipper_reminder, false, 20, R.anim.reminder_vibrate, 0);
                        }
                    }, 2100);

                    break;
            }
        }
        else{
            switch(coverID){
                //Yo! Reverse
                case 1:
                    playAnim(offset, R.id.flipper_note, false, 500, R.anim.flipper_exit, 0);
                    break;

                //"Bunk"
                case 2:

                    playAnim(offset, R.id.flipper_sub_phy, false, 500, R.anim.flipper_exit, 0);
                    playAnim(offset, R.id.flipper_sub_maths, false, 500, R.anim.flipper_exit, 0);
                    playAnim(offset, R.id.flipper_sub_history, false, 500, R.anim.flipper_exit, 0);

                    playAnim(offset, R.id.flipper_sub_phy_bunk, false, 500, R.anim.flipper_exit, 0);
                    playAnim(offset, R.id.flipper_sub_maths_bunk, false, 500, R.anim.flipper_exit, 0);
                    playAnim(offset, R.id.flipper_sub_history_bunk, false, 500, R.anim.flipper_exit, 0);

                    break;

                //Budvisor Reverse
                case 3:
                    playAnim(offset, R.id.budvisor_hand, false, 500, R.anim.flipper_exit, 0);
                    playAnim(offset, R.id.budvisor_face, false, 500, R.anim.flipper_exit, 0);
                    break;

                //Reminder Reverse
                case 4:
                    playAnim(offset, R.id.flipper_reminder, false, 500, R.anim.flipper_exit, 0);
                    break;
            }
        }
    }

    void playAnim(final int offset, int itemViewID, Boolean reverseOrNot, int duration, int anim_id, final int anim_code) {

        /* Animation code
		 * 1 = delete it
		 * 2 = animate note
		 * 3 = bunk! animation
        */

        final View itemView = findViewById(itemViewID);

        if (itemView != null) {
            Animation animation = AnimationUtils.loadAnimation(Tour.this, anim_id);
            if(offset!=0)
                animation.setStartOffset(offset);
            if(duration!=0)
                animation.setDuration(duration);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            if(!reverseOrNot)
                animation.setInterpolator(new OvershootInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation paramAnimation) {}

                public void onAnimationRepeat(Animation paramAnimation) {}

                public void onAnimationEnd(Animation paramAnimation) {

                    switch(anim_code){

                        case 1:
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((AnimationDrawable)itemView.getBackground()).start();
                                }
                            }, 100);
                            break;

                        case 2:
                            final Drawable notes[] = new Drawable[]{getResources().getDrawable(R.drawable.note1),
                                    getResources().getDrawable(R.drawable.note2),
                                    getResources().getDrawable(R.drawable.note3),
                                    getResources().getDrawable(R.drawable.note4),
                                    getResources().getDrawable(R.drawable.note5)};

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for(int i=0 ; i<5 ; i++){
                                        final int finalI = i;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(flip_count==1)
                                                    noteImage.setImageDrawable(notes[finalI]);
                                            }
                                        }, 105 + i*175);
                                    }
                                }
                            }, 100);
                            break;

                        case 3:
                            //bunk! animation
                            final ImageView phyBunk = (ImageView)findViewById(R.id.flipper_sub_phy_bunk);
                            final ImageView mathsBunk = (ImageView)findViewById(R.id.flipper_sub_maths_bunk);
                            final ImageView historyBunk = (ImageView)findViewById(R.id.flipper_sub_history_bunk);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            if(flip_count==2){
                                                phyBunk.setVisibility(View.VISIBLE);
                                                playAnim(1, R.id.flipper_sub_phy_bunk, true, 100, R.anim.subject_bunk, 0);
                                                playAnim(1, R.id.flipper_sub_phy, true, 100, R.anim.subject_bunk, 0);
                                            }
                                        }
                                    }, 200);
                                }
                            }, 100);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if(flip_count==2){
                                        mathsBunk.setVisibility(View.VISIBLE);
                                        playAnim(1, R.id.flipper_sub_maths_bunk, true, 100, R.anim.subject_bunk, 0);
                                        playAnim(1, R.id.flipper_sub_maths, true, 100, R.anim.subject_bunk, 0);
                                    }
                                }
                            }, 600);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if(flip_count==2){
                                        historyBunk.setVisibility(View.VISIBLE);
                                        playAnim(1, R.id.flipper_sub_history_bunk, true, 100, R.anim.subject_bunk, 0);
                                        playAnim(1, R.id.flipper_sub_history, true, 100, R.anim.subject_bunk, 0);
                                    }

                                }
                            }, 900);

                            break;

                    }
                }
            });

            itemView.startAnimation(animation);
        }

    }

    public void flipNext(View v){

        //open MainActivity
        if(flip_count == flipperChildCount){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Welcome tour won't be displayed from next launch
                    SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(Tour.this);
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean("hasRun", true);
                    editor.commit();
                }
            }).start();

            Intent NotificationIntent = new Intent();
            NotificationIntent .setAction("com.saketme.bunkometer.START_NOTIFICATION");
            sendBroadcast(NotificationIntent);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("No_Subjects_View_Delay_Time", 250);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        //flip
        else if(flip_count<flipperChildCount){

            animateCover(0, flip_count, false);
            animateCover(100, flip_count+1, true);

            //Log.i("Test", "Next pressed. Flip count: " + flip_count);

            viewFlipper.setInAnimation(Tour.this, R.anim.flipper_right_in);
            viewFlipper.setOutAnimation(Tour.this, R.anim.flipper_left_out);
            viewFlipper.showNext();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewFlipperCover.setInAnimation(Tour.this, R.anim.fade_in);
                    viewFlipperCover.setOutAnimation(Tour.this, R.anim.fade_out);
                    viewFlipperCover.showNext();
                }
            }, 100);



            ++flip_count;
        }

        //Switching between Next and I'm Ready labels
        if(flip_count == 0){
            prevButton.setVisibility(View.INVISIBLE );
            prevButton.setClickable(false);
        }
        else{
            prevButton.setVisibility(View.VISIBLE);
            prevButton.setClickable(true);

            nextButton.setText(flip_count == 4 ? readyButtonLabel : nextButtonLabel);
            nextButton.setTextColor(flip_count == 4 ? readyColor : nextColor);
        }
    }

    public void flipPrev(View v){

        if(flip_count>1){

            animateCover(0, flip_count, false);
            animateCover(100, flip_count-1, true);

            --flip_count;
            //Log.i("Test","Prev pressed. Flip count: " + flip_count);

            viewFlipper.setInAnimation(Tour.this, R.anim.flipper_left_in);
            viewFlipper.setOutAnimation(Tour.this, R.anim.flipper_right_out);
            viewFlipper.showPrevious();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewFlipperCover.setInAnimation(Tour.this, R.anim.fade_in);
                    viewFlipperCover.setOutAnimation(Tour.this, R.anim.fade_out);
                    viewFlipperCover.showPrevious();
                }
            }, 100);
        }

        //Log.w("Test", "Prev Flipping. Count: " + flip_count);

        if(flip_count == 1){
            prevButton.setVisibility(View.INVISIBLE);
            prevButton.setClickable(false);
        }
        else{
            prevButton.setVisibility(View.VISIBLE);
            prevButton.setClickable(true);

        }

        nextButton.setText(nextButtonLabel);
        nextButton.setTextColor(nextColor);

    }


}