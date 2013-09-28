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
import android.widget.Toast;

public class Confirm_delete extends DialogFragment {

    private Typeface robotoCondensed = null;
	
	public static Confirm_delete newInstance(){
		Confirm_delete confirmDialog = new Confirm_delete();
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

        final String subject_name = getArguments().getString("subject_name");

        TextView title = (TextView)view.findViewById(R.id.delete_all_subs_title);
        TextView message = (TextView)view.findViewById(R.id.delete_all_subs_message);

        title.setText(getResources().getString(R.string.delete_text) + " " + subject_name + " " + getResources().getString(R.string.for_sure));
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        message.setVisibility(View.GONE);

		builder.setPositiveButton(getResources().getString(R.string.yep), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Toast.makeText(getActivity(), subject_name + " deleted!", Toast.LENGTH_SHORT).show();
				MainActivity mainActivity = (MainActivity)getActivity();
				mainActivity.deleteSubjectFromDB(subject_name, getArguments().getInt("view_to_delete"));

                if(getArguments().getInt("confused_count")>=3 && getArguments().getInt("confused_count")<100){
                    //Log.i("CD", "Received confused count:" + getArguments().getInt("confused_count"));
                    Toast.makeText(getActivity(), getResources().getString(R.string.finallyexcl), Toast.LENGTH_SHORT).show();
                    MainActivity.confusedCount = 100;
                }

			}
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

                //Log.i("CD", "Received confused count:" + getArguments().getInt("confused_count"));

                switch (getArguments().getInt("confused_count")){
                    case 1: Toast.makeText(getActivity(), "You confuse me...", Toast.LENGTH_SHORT).show();
                            break;

                    case 2: Toast.makeText(getActivity(), "You confuse me a LOT", Toast.LENGTH_SHORT).show();
                            break;

                    case 3: Toast.makeText(getActivity(), "You're loving this, aren't you?", Toast.LENGTH_SHORT).show();
                            break;

                    case 4: Toast.makeText(getActivity(), "Dude stop", Toast.LENGTH_SHORT).show();
                            break;

                    case 5: Toast.makeText(getActivity(), "Alright.", Toast.LENGTH_SHORT).show();
                            break;

                    case 6: Toast.makeText(getActivity(), "...", Toast.LENGTH_SHORT).show();
                        break;

                    case 7: Toast.makeText(getActivity(), "...", Toast.LENGTH_SHORT).show();
                        break;

                    case 8: Toast.makeText(getActivity(), "...", Toast.LENGTH_SHORT).show();
                        break;

                    case 9: Toast.makeText(getActivity(), "Boo!", Toast.LENGTH_SHORT).show();
                            break;

                    case 10: Toast.makeText(getActivity(), "Okay, now go and tell your friends about Bunk-o-Meter?", Toast.LENGTH_LONG).show();
                            break;

                    case 11: Toast.makeText(getActivity(), "You're crazy, human. I'm going to sleep now.", Toast.LENGTH_SHORT).show();
                             break;

                    case 100: Toast.makeText(getActivity(), "Not falling for this again.", Toast.LENGTH_SHORT).show();
                              break;

                    default: Toast.makeText(getActivity(), "zzz...", Toast.LENGTH_SHORT).show();
                            break;
                }

                MainActivity.confusedCount++;

			}
		});
		return builder.create();
	}

}
