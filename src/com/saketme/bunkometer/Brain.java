package com.saketme.bunkometer;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class Brain{
	
	public String Title = null;
	public String Message = null;
	private String Face = null;
	public String Card_ID = null;
	public String Dismiss_button = null;
	public Boolean show_card = false;
	
	private int optimal_no_of_subjects = 0;
	
	private Context context = null;
	
	private List<String> found_subjects_list = null;
	
	private BrainHandler bh = null;
	
	public Brain(Context context) throws ParseException {

		int priority_no;
		this.context = context;
		
		bh = new BrainHandler(context);
		DatabaseHandler dh = new DatabaseHandler(context);
		
		optimal_no_of_subjects = (dh.countSubjects()/2) + 1;
		
		search:
			for(priority_no = 1 ; priority_no <= 8 ; priority_no++){
				
				//This will initialize the card with new message in every iteration. 
				//BUT the final initilization would be our desired card since that's when the
				//loop will stop.
				if(decide(priority_no)){
					initializeCard(priority_no);

					if(bh.getVisibility(Card_ID)){
						//Log.w("Brain", "Condition " + priority_no + " satisfied. Moving over.");
						//Log.w("Brain", "Current CARD ID: " + Card_ID);
						//Log.i("Brain", "Visibility: " + bh.getVisibility(Card_ID));
						break;
					}
				}
			}

		if(priority_no == 9){
			Card_ID = null;
			//Log.e("Brain", "No condition satisfied.");
		}

        //dLog.i("Brain", "Final card ID: " + Card_ID);
	}
	
	private boolean decide(int p) throws ParseException{
		
		int condition_true_or_not = 0;
		
		switch(p){
			case 1: 	
				if(messageSemOver())
					condition_true_or_not = 1;
				break;
				
			case 2: 	
				if(alert100Limit())
					condition_true_or_not = 1;
				break;
				
			case 3: 	
				if(warning90Limit())
					condition_true_or_not = 1;
				break;
				
			case 4: 	
				if(warning50Limit())
					condition_true_or_not = 1;
				break;
				
			case 5: 	
				if(warning2Bunks())
					condition_true_or_not = 1;
				break;

			case 6: 	
				if(tipCreativeIdeas())
					condition_true_or_not = 1;
				break;
				
			case 7: 	
				if(tipNoBunkForAWeek(context))
					condition_true_or_not = 1;
				//Log.i("BrainHandler", "tipNoBunkForAWeek Decided");
				break;
				
		}

        return condition_true_or_not == 1;
		
	}
	
	private void initializeCard(int priority_no){
		switch(priority_no){
		
		case 2:		Title = "Backlog.";
					String sub_list = "";
					for(int i=0 ; i<found_subjects_list.size() ; i++){
						sub_list += found_subjects_list.get(i);
						if(i>=0){
							if(i == (found_subjects_list.size()-2))
								sub_list += " and ";
							else if(i != (found_subjects_list.size()-1))
								sub_list += ", ";
						}
						
					}
					Message = "One more bunk in " + sub_list + " and you're going to sit for an extra semester.";
					Dismiss_button = "alert";
					Face = "Happy";
					Card_ID = "alert100Limit";
					show_card = true;
					break;		
		
		case 3:		Title = "Stop playing Truant";
					Message = "Confused? Well that's British for 'no more bunking'. 90% limit reached in a few subjects.";
					Dismiss_button = "warning";
					Face = "Happy";
					Card_ID = "warning90Limit";
					show_card = true;
					break;		
		
		case 4:		Title = "Bunking on the Rise?";
					Message = "You've bunked more than 50% of your limit in a few subjects. About time to ask your friends for proxy?";
					Dismiss_button = "warning";
					Face = "Happy";
					Card_ID = "warning50Limit";
					show_card = true;
					break;		
		
		case 7:		Title = "Relax, Take Some Rest";
					Message = "You haven't bunked in a while. Go and sleep like a baby. You're being too regular.";
					Dismiss_button = "tip";
					Face = "Happy";
					Card_ID = "tipNoBunkForAWeek";
					show_card = true;
					break;
				
		default:	Title = "Aw, Snap";
					Message = "Something went wrong. Please report the dev by going into the About page.";
					Dismiss_button = "tip";
					Face = "Happy";
					Card_ID = "error";
					show_card = false;
					break;
		}
		
	}
	
	private Boolean messageSemOver(){
		//PRIORITY = 1;
		return false;
	}
	
	private Boolean alert100Limit(){
		//PRIORITY = 2;
		//Log.w("Brain", "Checking for: alert100Limit");
		DatabaseHandler dh = new DatabaseHandler(context);
		found_subjects_list = dh.checkBunkPercentageOfAllSubs(100);
		
		if((found_subjects_list != null)&&(found_subjects_list.size()>=1)){
			
			//Log.e("Brain", "90 Size: " + dh.checkBunkPercentageOfAllSubs(90).size() + ". 50 Size: " + dh.checkBunkPercentageOfAllSubs(50).size());
			
			if(dh.checkBunkPercentageOfAllSubs(90).equals(found_subjects_list) && dh.checkBunkPercentageOfAllSubs(90).size() > 1 && dh.checkBunkPercentageOfAllSubs(90).size() >= optimal_no_of_subjects){
				//Log.w("Brain", "Setting warning90Limit to Hidden. Size is: " + dh.checkBunkPercentageOfAllSubs(90).size());
				bh.setVisibility("warning90Limit", "HIDDEN");
			}
			if(dh.checkBunkPercentageOfAllSubs(50).equals(found_subjects_list) && dh.checkBunkPercentageOfAllSubs(50).size() > 1 && dh.checkBunkPercentageOfAllSubs(50).size() >= optimal_no_of_subjects){
				//Log.w("Brain", "Setting warning50Limit to Hidden");
				bh.setVisibility("warning50Limit", "HIDDEN");
			}
			return true;
		}
		else
			return false;
	}	
	
	private Boolean warning90Limit(){
		//PRIORITY = 3;
		//Log.w("Brain", "Checking for: warning90Limit");
		DatabaseHandler dh = new DatabaseHandler(context);
		found_subjects_list = dh.checkBunkPercentageOfAllSubs(90);

        //Log.i("B", "90 List: " + found_subjects_list + ". 50 List: " + dh.checkBunkPercentageOfAllSubs(50));
		
		if((found_subjects_list != null)&&(found_subjects_list.size()>=2)){
			 if(dh.checkBunkPercentageOfAllSubs(50).equals(found_subjects_list) && dh.checkBunkPercentageOfAllSubs(50).size() >= optimal_no_of_subjects){
                    //Log.w("Brain", "Setting warning50Limit to Hidden");
				 	bh.setVisibility("warning50Limit", "HIDDEN");
				}
			return true;
		}
		else
			return false;		
	}
	
	private Boolean warning50Limit(){
		//PRIORITY = 4;
		//Log.w("Brain", "Checking for: warning50Limit");
		DatabaseHandler dh = new DatabaseHandler(context);
		found_subjects_list = dh.checkBunkPercentageOfAllSubs(50);

        return (found_subjects_list != null) && (found_subjects_list.size() >= 2);
	}
	
	private Boolean warning2Bunks(){
		//PRIORITY = 5;
		return false;
	}
	
	private Boolean tipCreativeIdeas(){
		//PRIORITY = 6;
		return false;
		
	}	
	
	
	@SuppressLint("SimpleDateFormat")
	private Boolean tipNoBunkForAWeek(Context context) throws ParseException{
		/** PRIORITY = 7 **/
		
		//Get current date
		String current_date = Brain.getCurrentDate();
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date thisDate = outputFormat.parse(current_date);
		
		//Subtract 7 days from it
		Calendar c = Calendar.getInstance();
		c.setTime(thisDate);
		
		//Subtract date by 7 days (i.e., add (-7) days):
		c.add(Calendar.DATE, -7);
		
		//Get the date and convert it into string
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		
		String week_old_date = (day + "-" + month + "-" + year);
		
		DatabaseHandler dh = new DatabaseHandler(context);

        //Log.i("Brain", "Current date: " + thisDate + ". Week old date: " + week_old_date + ". Condition result: " + dh.countSubjectsSameDate(week_old_date));

		return dh.countSubjectsSameDate(week_old_date);
		
	}	
	
	public static String getCurrentDate(){
		//Get the current date
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		
		return (day + "-" + month + "-" + year);
	}
	
}
