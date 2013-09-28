package com.saketme.bunkometer;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    private int unique_id;
    private Toast toast;
    private BrainHandler bh;
    private static DatabaseHandler dh = null;
    private Boolean showAnimation;
    public static int confusedCount = 1;
    private Boolean switchingToWelcomeScreen = false;

    //Card Details
    private String main_card_ID;
    private String main_Title;
    private String main_Face;
    private String main_Message;
    private String main_button;

    private Boolean main_show_card;
    private Boolean is_a_card_being_displayed;

    //temporary data for card
    private String temp_card_name;
    private String temp_card_id;

    //Main List
    private static LinearLayout MAIN_LAYOUT = null;

    //Subject Item
    private static RelativeLayout LONGPRESS_SUBJECT_VIEW = null;
    private Typeface slabFontLight, robotoCondensed;

    //Subject Elements
    private final List<RelativeLayout> LONG_PRESS_LAYOUT;
    private final List<TextView> SUB_NAME;
    private final List<TextView> PERCENTAGE_TEXT;
    private final List<TextView> BUNK_COUNT;
    private final List<TextView> BUNK_COUNT_HIDDEN;
    private final List<TextView> GREY_BUNK_COUNT;

    public MainActivity() {
        temp_card_id = null;
        temp_card_name = null;
        LONG_PRESS_LAYOUT = new ArrayList<RelativeLayout>();
        SUB_NAME = new ArrayList<TextView>();
        PERCENTAGE_TEXT = new ArrayList<TextView>();
        BUNK_COUNT = new ArrayList<TextView>();
        BUNK_COUNT_HIDDEN = new ArrayList<TextView>();
        GREY_BUNK_COUNT = new ArrayList<TextView>();
        slabFontLight = null;
        robotoCondensed = null;
        main_card_ID = null;
        main_Title = null;
        main_Face = null;
        main_Message = null;
        main_button = null;
        is_a_card_being_displayed = false;
        main_show_card = false;
        unique_id = 0;
        bh = null;
        showAnimation = true;

        dh = new DatabaseHandler(this);
        bh = new BrainHandler(this);

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //long startTime = System.currentTimeMillis();

        //Set the content View depending on whether there are any subjects or not
        if (countSubjects() == 0) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    /** IF APP IS BEING OPENED OPENED FOR THE FIRST TIME **/
                    final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    final Boolean hasRun = mPreferences.getBoolean("hasRun", false);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!hasRun){
                                switchingToWelcomeScreen = true;
                                Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                onBackPressed();
                            }
                            else
                                setNoSubjectsView(getIntent().getIntExtra("No_Subjects_View_Delay_Time", 100));
                        }
                    });
                }
            }).start();
        }

        else {

            if(savedInstanceState != null){
                /*
                // Card details
                main_card_ID = savedInstanceState.getString("main_card_ID", null);
                main_Title = savedInstanceState.getString("main_Title", null);
                main_Face = savedInstanceState.getString("main_Face", null);
                main_Message = savedInstanceState.getString("main_Message", null);
                main_button = savedInstanceState.getString("main_button", null);

                main_show_card = savedInstanceState.getBoolean("main_show_card", false);
                is_a_card_being_displayed = savedInstanceState.getBoolean("is_a_card_being_displayed", false);

                temp_card_name = savedInstanceState.getString("temp_card_name", null);
                temp_card_id = savedInstanceState.getString("temp_card_id", null);

                showAnimation = !savedInstanceState.getBoolean("justRotated", false);
                */
            }

            setMainSubjectsView();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //long startTime = System.currentTimeMillis();
                    final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    int runCount = mPreferences.getInt("runCount", 0);
                    //for avoiding increment of runCount when orientation changes
                    if(savedInstanceState == null){
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("runCount", ++runCount);
                        editor.commit();
                    }
                    //long endTime = System.currentTimeMillis();
                    //Log.w("MA","runCount thread took: " + (endTime - startTime) + " (ms)");
                }
            }).start();

            //long endTime = System.currentTimeMillis();
            //Log.w("MA","onCreate took: " + (endTime - startTime) + " (ms)");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    restoreSubjects();
                }

            }, getIntent().getIntExtra("restore_subjects_delay_time", 0));

        }

    }

    protected void onSaveInstanceState (Bundle outState){
        outState.putBoolean("justRotated", true);
/*
        // Card details /
        outState.putString("main_card_ID", main_card_ID);
        outState.putString("main_Title", main_Title);
        outState.putString("main_Face", main_Face);
        outState.putString("main_Message", main_Message);
        outState.putString("main_button", main_button);

        outState.putBoolean("main_show_card", main_show_card);
        outState.putBoolean("is_a_card_being_displayed", false);

        outState.putString("temp_card_name", temp_card_name);
        outState.putString("temp_card_id", temp_card_id);
*/
    }

    Boolean showSpreadWord(){

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int optimalRunCount = mPreferences.getInt("optimalRunCount", 10); //Ask for sharing after this many launch counts
        Boolean showSpreadWord = mPreferences.getBoolean("showSpreadWord", true);
        int runCount = mPreferences.getInt("runCount", 0);

        //Log.i("MA", "Run count: " + runCount + ". Optimal run count: " + optimalRunCount + ". Show spreadWord: " + showSpreadWord);

        if(runCount >= optimalRunCount && showSpreadWord){
            //Log.i("MA", "Spread word can be shown");
            return true;
        }

        //Log.i("MA", "Spread word can NOT be shown");
        return false;


    }

    public void addSubjectView(final String subject_name, final int bunk_count, final int limit, final int newOrNot, Boolean FLAG, final int totalSubs) {

       // long startTime = System.currentTimeMillis();

        /** IDs
         * 0 - subjectLayout
         * 200 - +1 button
         * 300 - View of name and count combined (for long pressing)
         *
         * 7777 - Card content
         * 8888 - Progress Bar in CloseCardAsyncTask
         * 9999 - Card
         */

        //If a subject is being added FOR THE FIRST TIME, switch to the main layout
        if (totalSubs == 0) {
            setMainSubjectsView();
        }

        if(slabFontLight==null)
            slabFontLight = Typeface.createFromAsset(getAssets(), "RobotoSlab-Light.ttf");

        //Inflate the Main Layout with Hidden Layout Views
        final LinearLayout SUBJECTS_CONTAINER = (LinearLayout) findViewById(R.id.main_subjects_container);
        final View subjectLayout = getLayoutInflater().inflate(R.layout.new_subject, null);
        subjectLayout.setId(unique_id);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = lp.rightMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        lp.bottomMargin = lp.leftMargin/2;
        SUBJECTS_CONTAINER.addView(subjectLayout, lp);

        new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                /*** SUBJECT NAME ***/
                //Adding unique IDs to new views
                TextView subjectview = (TextView) subjectLayout.findViewById(R.id.dynamic_subject_name);
                SUB_NAME.add(subjectview);

                //subjectview.setId(unique_id);
                subjectview.setTypeface(slabFontLight);
                subjectview.setText(subject_name);

                /*** BUNKED COUNTs ***/
                TextView bunkView = (TextView) subjectLayout.findViewById(R.id.dynamic_bunk_count);
                BUNK_COUNT.add(bunkView);

                TextView hiddenBunkView = (TextView) subjectLayout.findViewById(R.id.dynamic_bunk_count_hidden);
                BUNK_COUNT_HIDDEN.add(hiddenBunkView);

                //bunkView.setId(Integer.parseInt("10" + unique_id));
                bunkView.setText(String.valueOf(bunk_count%10));
                bunkView.setTypeface(slabFontLight);

                //hiddenBunkView.setId(Integer.parseInt("80" + unique_id));
                hiddenBunkView.setTypeface(slabFontLight);

                /*** GRAYED BUNK COUNTs **/
                TextView greyBunkView = (TextView) subjectLayout.findViewById(R.id.dynamic_bunk_count_grayed);
                GREY_BUNK_COUNT.add(greyBunkView);

                TextView hiddenGreyBunkView = (TextView) subjectLayout.findViewById(R.id.dynamic_bunk_count_grayed_hidden);

                //greyBunkView.setId(Integer.parseInt("50" + unique_id));
                greyBunkView.setTypeface(slabFontLight);

                //hiddenGreyBunkView.setId(Integer.parseInt("60" + unique_id));
                hiddenGreyBunkView.setTypeface(slabFontLight);

                if (bunk_count > 9) {
                    greyBunkView.setText(String.valueOf(bunk_count/10));
                }

                /*** PLUS 1 BUTTON = 200 + X ***/
                Button plus1view = (Button) subjectLayout.findViewById(R.id.dynamic_button_plus1);
                plus1view.setId(Integer.parseInt("20" + unique_id));

                if (bunk_count > limit || bunk_count == limit) {
                    disablePlus1Button(Integer.parseInt("20" + unique_id));
                }

                /** PERCENTAGE TEXT **/
                TextView percentageText = (TextView) subjectLayout.findViewById(R.id.dynamic_bunk_percentage_text);
                PERCENTAGE_TEXT.add(percentageText);

                /** REGISTERING LONG PRESS LISTENERS = 300 + X **/
                final String subject_press_id = "30" + unique_id;
                final RelativeLayout longpress = (RelativeLayout) subjectLayout.findViewById(R.id.dynamic_longpress_item);
                LONG_PRESS_LAYOUT.add(longpress);

                longpress.setId(Integer.parseInt(subject_press_id));
                /*
                longpress.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        //Inflate Modify and Delete buttons
                        View buttonContainer = getLayoutInflater().inflate(R.layout.long_press_buttons, null);

                        //Adding -8dp top margin
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.leftMargin = lp.rightMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                        lp.topMargin = -lp.leftMargin/2;
                        lp.bottomMargin = lp.leftMargin/2;
                        SUBJECTS_CONTAINER.addView(buttonContainer, view.getId()%100+1, lp);

                        return true;
                    }
                });
                */

                longpress.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(getResources().getString(R.string.select_action));
                        menu.add(0, Integer.parseInt(subject_press_id), 0, getResources().getString(R.string.modify_text));
                        menu.add(0, Integer.parseInt(subject_press_id), 0, getResources().getString(R.string.delete_text));
                    }
                });

                //Updating Percentage text
                updateBunkPercentage(unique_id, (float) bunk_count, (float) limit);

                if (newOrNot == 1) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //long startTime = System.currentTimeMillis();

                            //Adding the subject record to the table
                            Calendar c = Calendar.getInstance();
                            String date = (c.get(Calendar.DATE) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR));

                            //Log.i("MainActivity", "Adding date: " + date);
                            dh.addSubject(subject_name, bunk_count, limit, date);

                            //long endTime = System.currentTimeMillis();
                            //Log.d("MA","Adding subject to the DB took: " + (endTime - startTime) + " (ms)");
                        }
                    }).start();


                    final Drawable oldBG = longpress.getBackground();
                    Drawable yellowGlow = MainActivity.this.getResources().getDrawable(R.drawable.subject_bg_yellow_glow);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                        longpress.setBackgroundDrawable(yellowGlow);
                    else
                        longpress.setBackground(yellowGlow);

                    TransitionDrawable transition = (TransitionDrawable) longpress.getBackground();
                    transition.startTransition(1000);
                    transition.reverseTransition(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @SuppressWarnings("deprecation")
                                @Override
                                public void run() {
                                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                                        longpress.setBackgroundDrawable(oldBG);
                                    else
                                        longpress.setBackground(oldBG);
                                }

                            }, 1000);
                        }
                    });

                    final ScrollView scrollView = (ScrollView)findViewById(R.id.main_activity_scrollview);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                }

                            }, 400);
                        }
                    });


                }
                else if((unique_id+1) == totalSubs && !showSpreadWord() && !showAnimation){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCard(400);
                        }
                    });
                    //Log.w("Ma", "Either Inflating card or share view");
                }
                else if((unique_id+1) == totalSubs && showSpreadWord() && !showAnimation){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enjoyingBunkometer();
                        }
                    });
                }

                //Incrementing the unique ID for the next subject.
                unique_id++;

            }
        }).start();

        /** Showing Subject View Tip **/
        if((unique_id) == 0){
            if(countSubjects() != 1){
                FLAG = false;
            }
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if(mPreferences.getInt("subjectTourComplete", 0) == 0){
                subjectTourTip(mPreferences, mPreferences.getInt("subjectTourComplete", 0), FLAG);
            }
        }

        //Log.i("MA","AddSubjectView took: " + (System.currentTimeMillis() - startTime) + " (ms)");

    }

    void subjectTourTip(final SharedPreferences mPreferences, final int COUNT, Boolean FLAG){

        if(findViewById(R.id.tip_parent) == null){

            final View subjectTourTipContainer = getLayoutInflater().inflate(R.layout.subject_tour, null);
            LinearLayout SUBJECTS_CONTAINER = (LinearLayout) findViewById(R.id.main_subjects_container);
            SUBJECTS_CONTAINER.addView(subjectTourTipContainer, 1);

            //Log.i("MA", "FLAG: " + FLAG + " and Count: " + COUNT);

            if(FLAG && COUNT==0)
                playAnim(this, 0, subjectTourTipContainer, 0, R.anim.fade_in_slide_up_enter, 0);
            else
                playAnim(this, 300, subjectTourTipContainer, 0, R.anim.subject_tour_flipper_enter, 0);

            //Registering the Viewflipper and its buttons
            final ViewFlipper flipper = (ViewFlipper)subjectTourTipContainer.findViewById(R.id.subject_tour_viewFlipper);
            final Button okButton = (Button)subjectTourTipContainer.findViewById(R.id.subject_tour_ok_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int VIEW_COUNT = flipper.getDisplayedChild();

                    if(VIEW_COUNT == 2){
                        playAnim(MainActivity.this, 0, subjectTourTipContainer, 0, R.anim.subject_tour_flipper_exit, 1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putInt("subjectTourComplete", (COUNT + 1));
                                editor.commit();
                            }
                        }).start();

                        Intent serviceIntent = new Intent(MainActivity.this, TimedNotification.class);
                        serviceIntent.putExtra("Is_Welcome_Notification", true);
                        startService(serviceIntent);

                    }

                    if(VIEW_COUNT < 2){
                        flipper.setInAnimation(MainActivity.this, R.anim.subject_tour_flipper_enter);
                        flipper.setOutAnimation(MainActivity.this, R.anim.subject_tour_flipper_exit);
                        flipper.showNext();
                    }
                    if(flipper.getDisplayedChild()==2)
                        //Do something
                        okButton.setText(getResources().getString(R.string.got_it));

                }
            });
        }
    }

    void enjoyingBunkometer(){
        if(MAIN_LAYOUT == null)
            MAIN_LAYOUT = (LinearLayout) findViewById(R.id.main_activity_subjects_list);
        View spreadTheWordLayout = getLayoutInflater().inflate(R.layout.enjoying_bunkometer, null);
        MAIN_LAYOUT.addView(spreadTheWordLayout, 0);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        spreadTheWordLayout.setLayoutParams(lp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.i("MA", "Everything done. Now enabling buttons");
                //ENABLING +1 BUTTONS
                LinearLayout subsContainer = (LinearLayout)findViewById(R.id.main_subjects_container);
                int VIEW_COUNT = subsContainer.getChildCount();

                while(VIEW_COUNT > 0){
                    //Log.i("MA", "Searching for: " + (VIEW_COUNT+200-1));
                    if(findViewById(VIEW_COUNT+200-1)!=null){
                        (findViewById(VIEW_COUNT+200-1)).setEnabled(true);
                    }
                    --VIEW_COUNT;
                }
            }
        }, 0);

    }

    public void expandShareConfirmButtons(View view){

        final LinearLayout confirmButtons = (LinearLayout)findViewById(R.id.enjoying_bunkometer_buttons_container);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout spreadWordLayout = (LinearLayout)findViewById(R.id.spread_word_container);
        final int sixteenMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        if(confirmButtons == null){
        //Show sharing options
            if(MAIN_LAYOUT == null)
                MAIN_LAYOUT = (LinearLayout) findViewById(R.id.main_activity_subjects_list);

            //remove marginBottom from spreadWordContainer
            lp.bottomMargin = 0;
            (spreadWordLayout).setLayoutParams(lp);

            //add margin to spreadWordContainer and inflate it
            LinearLayout.LayoutParams lpButtons = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpButtons.bottomMargin = sixteenMargin;

            View spreadTheWordLayoutButtons = getLayoutInflater().inflate(R.layout.enjoying_bunkometer_buttons, null);
            MAIN_LAYOUT.addView(spreadTheWordLayoutButtons, 1, lpButtons);

            //toggling 'touch to respond' text
            ((TextView)findViewById(R.id.touch_to_respond)).setText(getResources().getString(R.string.touch_close));

            //toggling 'liking Bunk-o-meter' text
            playAnim(this, 0, R.id.liking_bunkometer, 200, R.anim.fade_out, 0);
            (findViewById(R.id.liking_bunkometer)).setVisibility(View.GONE);
            (findViewById(R.id.share_bunkometer_request)).setVisibility(View.VISIBLE);
            playAnim(this, 0, R.id.share_bunkometer_request, 200, R.anim.fade_in, 0);

            playAnim(this, 0, R.id.enjoying_bunkometer_buttons_container, 50, R.anim.fade_in, 0);

        }
        else{
            //Contract
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            animation.setStartOffset(0);
            animation.setDuration(200);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setAnimationListener(new AnimationListener() {

                public void onAnimationStart(Animation paramAnimation) {}

                public void onAnimationRepeat(Animation paramAnimation) {}

                public void onAnimationEnd(Animation paramAnimation) {

                            ((View) confirmButtons.getParent()).post(new Runnable() {
                                public void run() {
                                    ((LinearLayout) confirmButtons.getParent()).removeView(confirmButtons);
                                    lp.bottomMargin = sixteenMargin;
                                    spreadWordLayout.setLayoutParams(lp);
                                }
                            });

                    //toggling 'liking Bunk-o-meter text'
                    playAnim(MainActivity.this, 0, R.id.share_bunkometer_request, 200, R.anim.fade_out, 0);
                    (findViewById(R.id.share_bunkometer_request)).setVisibility(View.GONE);
                    (findViewById(R.id.liking_bunkometer)).setVisibility(View.VISIBLE);
                    playAnim(MainActivity.this, 0, R.id.liking_bunkometer, 200, R.anim.fade_in, 0);

                    //toggling 'touch to respond' text
                    ((TextView)findViewById(R.id.touch_to_respond)).setText("touch to respond");
                }
            });
            confirmButtons.startAnimation(animation);
        }
    }

    public void shareBunkometerOnClick(View view){

        String smileyFace = null;
        String smileyMessage = null;
        final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final SharedPreferences.Editor editor = mPreferences.edit();

        if(MAIN_LAYOUT == null)
            MAIN_LAYOUT = (LinearLayout) findViewById(R.id.main_activity_subjects_list);

        switch(view.getId()){

            case R.id.spread_word_no_button:
                        //Spread the word won't be shown anymore
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                editor.putBoolean("showSpreadWord", false);
                                editor.commit();
                            }
                        }).start();

                        smileyFace = ":/";
                        break;

            case R.id.spread_word_later_button:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                editor.putInt("optimalRunCount", mPreferences.getInt("runCount", 10) + 10);
                                editor.commit();
                            }
                        }).start();

                        smileyFace = ":)";

                        break;

            case R.id.spread_word_sure_button:
                        //Spread the word won't be shown anymore
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                editor.putBoolean("showSpreadWord", false);
                                editor.commit();
                            }
                        }).start();
                        smileyFace = ":D";
                        smileyMessage = getResources().getString(R.string.you_rock);
                        break;

        }

            //remove the spreadView and spreadViewButtons layouts
            LinearLayout spreadWord = (LinearLayout) MAIN_LAYOUT.getChildAt(0);
            LinearLayout spreadWordButtons = (LinearLayout) MAIN_LAYOUT.getChildAt(1);
            LinearLayout.LayoutParams lpOld = (LinearLayout.LayoutParams) spreadWordButtons.getLayoutParams();
            MAIN_LAYOUT.setLayoutTransition(null);
            MAIN_LAYOUT.removeView(spreadWord);
            MAIN_LAYOUT.removeView(spreadWordButtons);

            //animate removal
            playAnim(this, 0, spreadWord, 200, R.anim.fade_out, 0);
            playAnim(this, 0, spreadWordButtons, 200, R.anim.fade_out, 0);

            //Add result view with the same height and bottomMargin
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, spreadWord.getHeight() + spreadWordButtons.getHeight());
            lp.bottomMargin = lpOld.bottomMargin;
            final View spreadWordResult = getLayoutInflater().inflate(R.layout.enjoying_bunkometer_result, null);
            MAIN_LAYOUT.addView(spreadWordResult, 0, lp);

            //Change smiley
            LinearLayout smileyContainer= (LinearLayout)((LinearLayout)spreadWordResult).getChildAt(0);
            if(smileyFace != null)
                ((TextView)smileyContainer.getChildAt(0)).setText(smileyFace);

            if(smileyMessage != null){
                (smileyContainer.getChildAt(1)).setVisibility(View.VISIBLE);
                ((TextView)smileyContainer.getChildAt(1)).setText(smileyMessage);
            }
            playAnim(this, 0, smileyContainer, 500, R.anim.fade_in, 0);

            MAIN_LAYOUT.setLayoutTransition(new LayoutTransition());

            //remove it after 1.4 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MAIN_LAYOUT.removeView(spreadWordResult);
                    playAnim(MainActivity.this, 0, spreadWordResult, 300, R.anim.subject_tour_flipper_exit, 0);
                }
            }, 1400);

            //Tell a friend Dialog
            if(":D".equals(smileyFace)){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            ShareBunkometerDialog shareBunkometerDialog = ShareBunkometerDialog.getInstance(MainActivity.this);

                            //Attaching data (Subject details) with the Fragment
                            Bundle args = new Bundle();
                            args.putBoolean("showToast", false);
                            shareBunkometerDialog.setArguments(args);

                            shareBunkometerDialog.show(getFragmentManager(), "Share Bunkometer Dialog");
                    }
                }, 1000);

            }
    }

    void tellAFriendDialog(){

        int dialogDelay = youRock(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShareBunkometerDialog shareBunkometerDialog = ShareBunkometerDialog.getInstance(MainActivity.this);
                Boolean showToast;
                showToast = MAIN_LAYOUT == null;

                //Attaching data (Subject details) with the Fragment
                Bundle args = new Bundle();
                args.putBoolean("showToast", showToast);
                shareBunkometerDialog.setArguments(args);
                shareBunkometerDialog.show(getFragmentManager(), "Share_Bunkometer_Dialog");
            }
        }, dialogDelay);
/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("showSpreadWord", false);
                editor.commit();
            }
        }).start();
*/
    }

    public int youRock(Boolean rockOrNot){

        int dialogDelay = 0;
        LinearLayout oldSpreadWordResult = (LinearLayout)findViewById(R.id.spread_word_container);

        if(MAIN_LAYOUT != null){

            //Remove old spreadViews if they're present
            if(findViewById(R.id.spread_word_container) != null){
                MAIN_LAYOUT.setLayoutTransition(null);
                MAIN_LAYOUT.removeView(findViewById(R.id.spread_word_container));
                if(findViewById(R.id.enjoying_bunkometer_buttons_container) != null){
                    MAIN_LAYOUT.removeView(findViewById(R.id.enjoying_bunkometer_buttons_container));
                }
                MAIN_LAYOUT.setLayoutTransition(new LayoutTransition());
            }
            //remove the card if it's present
            else if(findViewById(7777) != null){
                findViewById(7777).setVisibility(View.GONE);
            }

            Log.i("MA", "Old spread word result container? " + oldSpreadWordResult);

            //Add result view with the same height and bottomMargin
            final View spreadWordResult = getLayoutInflater().inflate(R.layout.enjoying_bunkometer_result, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics()));
            lp.bottomMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            MAIN_LAYOUT.addView(spreadWordResult, 0, lp);

            if(rockOrNot)
                (findViewById(R.id.spread_word_result_rock_text)).setVisibility(View.VISIBLE);
            else
                ((TextView)findViewById(R.id.spread_word_result_smiley)).setText(":/");

            //remove it after 1.4 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MAIN_LAYOUT.removeView(spreadWordResult);
                    if(findViewById(7777) != null){
                        findViewById(7777).setVisibility(View.VISIBLE);
                    }
                }
            }, 1400);

            dialogDelay = 1400;
        }else{
            Toast.makeText(this, this.getResources().getString(R.string.you_rock), Toast.LENGTH_SHORT).show();
        }

        return dialogDelay;
    }

    void updateBunkPercentage(int view_id, float bunk_count, float limit) {

        //Per_fill_id = 700

        /** Colors:
         *  Excellent = #00c345
         * 	Good = #e1c003
         * 	Bad = #ff4444
         *
         * 	0xFF<color code>: FF is for white (alpha value). Dunno what 0 is for.
         *
         */

        float percentage = 0.0f;
        int color = getResources().getColor(R.color.bunk_excellent);
        int color_Grey = color;

        /*** CALCULATING THE PERCENTAGE ***/
        if (limit != 0)
            percentage = (bunk_count / limit);

        if (percentage > 0.66 || percentage == 0.66)
            color_Grey = color = getResources().getColor(R.color.bunk_bad); //Bad

        else if (percentage > 0.33 || percentage == 0.33)
            color_Grey = color = getResources().getColor(R.color.bunk_good); //Good

        if(bunk_count < 10.0)
            color_Grey = getResources().getColor(R.color.bunk_grey);

        TextView bunkCountView = BUNK_COUNT.get(view_id%100);
        final TextView GreyBunkCountView = GREY_BUNK_COUNT.get(view_id%100);
        bunkCountView.setTextColor(color);

        if(GreyBunkCountView.getCurrentTextColor() != color_Grey){

            if(bunk_count%10!=0){
                updateGreyColor(GreyBunkCountView, color_Grey, this);
            }
            else{
                GreyBunkCountView.setTextColor(color_Grey);
            }
        }
        //% bunked text
        TextView percentageText = PERCENTAGE_TEXT.get(view_id%100);
        percentageText.setText((int) (percentage * 100) + "% " + getResources().getString(R.string.bunked));

        playAnim(this, 0, percentageText, 300, R.anim.fade_in_half, 0 );

        if (percentage >= 0.89) {
            percentageText.setTextColor(getResources().getColor(R.color.percentage_alert));
        }
        else
            percentageText.setTextColor(getResources().getColor(R.color.percentage_normal));

    }

    private static void updateGreyColor(final TextView countView, final int color, Context context){

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in_half);
        animation.setStartOffset(0);
        animation.setDuration(700);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                countView.setTextColor(color);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        countView.startAnimation(animation);

    }

    static int countSubjects() {
        return dh.countSubjects();
    }

    @SuppressWarnings("deprecation")
    public void onClickPlus1(View v) {

        /** IDs
         * v.getId = 200
         *
         * 0 - Subject name
         * 100 - Bunk count
         * 200 - +1 button
         * 300 - View of name and count combined (for long pressing)
         * 400 - Super subject item
         * 500 - Greyed bunk count
         * 700 - Percentage Text
         */
        String current_date = Brain.getCurrentDate();
        //Log.i("MainActivity", "Bunk +1. Storing date as: " + current_date);

        //Bunk count and hidden bunk count
        TextView BUNK_COUNT_VIEW = BUNK_COUNT.get((v.getId()%100));
        TextView hiddenBunkCount = BUNK_COUNT_HIDDEN.get((v.getId()%100));
        int BUNK_COUNT = Integer.parseInt(BUNK_COUNT_VIEW.getText().toString());

        //Greyed bunk count
        TextView GREY_BUNK_COUNT_VIEW = GREY_BUNK_COUNT.get((v.getId()%100));
        TextView hiddenGreyedBunkCount = (TextView)((RelativeLayout)GREY_BUNK_COUNT_VIEW.getParent()).getChildAt(1);
        int grey_bunk_count = Integer.parseInt(GREY_BUNK_COUNT_VIEW.getText().toString());

        //Super Subject View
        LONGPRESS_SUBJECT_VIEW = LONG_PRESS_LAYOUT.get((v.getId()%100));

        int TOTAL_BUNK_COUNT = Integer.parseInt(String.valueOf(grey_bunk_count) + String.valueOf(BUNK_COUNT));

        //subject name
        TextView dynamic_subject_name = SUB_NAME.get((v.getId()%100));
        String subject_name = dynamic_subject_name.getText().toString();

        int limit = dh.getLimit(subject_name);

        TOTAL_BUNK_COUNT++;

        if (TOTAL_BUNK_COUNT < limit || TOTAL_BUNK_COUNT == limit) {

            //Update the bunk count
            speedometer(BUNK_COUNT_VIEW, hiddenBunkCount, TOTAL_BUNK_COUNT);

            if (TOTAL_BUNK_COUNT%10 == 0){
                speedometer(GREY_BUNK_COUNT_VIEW, hiddenGreyedBunkCount, TOTAL_BUNK_COUNT/10);
                //Log.i("Speedometer", "Grey count: " + hiddenGreyedBunkCount.getText());
            }

            //update it in DB
            dh.updateSubject(subject_name, TOTAL_BUNK_COUNT, limit, subject_name, current_date);

            updateBunkPercentage((v.getId() + 500), (float) TOTAL_BUNK_COUNT, (float) limit);

            if(!showSpreadWord()){
                updateCard(300);
            }
        } else {
            //Workaround for preventing multiple toast overlaps
            if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
                toast = Toast.makeText(this, "Limit reached. Enough bunking.", Toast.LENGTH_LONG);
                toast.show();
            }

            final Drawable old = LONGPRESS_SUBJECT_VIEW.getBackground();
            Drawable glow = MainActivity.this.getResources().getDrawable(R.drawable.subject_bg_red_glow);

            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                LONGPRESS_SUBJECT_VIEW.setBackgroundDrawable(glow);
            } else {
                LONGPRESS_SUBJECT_VIEW.setBackground(glow);
            }

            TransitionDrawable transition = (TransitionDrawable) LONGPRESS_SUBJECT_VIEW.getBackground();
            transition.startTransition(500);
            transition.reverseTransition(500);

            new Handler().postDelayed(new Runnable() {
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        LONGPRESS_SUBJECT_VIEW.setBackgroundDrawable(old);
                    } else {
                        LONGPRESS_SUBJECT_VIEW.setBackground(old);
                    }

                    //Log.w("Runnable", "Reverting BG");
                }

            }, 500);

            disablePlus1Button(v.getId());
        }
        if (TOTAL_BUNK_COUNT == limit) {
            disablePlus1Button(v.getId());
        }
    }

    void speedometer(final TextView countView, TextView hiddenCountView, int COUNT){

        //Update the bunk count
        countView.setText(String.valueOf(COUNT%10));
        countView.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bunk_count_enter);
        animation.setStartOffset(0);
        animation.setDuration(250);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        countView.startAnimation(animation);

        //Fake bunk count for animation
        hiddenCountView.setText(String.valueOf((COUNT-1)%10));
        hiddenCountView.setTextColor(countView.getCurrentTextColor());

        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.bunk_count_exit);
        animation2.setStartOffset(0);
        animation2.setDuration(250);
        animation2.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation2) {
            }

            @Override
            public void onAnimationEnd(Animation animation2) {
                countView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation2) {

            }
        });
        hiddenCountView.startAnimation(animation2);
    }

    @SuppressWarnings("deprecation")
    void disablePlus1Button(int button_id) {
        Button plus1button = (Button) findViewById(button_id);

        Drawable d = this.getResources().getDrawable(R.drawable.plusone_disabled);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            plus1button.setBackground(d);
        } else
            plus1button.setBackgroundDrawable(d);
    }

    @SuppressWarnings("deprecation")
    void enablePlus1Button(int button_id) {
        Button plus1button = (Button) findViewById(button_id);
        Drawable d = this.getResources().getDrawable(R.drawable.plus1_subject_button);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            plus1button.setBackground(d);
        } else
            plus1button.setBackgroundDrawable(d);

    }

    public void closeCard(View v) {

        LinearLayout card = (LinearLayout)findViewById(9999);

        if(card != null){
            playAnim(this, 0, 7777, 200, R.anim.fade_out, 0);
            playAnim(this, 100, 9999, 200, R.anim.close_card, 1);

            is_a_card_being_displayed = false;
            if(!"tipNoBunkForAWeek".equals(main_card_ID)){
                bh.setState(main_card_ID, "OFF");
                bh.setVisibility(main_card_ID, "HIDDEN");
            }
            //Log.i("MainActivity", "Setting card as HIDDEN");
        }

    }

    void updateCard(int delay) {

        if ("tipNoBunkForAWeek".equals(main_card_ID)){
            Toast.makeText(this, "Niiice!", Toast.LENGTH_SHORT).show();
            //Remove the card as well (if present)

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LinearLayout dismissButton = (LinearLayout) findViewById(R.id.card_close_button);
                    closeCard(dismissButton);
                }
            }, 200);

            main_card_ID = null;

        }
        else{
            final inflateCardAsync inflatea_async = new inflateCardAsync(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    inflatea_async.execute();
                }

            }, delay);
        }

    }

    void restoreSubjects() {

        //long startTime = System.currentTimeMillis();

        //Log.i("MainActivity", "Restoring subjects");
        List<Classes> subjects = dh.getAllContacts();
        int totalSubs = countSubjects();
        for (Classes cn : subjects) {
            addSubjectView(cn.getSubject(), cn.getBunkedClasses(), cn.getLimit(), 0, false, totalSubs);
        }

        //Now show animation
        if(showAnimation){
            AnimateSubjectEntry animateSubjectEntry = new AnimateSubjectEntry(this, countSubjects());
            animateSubjectEntry.execute();
        }

        //long endTime = System.currentTimeMillis();
        //Log.w("MA","Restore subjects took: " + (endTime - startTime) + " (ms)");
/*
    addSubjectView("Maths", 0, 10, 0, false);

    addSubjectView("Phyiscs", 0, 10, 0, false);
    addSubjectView("Chemistry", 0, 10, 0, false);
    addSubjectView("Geology", 0, 10, 0, false);
    addSubjectView("COA", 0, 10, 0, false);
    addSubjectView("Software Engg.", 0, 10, 0, false);
*/

    }

    public void deleteSubjectFromDB(final String subject_name, int view_to_delete_id) {

        int OFFSET = 300;

        if(countSubjects()==1 && is_a_card_being_displayed){
            OFFSET = 500;
        }

        View subLongPress = (View)findViewById(view_to_delete_id).getParent();
        playAnim(this, 300, subLongPress, 200, R.anim.delete_subject, 1);

        DatabaseHandler dhThread = new DatabaseHandler(MainActivity.this);
        dhThread.deleteSubject(subject_name);

        updateCard(OFFSET+300);

        //Changing the content View
        if (countSubjects() == 0){
            playAnim(MainActivity.this, OFFSET, R.id.tip_layout, 350, R.anim.fade_out, 1);
            playAnim(MainActivity.this, OFFSET + 300, R.id.spread_word_container, 350, R.anim.fade_out, 1);
            playAnim(MainActivity.this, OFFSET + 300, R.id.spread_word_result_container, 350, R.anim.fade_out, 1);
            playAnim(MainActivity.this, OFFSET + 300, R.id.enjoying_bunkometer_buttons_container, 350, R.anim.fade_out, 1);

            playAnim(MainActivity.this, OFFSET + 600, R.id.main_activity_subjects_label, 350, R.anim.main_activity_exit, 2);

            //reset options for SPREAD THE WORD
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatabaseHandler dh = new DatabaseHandler(MainActivity.this);
                    BrainHandler bh = new BrainHandler(MainActivity.this);
                    dh.dropTable();
                    bh.resetAll();
                }
            }).start();
        }

        LinearLayout subjectTour = (LinearLayout)findViewById(R.id.tip_parent);

        View previousView = findViewById(view_to_delete_id-1);

        if(subjectTour != null && (previousView == null) && (countSubjects() >= 1)){
            //Means subject tour tip is present. So let's re-position it now.
            playAnim(MainActivity.this, OFFSET, R.id.tip_parent, 350, R.anim.subject_tour_flipper_exit, 1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            subjectTourTip(mPreferences, 1, false);
                        }
                    });
                }

            }, 1000);

        }

    }

    public void deleteAllSubjectsFromDB(){

        int VIEW_COUNT;
        int TIME_BREAK = 100;
        int i=0;

        LinearLayout subsContainer = (LinearLayout)findViewById(R.id.main_subjects_container);
        if(subsContainer!=null){
            VIEW_COUNT = subsContainer.getChildCount();
            if(is_a_card_being_displayed){
                TIME_BREAK = 550;
            }
            //Log.i("MA", "Total subjects to delete: " + (VIEW_COUNT - i));

            //Remove the card as well (if present)
            LinearLayout dismissButton = (LinearLayout) findViewById(R.id.card_close_button);
            closeCard(dismissButton);

            while(VIEW_COUNT > i){

                if((VIEW_COUNT-1) == i){
                    //Log.i("MA", "last subject");
                    playAnim(this, TIME_BREAK, subsContainer.getChildAt(VIEW_COUNT-1), 350, R.anim.delete_subject, 3);
                }
                else
                    playAnim(this, TIME_BREAK, subsContainer.getChildAt(VIEW_COUNT-1), 350, R.anim.delete_subject, 1);
                --VIEW_COUNT;
                TIME_BREAK += 150;
            }

            if(findViewById(R.id.spread_word_container)!=null){
                playAnim(this, TIME_BREAK, R.id.spread_word_container, 300, R.anim.fade_out, 1);
            }

            //reset options for SPREAD THE WORD
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dh.dropTable();
                    bh.resetAll();
                }
            }).start();
        }

    }

    void playAnim(final Context context, final int offset, final View itemView, int duration, int anim_id, final int anim_code) {

        /* Animation codes
		 * 0 = do main_subjects_enter_after_adding_subs
		 * 1 = delete it
		 * 2 = set ContentView to no_subjects
		 * 3 = delete all -> change contentView to no_subjects enter
		 * 4 = undo delete view
		 */

        if (itemView != null) {
            Animation animation = AnimationUtils.loadAnimation(context, anim_id);
            if(offset!=0)
                animation.setStartOffset(offset);
            if(duration!=0)
                animation.setDuration(duration);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setAnimationListener(new AnimationListener() {

                public void onAnimationStart(Animation paramAnimation) {}

                public void onAnimationRepeat(Animation paramAnimation) {}

                public void onAnimationEnd(Animation paramAnimation) {

                    switch(anim_code){

                        case 1:
                                ((View) itemView.getParent()).post(new Runnable() {
                                    public void run() {
                                        ((LinearLayout) itemView.getParent()).removeView(itemView);
                                    }
                                });
                                break;

                        case 2:
                                playAnim(MainActivity.this, 0, R.layout.activity_main, 100, R.anim.fade_out, 0);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setNoSubjectsView(-100);
                                    }

                                }, 100);
                                break;

                        case 3:
                            ((View) itemView.getParent()).post(new Runnable() {
                                public void run() {
                                    ((LinearLayout) itemView.getParent()).removeView(itemView);
                                }
                            });
                            playAnim(MainActivity.this, 0, R.id.main_activity_subjects_label, 350, R.anim.main_activity_exit, 2);
                            break;

                    }
                }
            });

            itemView.startAnimation(animation);
        }

    }

    void playAnim(final Context context, final int offset, int view_id, int duration, int anim_id, final int anim_code) {
        View subView = findViewById(view_id);
        playAnim(context, offset, subView, duration, anim_id,  anim_code);

    }

    void setMainSubjectsView(){
        setContentView(R.layout.activity_main);
        MAIN_LAYOUT = (LinearLayout) findViewById(R.id.main_activity_subjects_list);

        TextView subjectsText = (TextView)findViewById(R.id.main_activity_subjects_label);

        //Set Roboto-condensed font for Android 4.0 users
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){
            if(robotoCondensed!=null) robotoCondensed = Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf");
            subjectsText.setTypeface(robotoCondensed);
        }

        playAnim(this, getIntent().getIntExtra("subjects_text_anim_delay", 200), subjectsText, 200, R.anim.notification_enabled, 0);

    }

    void setNoSubjectsView(int OFFSET){

        bh.resetAll();

        int ARROW_EXTRA_OFFSET = 300;

        setContentView(R.layout.no_subjects);

        //Set Roboto-condensed font for Android 4.0 users
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){

            TextView emptyText = (TextView)findViewById(R.id.no_subjects_title);

            if(robotoCondensed!=null) robotoCondensed = Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf");
            emptyText.setTypeface(robotoCondensed);
        }

        MAIN_LAYOUT=null;

        ImageView psstArrow = (ImageView)findViewById(R.id.psst_arrow);

        //Detect whether a hardware menu is present or not
        if(ViewConfiguration.get(this).hasPermanentMenuKey()){

            //remove the right margin for psst arrow
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(psstArrow.getLayoutParams());
            params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
            params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            psstArrow.setLayoutParams(params);

        }

         playAnim(MainActivity.this, OFFSET, R.id.no_subjects_message, 400, R.anim.slide_up_enter, 0);
        if(OFFSET<0) OFFSET = 0;
        playAnim(MainActivity.this, ARROW_EXTRA_OFFSET + OFFSET, psstArrow, 250, R.anim.fade_in, 0);

    }

    public void updateSubjectInDB(final String subject_name, final int bunk_count, final int bunk_limit, final String old_subject_name, int id_of_subject_being_modified) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dh.updateSubject(subject_name, bunk_count, bunk_limit, old_subject_name, null);
            }
        }).start();

        /** Here, 'id_of_subject_being_modified' = Subject view (name + bunk count) for long pressing **/
        //id_of_subject_being_modified = 300

        //Update the subject name
        TextView modify_subject_name = SUB_NAME.get(id_of_subject_being_modified%100);
        modify_subject_name.setText(subject_name);

        //Update the bunk count
        TextView modify_bunk_count = BUNK_COUNT.get(id_of_subject_being_modified%100);
        modify_bunk_count.setText(String.valueOf(bunk_count % 10));

        //Update the greyed bunk count
        TextView greyedBunkCount = GREY_BUNK_COUNT.get(id_of_subject_being_modified%100);
        greyedBunkCount.setText(String.valueOf(bunk_count/10));

        //Update the percentage bar
        updateBunkPercentage((id_of_subject_being_modified + 400), bunk_count, bunk_limit);

        //Update Plus 1 button
        if (bunk_count < bunk_limit) {
            enablePlus1Button((id_of_subject_being_modified - 100));
        }

        if (bunk_count == bunk_limit) {
            Button plusOneButton = (Button) findViewById(id_of_subject_being_modified-100);
            disablePlus1Button(plusOneButton.getId());
        }

        updateCard(300);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem m) {
        switch (m.getItemId()) {
            case R.id.main_activity_add_subject:
                //TODO: countSubjects=0
                if(countSubjects()==0){
                    Intent intent = new Intent(this, AddSubjects.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("transfer", false);
                    startActivity(intent);
                    overridePendingTransition(R.anim.add_subjects_enter, R.anim.fade_out_half);
                }else{
                    NewSubjectDialog newsubjectobject = NewSubjectDialog.newInstance();

                    //Attaching data (Subject details) with the Fragment
                    Bundle args = new Bundle();
                    args.putString("title", getResources().getString(R.string.new_subject_title));
                    args.putString("subject_name", null);
                    args.putString("bunk_count", null);
                    args.putString("bunk_limit", null);
                    args.putString("ok_text", "Save");
                    args.putBoolean("new_or_not", true);
                    newsubjectobject.setArguments(args);

                    newsubjectobject.show(getFragmentManager(), "New Subject Dialog");
                }

                return true;
/*
            case R.id.main_activity_raw_table:
                Intent i = new Intent(this, Subjects_list.class);
                startActivity(i);
                return true;
*/
            case R.id.menu_tell_a_friend:
                tellAFriendDialog();
                return true;

            case R.id.menu_notification_settings:
                startActivity(new Intent(this, NotificationSettings.class));
                //overridePendingTransition(R.anim.next_screen_enter, R.anim.welcome_screen_exit);
                return true;

            case R.id.menu_about:
                startActivity(new Intent(this, About.class));
                //overridePendingTransition(R.anim.next_screen_enter, R.anim.welcome_screen_exit);
                return true;

            case R.id.menu_delete_all:

                if(countSubjects() == 0){
                    Toast.makeText(this, getResources().getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
                }
                else{
                    DeleteAllSubjectsDialog newObject = DeleteAllSubjectsDialog.getInstance(this);
                    newObject.show(getFragmentManager(), "Delete All Subjects Dialog");
                }
                return true;

            default:
                return super.onOptionsItemSelected(m);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle() == getResources().getString(R.string.modify_text)) {

            NewSubjectDialog newsubjectobject = NewSubjectDialog.newInstance();

            //get the row the clicked layout is in
            LONGPRESS_SUBJECT_VIEW = LONG_PRESS_LAYOUT.get(item.getItemId()%100);
            TextView childSubject = SUB_NAME.get(item.getItemId()%100);

            //Bunk count
            TextView childBunkCount = BUNK_COUNT.get(item.getItemId()%100);
            int BUNK_COUNT = Integer.parseInt(childBunkCount.getText().toString());

            //Grey bunk count
            TextView greyedBunkCount = GREY_BUNK_COUNT.get(item.getItemId()%100);
            int GREY_BUNK_COUNT = Integer.parseInt(greyedBunkCount.getText().toString());

            int TOTAL_BUNK_COUNT = Integer.parseInt(String.valueOf(GREY_BUNK_COUNT) + String.valueOf(BUNK_COUNT));

            //Attaching data (Subject details) with the Fragment
            Bundle args = new Bundle();
            args.putString("subject_name", childSubject.getText().toString());
            args.putString("bunk_count", String.valueOf(TOTAL_BUNK_COUNT));
            args.putString("title", getResources().getString(R.string.modify_text_caps) + childSubject.getText().toString().toUpperCase());
            args.putString("ok_text", "Update");
            args.putBoolean("new_or_not", false);
            args.putInt("view_id_being_modified", item.getItemId());
            newsubjectobject.setArguments(args);

            newsubjectobject.show(getFragmentManager(), "New Subject Dialog");

        } else {
            Confirm_delete confirm = Confirm_delete.newInstance();

            /** ItemID = 300 **/

            //get the row the clicked layout is in
            TextView subjectName = SUB_NAME.get(item.getItemId()%100);
            String mSubjectName = subjectName.getText().toString();

            //Attaching data (Subject details) with the Fragment
            Bundle args = new Bundle();
            args.putString("subject_name", mSubjectName);
            args.putInt("view_to_delete", item.getItemId());
            args.putInt("confused_count", confusedCount);

            confirm.setArguments(args);

            confirm.show(getFragmentManager(), "Confirm Delete Dialog");

        }
        return true;

    }

    class AnimateSubjectEntry extends AsyncTask<Void, Void, Void>{

        Context context = null;
        final int totalSubs;
        LinearLayout subjectsContainer = null;
        Animation animation;

        AnimateSubjectEntry(Context context, int totalSubs){
            this.context = context;
            this.totalSubs = totalSubs;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            animation = AnimationUtils.loadAnimation(context, R.anim.subjects_enter);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            subjectsContainer = (LinearLayout)findViewById(R.id.main_subjects_container);
            if(subjectsContainer != null){
                animation.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation paramAnimation) {}
                    public void onAnimationRepeat(Animation paramAnimation) {}
                    public void onAnimationEnd(Animation paramAnimation) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!showSpreadWord()){ updateCard(0);}
                                else enjoyingBunkometer();
                            }
                        });
                    }
                });
                subjectsContainer.startAnimation(animation);
            }
            return null;
        }

    }

    class inflateCardAsync extends AsyncTask<Void, Void, Void> {

        Brain bObj = null;
        Boolean roll_back = false;
        final Context context;
        Boolean ISTHECARDNEW = false;
        LinearLayout subsContainer = null;

        public inflateCardAsync(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            //Disabling +1 buttons
            subsContainer = (LinearLayout)findViewById(R.id.main_subjects_container);
            int VIEW_COUNT = subsContainer.getChildCount();

            while(VIEW_COUNT > 0){
                //Log.i("MA", "Searching for: " + (VIEW_COUNT+200-1));
                if(findViewById(VIEW_COUNT+200-1)!=null){
                    (findViewById(VIEW_COUNT+200-1)).setEnabled(false);
                }
                --VIEW_COUNT;
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            //Log.i("InflateAsync","Values -> main_show_card: " + main_show_card + ", is_a_card_being_displayed: " + is_a_card_being_displayed);

            //if(showAnimation || !main_show_card){

                try {
                    bObj = new Brain(MainActivity.this);

                    temp_card_id = bObj.Card_ID;
                    temp_card_name = bObj.Title;

                    if (temp_card_id != null) {
                        if (bh.getVisibility(temp_card_id)) {
                            MainActivity.this.main_card_ID = temp_card_id;

                            if(!temp_card_name.equals(MainActivity.this.main_Title))
                                ISTHECARDNEW = true;

                                MainActivity.this.main_Title = temp_card_name;
                                MainActivity.this.main_Message = bObj.Message;
                                MainActivity.this.main_show_card = bObj.show_card;
                                MainActivity.this.main_button = bObj.Dismiss_button;

                        }
                    } else {
                        main_show_card = false;
                        roll_back = true;
                    }


                } catch (ParseException e) {
                    //Log.e("MainActivity", "Some parse exception has occurred");
                    e.printStackTrace();
                }
            //}

            return null;
        }

        protected void onPostExecute(Void result) {

            int fade_duration = 300;
            int OFFSET = 1;

            if (main_show_card && bh.getVisibility(main_card_ID)) {
                //Log.i("MainActivity", "Displaying a card");

                if (!is_a_card_being_displayed) {
                    //Log.w("MainActivity", "No card present. Let's inflate a new one.");

                    final View cardLayout = getLayoutInflater().inflate(R.layout.card, null);
                    //Making the card invisible and adding it
                    cardLayout.setVisibility(View.INVISIBLE);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    MAIN_LAYOUT.addView(cardLayout, 0, params);

                    //Setting the card content as invisible too, so that the placeholder stuff are not displayed
                    LinearLayout cardContent = (LinearLayout)findViewById(R.id.card_parent);
                    cardContent.setVisibility(View.INVISIBLE);
                    cardContent.setId(7777);

                    if(showAnimation){
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.card_inflate);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.setAnimationListener(new AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                //Making the card visible again
                                cardLayout.setVisibility(View.VISIBLE);

                                //disable dismiss button while the card is being displayed
                                findViewById(R.id.card_close_button).setClickable(false);
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        cardLayout.startAnimation(animation);
                        OFFSET = 700;
                    }
                    else{
                        cardLayout.setVisibility(View.VISIBLE);
                        OFFSET = 300;
                    }

                    //Adding an ID to the card
                    cardLayout.setId(9999);
                    is_a_card_being_displayed = true;
                    bh.setState(main_card_ID, "ON");
                    bh.setVisibility(main_card_ID, "VISIBLE");

                }

                LinearLayout cardContent = (LinearLayout)findViewById(7777);

                //Show fade animation
                if(is_a_card_being_displayed && cardContent!=null){

                    //if(!ISTHECARDNEW)fade_duration=0;
                    cardContent.setVisibility(View.VISIBLE);

                    //enable dismiss button when the card is fully visible
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.card_close_button).setClickable(true);
                        }
                    }, OFFSET);

                    if(ISTHECARDNEW)
                        playAnim(context, OFFSET, 7777, fade_duration, R.anim.fade_in, 0);
                }

                TextView tipsTitle = (TextView) findViewById(R.id.card_title);
                TextView tipsMessage = (TextView) findViewById(R.id.card_message);
                ImageView cardFace = (ImageView)findViewById(R.id.card_face);

                tipsTitle.setText(main_Title);
                tipsMessage.setText(main_Message);

                /** Set color of the title
                 *  Tip = #0099CC
                 * 	Warning = #cc7800
                 * 	Alert = #ff4444
                 *
                 * 	0xFF<color code>: FF is for white. Dunno what 0 is for.
                 **/
                if (main_button.equals("warning"))
                    tipsTitle.setTextColor(0xFFcc7800);

                else if (main_button.equals("alert"))
                    tipsTitle.setTextColor(0xFFff4444);

                /** Card face **/
                if(main_card_ID.equals("warning50Limit")){
                    cardFace.setImageDrawable(getResources().getDrawable(R.drawable.on_the_rise_face));
                }
                else if(main_card_ID.equals("warning90Limit")){
                    cardFace.setImageDrawable(getResources().getDrawable(R.drawable.watch_out));
                }
                else if(main_card_ID.equals("alert100Limit")){
                    cardFace.setImageDrawable(getResources().getDrawable(R.drawable.you_are_gone_face));
                }


            } else if (main_show_card != null && !main_show_card && is_a_card_being_displayed) {
                playAnim(MainActivity.this, 200, 7777, 200, R.anim.fade_out, 0);
                playAnim(MainActivity.this, 300, 9999, 200, R.anim.close_card, 1);

                bh.setState(main_card_ID, "OFF");

                is_a_card_being_displayed = false;

                if (!roll_back) {
                    //Log.i("MainActivity", "Setting card as HIDDEN");
                    bh.setVisibility(main_card_ID, "HIDDEN");
                }

            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Log.i("MA", "Everything done. Now enabling buttons");
                    //ENABLING +1 BUTTONS
                    subsContainer = (LinearLayout)findViewById(R.id.main_subjects_container);
                    int VIEW_COUNT = subsContainer.getChildCount();

                    while(VIEW_COUNT > 0){
                        //Log.i("MA", "Searching for: " + (VIEW_COUNT+200-1));
                        if(findViewById(VIEW_COUNT+200-1)!=null){
                            (findViewById(VIEW_COUNT+200-1)).setEnabled(true);
                        }
                        --VIEW_COUNT;
                    }
                }
            }, OFFSET-400);

        }

    }
/*
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(getIntent().getBooleanExtra("first_launch", false)){
            if(countSubjects()>0){
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.app_exit);
            }else{
                overridePendingTransition(R.anim.fade_in, R.anim.prev_screen_exit);
            }
        }
        else{
            if(!switchingToWelcomeScreen)
                overridePendingTransition(R.anim.fade_in, R.anim.app_exit);
        }
    }
*/
}