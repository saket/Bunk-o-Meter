package com.saketme.bunkometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DeleteAllSubjectsDialog extends DialogFragment {

    private static Context context;
    private Typeface robotoCondensed = null;
    private static final DeleteAllSubjectsDialog ourInstance = new DeleteAllSubjectsDialog();

    public static DeleteAllSubjectsDialog getInstance(Context context) {
        DeleteAllSubjectsDialog.context = context;
        return ourInstance;
    }

    public Dialog onCreateDialog(Bundle savedInstance){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_delete_all_subjects, null);
        builder.setView(view);

        //Set Roboto-condensed font for Android 4.0 users
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){

            TextView title = (TextView)view.findViewById(R.id.delete_all_subs_title);
            if(robotoCondensed!=null) robotoCondensed = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Condensed.ttf");
            title.setTypeface(robotoCondensed);
        }

        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.deleteAllSubjectsFromDB();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        return dialog;


    }
}
