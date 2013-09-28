package com.saketme.bunkometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

public class DeveloperContact extends DialogFragment{
    private static final DeveloperContact ourInstance = new DeveloperContact();
    private static Context context;

    public static DeveloperContact getInstance(Context context) {
        DeveloperContact.context = context;
        return ourInstance;
    }

    public Dialog onCreateDialog(Bundle savedInstance){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_developer_contact, null);
        builder.setView(view);

        builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        return dialog;

    }
}
