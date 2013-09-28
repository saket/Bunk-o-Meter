package com.saketme.bunkometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ShareBunkometerDialog extends DialogFragment {

    private static Context context;
    private static final ShareBunkometerDialog ourInstance = new ShareBunkometerDialog();

    public static ShareBunkometerDialog getInstance(Context context) {
        ShareBunkometerDialog.context = context;
        return ourInstance;
    }

    public Dialog onCreateDialog(Bundle savedInstance){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_tell_a_friend, null);
        builder.setView(view);

        final EditText shareMessage = (EditText)view.findViewById(R.id.share_bunkometer_message);

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage.getText().toString() + " \n\n" + getResources().getString(R.string.play_store_link));
                startActivity(Intent.createChooser(shareIntent, "Tell a friend via..."));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(getArguments().getBoolean("showToast", false))
                    Toast.makeText(context, ":/", Toast.LENGTH_SHORT).show();
                else{
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.youRock(false);
                }
            }
        });
/*
        Button shareButton = (Button)view.findViewById(R.id.share_bunkometer_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage.getText().toString() + " - " + getResources().getString(R.string.play_store_link));
                startActivity(Intent.createChooser(shareIntent, "Tell a friend via..."));
            }
        });
*/
        AlertDialog dialog = builder.create();
        return dialog;

    }
}
