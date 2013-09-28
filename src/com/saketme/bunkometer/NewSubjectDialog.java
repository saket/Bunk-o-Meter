package com.saketme.bunkometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewSubjectDialog extends DialogFragment {

    private boolean received_new_or_not = true;
    private View view;
    private Typeface robotoCondensed;

    public static NewSubjectDialog newInstance(){
        NewSubjectDialog newDialog = new NewSubjectDialog();
        return newDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Getting all the attached arguments
        final String received_subject_name = getArguments().getString("subject_name");
        final String received_title = getArguments().getString("title");
        final String received_OK_text = getArguments().getString("ok_text");
        final String received_bunk_count = getArguments().getString("bunk_count");
        final String received_bunk_limit = getArguments().getString("bunk_limit");
        received_new_or_not = getArguments().getBoolean("new_or_not");

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_new_subject_dialog, null);
        builder.setView(view);

        //Find all the views
        final EditText subject_name_field = (EditText)view.findViewById(R.id.fragment_subject_name);
        final EditText bunk_count_field = (EditText)view.findViewById(R.id.fragment_classes_already_bunked_field);
        final EditText bunk_limit_field = (EditText)view.findViewById(R.id.fragment_limit_for_bunking_field);
        final TextView dialogTitle = (TextView)view.findViewById((R.id.new_subject_fragment_title));
        Button multipleSubsAdd = (Button)view.findViewById(R.id.add_multiple_subjects_button);

        //Set Roboto-condensed font for Android 4.0 users
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){

            if(robotoCondensed!=null) robotoCondensed = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Condensed.ttf");
            dialogTitle.setTypeface(robotoCondensed);
        }

        //Hide the multiple subjects button if it's a modification request
        if(!received_new_or_not){
            multipleSubsAdd.setVisibility(View.GONE);
        }else{
            multipleSubsAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText subjectName = (EditText)view.findViewById(R.id.fragment_subject_name);
                    EditText subjectBunkLimit = (EditText)view.findViewById(R.id.fragment_limit_for_bunking_field);
                    Intent intent = new Intent(getActivity(), AddSubjects.class);
                    intent.putExtra("subject_name", subjectName.getText().toString());
                    intent.putExtra("bunk_limit", subjectBunkLimit.getText().toString());
                    intent.putExtra("transfer", true);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.add_subjects_enter, R.anim.fade_out_half);

                }
            });
        }

        final DatabaseHandler dh = new DatabaseHandler(getActivity());

        //modify the title
        dialogTitle.setText(received_title);
        //builder.setTitle(received_title);

        /**	IF IT'S A MODIFICATION REQUEST **/
        if(!received_new_or_not){

            //pre-populate the fields
            subject_name_field.setText(received_subject_name);
            bunk_count_field.setText(received_bunk_count);

            if(received_bunk_limit == null){
                bunk_limit_field.setText(String.valueOf(dh.getLimit(received_subject_name)));
            }
            else
                bunk_limit_field.setText(received_bunk_limit);

        }

        /** IF IT'S A NEW SUBJECT **/
        else{
            subject_name_field.setText(received_subject_name);
            bunk_count_field.setText(received_bunk_count);
            bunk_limit_field.setText(received_bunk_limit);

        }

        Button saveButton = (Button)view.findViewById(R.id.new_subject_save_button);
        final TextView errorText = (TextView)view.findViewById(R.id.new_subject_error_text);
        saveButton.setText(received_OK_text);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity parentActivity = (MainActivity) getActivity();

                Boolean isError;
                String errorMessage = "";
                String mSubjectName = subject_name_field.getText().toString();

                /** Validation **/
                if (subject_name_field.getText().length() == 0 || bunk_count_field.getText().length() == 0 || bunk_limit_field.getText().length() == 0) {
                    errorMessage = getResources().getString(R.string.empty_fields_error);
                    isError = true;
                }
                else if (Integer.parseInt(bunk_count_field.getText().toString()) > Integer.parseInt(bunk_limit_field.getText().toString())) {
                        errorMessage = getResources().getString(R.string.bunked_more_than_limit);
                        isError = true;
                }
                else if(Integer.parseInt(bunk_limit_field.getText().toString())==0){
                    errorMessage = getResources().getString(R.string.limit_zero);
                    isError = true;
                }
                else if (dh.getLimit(mSubjectName)!=0 && received_new_or_not) {
                    //Log.i("NSD", "Checking if it already exists or not");
                    errorMessage = getResources().getString(R.string.sub_exists_error);
                    isError = true;
                }
                else {
                    isError = false;
                }

                if (!isError) {
                    errorText.setVisibility(View.GONE);

                    /* Find whether it's a new subject or not and:
                     * a) if it's NEW: Add it to the DB and Activity
                     * b) if it's OLD: Update it in the DB and remove from Activity
                     */

                    if (getArguments().getBoolean("new_or_not")) {
                        parentActivity.addSubjectView(subject_name_field.getText().toString(), Integer.parseInt(bunk_count_field.getText().toString()), Integer.parseInt(bunk_limit_field.getText().toString()), 1, true, MainActivity.countSubjects());
                    } else {
                        //'Modify Subject Name' is the original subject name before changing
                        parentActivity.updateSubjectInDB(subject_name_field.getText().toString(), Integer.parseInt(bunk_count_field.getText().toString()), Integer.parseInt(bunk_limit_field.getText().toString()), received_subject_name, getArguments().getInt("view_id_being_modified"));
                    }

                    //dismiss the dialog
                    getDialog().dismiss();
                } else {
                    if (errorText != null) {
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText(errorMessage);
                    }
                }
            }
        });

        Button cancelButton = (Button)view.findViewById(R.id.new_subject_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss the dialog
                getDialog().dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(subject_name_field, InputMethodManager.SHOW_IMPLICIT);
		        
	        /* 	For hiding soft-keyboard:
	         * 	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			 * 	imm.hideSoftInputFromWindow(view.getWindowToken(),0); 
	         */

            }
        });
        return dialog;

    }
}