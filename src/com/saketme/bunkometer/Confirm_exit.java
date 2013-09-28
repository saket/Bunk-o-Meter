package com.saketme.bunkometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class Confirm_exit extends DialogFragment {

    private Typeface robotoCondensed = null;

	public static Confirm_exit newInstance(){
		Confirm_exit confirmDialog = new Confirm_exit();
		return confirmDialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_delete_all_subjects, null);
        builder.setView(view);

        //Set Roboto-condensed font for Android 4.0 users
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){

            TextView title = (TextView)view.findViewById(R.id.delete_all_subs_title);
            if(robotoCondensed!=null) robotoCondensed = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Condensed.ttf");
            title.setTypeface(robotoCondensed);
        }

        TextView title = (TextView)view.findViewById(R.id.delete_all_subs_title);
        TextView message = (TextView)view.findViewById(R.id.delete_all_subs_message);

        title.setText("Really " + getArguments().getString("action") + "? You've unsaved subjects");
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        message.setVisibility(View.GONE);

		builder.setPositiveButton("Yep", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.add_subjects_cancel);
            }
        });
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		return builder.create();
	}



}
