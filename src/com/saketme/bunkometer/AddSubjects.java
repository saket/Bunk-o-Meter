package com.saketme.bunkometer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddSubjects extends Activity {

    //Entry elements
    private final List<TextView> SUBJECT_NAME = new ArrayList<TextView>();
    private final List<TextView> SUBJECT_BUNK_LIMIT = new ArrayList<TextView>();
    private int unique_id = 0;
    private Typeface robotoCondensed = null;

    private LinearLayout entryContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BEGIN_INCLUDE (inflate_set_custom_view)
        // Inflate a "Done/Cancel" custom action bar view.
        final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_done_cancel, null);
        customActionBarView.findViewById(R.id.bulk_add_subjects_save).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        Intent intent = new Intent(AddSubjects.this, MainActivity.class);
                        intent.putExtra("restore_subjects_delay_time", 200);

                        String mSubjectName;
                        String mSubjectBunkString;
                        Integer mBunkLimitCount;
                        List<String> SUB_NAMES_FOR_VALIDATION = new ArrayList<String>();
                        DatabaseHandler dh = new DatabaseHandler(AddSubjects.this);

                        //For validation
                        Boolean mIsError = false;
                        String mErrorMessage = "";

                        int emptySubjects = 0;
                        int duplicateSubject = 0;
                        int alreadyExists = 0;

                        TextView errorMessage = (TextView)findViewById(R.id.bulk_add_error_message);
                        TextView duplicateMessage = (TextView)findViewById(R.id.bulk_add_error_message_duplicate);
                        TextView alreadyExistsMessage = (TextView)findViewById(R.id.bulk_add_error_message_already_exists);

                        for(int i = 0 ; i<SUBJECT_NAME.size() ; i++){
                            mSubjectName = (SUBJECT_NAME.get(i)).getText().toString();
                            mSubjectBunkString = (SUBJECT_BUNK_LIMIT.get(i)).getText().toString();

                            if((!"".equals(mSubjectName) && "".equals(mSubjectBunkString)) || ("".equals(mSubjectName) && !"".equals(mSubjectBunkString))){
                                ((LinearLayout)(SUBJECT_NAME.get(i)).getParent()).setBackgroundColor(0xFFffcaca);
                                mErrorMessage = getResources().getString(R.string.fields_empty_error);
                                mIsError = true;
                            }
                            else if("".equals(mSubjectName) && "".equals(mSubjectBunkString)){
                                emptySubjects++;
                            }
                            else if("0".equals(mSubjectBunkString)){
                                mErrorMessage = getResources().getString(R.string.zero_limit_error);
                                mIsError = true;
                            }
                            else if(SUB_NAMES_FOR_VALIDATION.contains(mSubjectName)){
                                ((LinearLayout)(SUBJECT_NAME.get(i)).getParent()).setBackgroundColor(0xFFe5caf2);
                                duplicateSubject++;
                            }
                            else if(dh.getLimit(mSubjectName)!=0){
                                ((LinearLayout)(SUBJECT_NAME.get(i)).getParent()).setBackgroundColor(0xFFffecc0);
                                alreadyExists++;
                            }
                            else{
                                SUB_NAMES_FOR_VALIDATION.add(mSubjectName);
                                ((LinearLayout)(SUBJECT_NAME.get(i)).getParent()).setBackgroundColor(0xFFffffff);
                            }
                            //Log.i("BAS", "List: " + SUB_NAMES_FOR_VALIDATION);
                        }

                        //Log.i("BAS", "Empty subjects: " + emptySubjects + ". And list size:" + SUBJECT_NAME.size());

                        /** Restoring Gone visibility if there's no error **/
                        if(!mIsError)
                            errorMessage.setVisibility(View.GONE);

                        if(duplicateSubject == 0)
                            duplicateMessage.setVisibility(View.GONE);

                        if(alreadyExists==0)
                            alreadyExistsMessage.setVisibility(View.GONE);

                        /** ELSE, showing error messages **/
                        /** FOR Incomplete data **/
                        if(emptySubjects == SUBJECT_NAME.size()){
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.setText(getResources().getString(R.string.no_subs_error));
                        }
                        else if(mIsError){
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.setText(mErrorMessage);
                        }

                        /** FOR duplicate stuff **/
                        if(duplicateSubject!=0){
                            duplicateMessage.setVisibility(View.VISIBLE);
                            if(duplicateSubject>1)
                                duplicateMessage.setText(getResources().getString(R.string.duplicate_entries_error));
                            else
                                duplicateMessage.setText(getResources().getString(R.string.duplicate_entry_error));
                        }
                        if(alreadyExists!=0){
                            alreadyExistsMessage.setVisibility(View.VISIBLE);
                            if(alreadyExists>1)
                                alreadyExistsMessage.setText(getResources().getString(R.string.subs_exist_error));
                            else
                                alreadyExistsMessage.setText(getResources().getString(R.string.sub_exists_error));
                        }
                        if(emptySubjects != SUBJECT_NAME.size() && !mIsError && duplicateSubject==0 && alreadyExists==0){
                            //Log.e("BAS", "Empty: " + emptySubjects + ", List: " + SUBJECT_NAME.size());
                            for(int i = 0 ; i<SUBJECT_NAME.size() ; i++){
                                mSubjectName = (SUBJECT_NAME.get(i)).getText().toString();
                                if(!"".equals(mSubjectName)){
                                    mBunkLimitCount = Integer.parseInt((SUBJECT_BUNK_LIMIT.get(i)).getText().toString());
                                    //Log.d("Bulk Add Subjects", mSubjectName + ": " + mBunkLimitCount);
                                    String date = Brain.getCurrentDate();
                                    dh.addSubject(mSubjectName, 0, mBunkLimitCount, date);
                                }
                            }

                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.add_subjects_exit);
                        }
                        else{
                            emptySubjects = 0;
                        }
                    }
                });
        customActionBarView.findViewById(R.id.bulk_add_subjects_discard).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Cancel"
                        onBackPressed();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        // END_INCLUDE (inflate_set_custom_view)

        setContentView(R.layout.bulkaddsubjects);

        //Set Roboto-condensed font for Android 4.0 users
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){

            TextView title = (TextView)findViewById(R.id.add_multiple_subjects_title);
            if(robotoCondensed!=null) robotoCondensed = Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf");
            title.setTypeface(robotoCondensed);
        }

        entryContainer = (LinearLayout) findViewById(R.id.bulk_add_subjects_container);

        //Inserting three entries by default
        Button addMore = (Button)findViewById(R.id.bulk_add_another_subject);
        addEntry(addMore);

        if(getIntent().getBooleanExtra("transfer", false)){
            ((EditText)((LinearLayout)findViewById(R.id.subject_entry)).getChildAt(0)).setText(getIntent().getStringExtra("subject_name"));
            ((EditText)((LinearLayout)findViewById(R.id.subject_entry)).getChildAt(1)).setText(getIntent().getStringExtra("bunk_limit"));
        }

        subjectTourTip();

        addEntry(addMore);
        addEntry(addMore);

        Intent intent = new Intent();
        intent.setAction("com.saketme.bunkometer.START_NOTIFICATION");
        sendBroadcast(intent);

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    void subjectTourTip(){

        if(findViewById(R.id.tip_parent) == null){

            final View subjectTourTipContainer = getLayoutInflater().inflate(R.layout.add_subject_tour, null);
            entryContainer.addView(subjectTourTipContainer, 1);

            //playAnim(0, subjectTourTipContainer, 0, R.anim.fade_in_slide_up_enter, 4);
            //playAnim(300, subjectTourTipContainer, R.anim.subject_tour_flipper_enter, 4);
            subjectTourTipContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playAnim(0, subjectTourTipContainer, R.anim.subject_tour_flipper_exit, 1);
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAnim(0, subjectTourTipContainer, R.anim.subject_tour_flipper_exit, 1);
                }
            },3000);
        }
    }

    void playAnim(final int offset, final View itemView, int anim_id, final int anim_code) {

        /* Animation code
		 * 1 = delete it
        */

        if (itemView != null) {
            Animation animation = AnimationUtils.loadAnimation(AddSubjects.this, anim_id);
            if(offset!=0)
                animation.setStartOffset(offset);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {

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

                    }
                }
            });

            itemView.startAnimation(animation);
        }

    }

    public void addEntry(View view){

        View subjectLayout = getLayoutInflater().inflate(R.layout.subjectentry, null);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 8);
        entryContainer.addView(subjectLayout, entryContainer.getChildCount() - 1, lp);

        /** Adding to list **/
        //Subject Name
        EditText subName = (EditText)findViewById(R.id.subject_name_text);
        subName.setId(unique_id);
        SUBJECT_NAME.add(subName);

        //Bunk limit
        EditText subBunkLimit = (EditText)findViewById(R.id.bunk_limit_text);
        subBunkLimit.setId(100+unique_id);
        SUBJECT_BUNK_LIMIT.add(subBunkLimit);

        /** Toggling the Done button in bunk limit field **/
        EditText prevField = (EditText)findViewById(subBunkLimit.getId()-1);
        if(prevField != null){
            prevField.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            prevField.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        }
        //Delete button
        Button deleteSub = (Button) findViewById(R.id.delete_subject_entry);
        deleteSub.setId(200+unique_id);

        if(SUBJECT_NAME.size()>1)
            ((LinearLayout)entryContainer.getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);


        final ScrollView scrollView = (ScrollView)findViewById(R.id.bulk_add_subjects_scrollview);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }

        }, 400);

        unique_id++;
    }


    public void deleteEntry(View view){
        Button deleteButton = (Button) findViewById(view.getId());
        LinearLayout subjectEntryContainer = (LinearLayout)deleteButton.getParent();
        TextView subNameView = (TextView)subjectEntryContainer.getChildAt(0);
        TextView subBunkLimitView = (TextView)subjectEntryContainer.getChildAt(1);

        ((LinearLayout)subjectEntryContainer.getParent()).removeView(subjectEntryContainer);

        SUBJECT_NAME.remove(subNameView);
        SUBJECT_BUNK_LIMIT.remove(subBunkLimitView);

        /** Toggling the Done button in bunk limit field **/
        TextView prevField = (TextView)findViewById(subBunkLimitView.getId() - 1);
        if(prevField != null)
            prevField.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //De-incrementing unique ID
        --unique_id;

        //disabling 'X' button if only one field is present
        if(SUBJECT_NAME.size()==1){
            ((LinearLayout)((LinearLayout)subjectEntryContainer.getParent()).getChildAt(0)).getChildAt(2).setVisibility(View.INVISIBLE);
        }

    }

    Boolean findUnsavedChanges(String action){

        for(int i = 0 ; i<SUBJECT_NAME.size() ; i++){
            TextView mSubject = SUBJECT_NAME.get(i);
            TextView mSubjectBunk = SUBJECT_BUNK_LIMIT.get(i);

            if(mSubject.length() != 0 || mSubjectBunk.length() !=0){
                Confirm_exit confirm = Confirm_exit.newInstance();
                Bundle args = new Bundle();
                args.putString("action", action);
                confirm.setArguments(args);
                confirm.show(getFragmentManager(), "Confirm Delete Dialog");

                return true;
            }
        }
        return false;

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

    @Override
    public void onBackPressed() {
        Boolean unSavedData = findUnsavedChanges("discard");
        if(!unSavedData){
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.add_subjects_cancel);
        }
    }

}