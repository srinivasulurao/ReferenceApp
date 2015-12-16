package com.personaltrainer.widgets;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Environment;

import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivitiesModel;
import com.personaltrainer.model.ActivityPoints;


@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class Utils {

	public static int MORNING = 1;
	public static int NOON = 2;
	public static int EVENING = 3;
	public static int NIGHT = 4;
	
	public static int total_count=0;
	
	public static double MORNING_POINTS=0.0;
	public static double NOON_POINTS=0.0;
	public static double EVENING_POINTS=0.0;
	public static double NIGHT_POINTS=0.0;

	public static String DBName=""; 
	public static String appName = " My Activity Tracker";
	public static String appName_ = "MyActivityTracker";
	
	public static String strMorning = "1";
	public static String strNoon = "2";
	//public static String strEvening = "3";
	public static String strNight = "3";

	public static String currentTab = "";
	public static String tab = "tab";
	public static String defaulttab = "defaulttab";
	
	public static String goodDiet = "Good Diet(2500 KCalorie)";
	public static String mediumDiet = "Medium Diet(1500 KCalorie)";
	public static String badDiet = "Bad Diet(Less that 800 Calorie)";

	public static String Eat = "Eat";
	public static String Sleep = "Sleep";
	public static String Transport= "transport";
	public static String Sport= "sport";
	public static String Read= "read";
	public static String work= "work";
	public static String shop= "shop";
	public static String entertainment= "entertainment";
	public static String housework= "housework";
	public static String cinema= "cinema";
	public static String walk= "walk";
	public static String drink= "drink";
	public static String others= "others";

	public static String TODAYS_GRAPH = "todays_graph";
	public static String PREVIOUS_GRAPH = "previous_graph";
	
	public static String currentDateOnDashboard= "";
	public static String currentIdOnDashboard= "";

	public static List<ActivityPoints> pointsList;

	static boolean val;
	public static double morningPoints=0.0;
	public static double noonPoints=0.0;
	public static double eveningPoints=0.0;
	public static double nightPoints=0.0;
	
	public static double previous_morningPoints=0.0;
	public static double previous_noonPoints=0.0;
	public static double previous_eveningPoints=0.0;
	public static double previous_nightPoints=0.0;
	
	public static double previousDay_return = -1.0;
	
	
	@SuppressLint("SimpleDateFormat")
	public static String convertTime(String time)
	{
		String sTime="";

		try{
			DateFormat f1 = new SimpleDateFormat("hh:mm");
			Date d = f1.parse(time);
			DateFormat f2 = new SimpleDateFormat("h:mma");
			sTime = f2.format(d).toLowerCase(); 

		}catch(Exception e){}

		return sTime;
	}

	public static double RoundDoubleValue(double value)
	{
		double finalValue=0.0;
		try{
			DecimalFormat df=new DecimalFormat("0.0");
			String formate = df.format(value); 
			finalValue = (Double)df.parse(formate) ;

		}catch(Exception e){}



		return finalValue;
	}

	public static String getTodaysDate(Context cnxt)
	{
		Calendar c = Calendar.getInstance();
		//c.add(Calendar.DATE, 1);
		Date todaysDate = c.getTime();
		SimpleDateFormat df =new SimpleDateFormat("dd/MMM/yyyy");
		return df.format(todaysDate);
	}

	public static String convertDate(Context con,String sDate)
	{
		String formattedDate="";
		try{
			DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			DateFormat targetFormat = new SimpleDateFormat("dd/MMM/yyyy");
			Date date = originalFormat.parse(sDate);
			formattedDate = targetFormat.format(date);  

		}catch(Exception e){}

		return formattedDate;
	}

	public static Date stringTodate(Context con, String sdate)
	{
		Date date =null;
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
			date = formatter.parse(sdate);

		}catch(Exception e){}

		return date;
	}

	public static void showAlertBox_(Context c, String title, String msg)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		
		
		alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {

				dialog.cancel();
			}
		});

		alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});
		
		alertDialog.show();
	}
	
	public static void showAlertBoxSingle(Context c, String title, String msg)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		
		
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {

				dialog.cancel();
			}
		});

		
		
		alertDialog.show();
	}

	
	public static int OverallPoints=0;
	public static int achievedPoints=0;
	
	public static int morning_overall_points=0;
	public static int morning_achieved_points=0;
	
	public static int noon_overall_points=0;
	public static int noon_achieved_points=0;
	
	public static int evening_overall_points=0;
	public static int evening_achieved_points=0;
	
	public static int night_overall_points=0;
	public static int night_achieved_points=0;
	
	public static int todo_overall_points=0;
	public static int todo_achieved_points=0;
	public static int todo_add_points=0;
	
	
	
	public static int convertDoubletoInt(double d)
	{
		Double db = new Double(d);
		return db.intValue();
	}
	
	public static void initilizePoints()
	{
		Utils.total_count = 0;
		//Utils.currentDateOnDashboard="";

		Utils.OverallPoints = 0;
		Utils.achievedPoints=0;

		Utils.morning_achieved_points=0;
		Utils.morning_overall_points=0;

		Utils.noon_achieved_points=0;
		Utils.noon_overall_points=0;

		Utils.evening_overall_points=0;
		Utils.evening_achieved_points=0;

		Utils.night_overall_points=0;
		Utils.night_achieved_points=0;
		
		Utils.todo_achieved_points=0;
		Utils.todo_overall_points=0;
		Utils.todo_add_points=0;
	}
	
	public static void MorningPoints(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getActivityContacts();
		
		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			
			if(am.get_date() == null || am.get_date().equals("")){}
			else
			{
				currentDateOnDashboard = am.get_date();
			}
			String pts = am.get_mypoints();
			String pts_ = am.get_points();
			
			if(pts != null)
			{
				achievedPoints += Integer.parseInt(pts);
				morning_achieved_points+= Integer.parseInt(pts);
				
			}
			
			if(pts_ != null)
			{
				OverallPoints += Integer.parseInt(pts_);
				morning_overall_points+= Integer.parseInt(pts_);
			}
			
		}
	}
	
	public static void NoonPoints(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getNoonActivityContacts();
		
	
		
		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			if(am.get_date() == null || am.get_date().equals("")){}
			else
			{
				currentDateOnDashboard = am.get_date();
			}
			String pts = am.get_mypoints();
			String pts_ = am.get_points();
			
			if(pts != null)
			{
				achievedPoints += Integer.parseInt(pts);
				noon_achieved_points+= Integer.parseInt(pts);
			}
			
			if(pts_ != null)
			{
				OverallPoints += Integer.parseInt(pts_);
				noon_overall_points+= Integer.parseInt(pts_);
			}
			
		}
	}
	
	public static void EveningPoints(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getEveningActivityContacts();
		
		
		
		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			if(am.get_date() == null || am.get_date().equals("")){}
			else
			{
				currentDateOnDashboard = am.get_date();
			}
			String pts = am.get_mypoints();
			String pts_ = am.get_points();
			
			if(pts != null)
			{
				achievedPoints += Integer.parseInt(pts);
				evening_achieved_points+= Integer.parseInt(pts);
			}
			
			if(pts_ != null)
			{
				OverallPoints += Integer.parseInt(pts_);
				evening_overall_points+= Integer.parseInt(pts_);
			}
			
		}
	}
	
	public static void NightPoints(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getNightActivityContacts();
		
		
		
		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			if(am.get_date() == null || am.get_date().equals("")){}
			else
			{
				currentDateOnDashboard = am.get_date();
			}
			String pts = am.get_mypoints();
			String pts_ = am.get_points();
			
			if(pts != null)
			{
				achievedPoints += Integer.parseInt(pts);
				night_achieved_points+= Integer.parseInt(pts);
			}
			
			if(pts_ != null)
			{
				OverallPoints += Integer.parseInt(pts_);
				night_overall_points+= Integer.parseInt(pts_);
			}
			
		}
	}
	
	public static void ToDoPoints(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		Cursor c = logDB.getTodoList();
		
		if(c.moveToFirst())
		{
			do
			{
				String sPoints = c.getString(2);
				String isChecked = c.getString(3);
				String sDate = c.getString(4);
				String sEnable = c.getString(5);
				
				if(sDate == null || sDate.equals("")){}
				else
				{
					if(currentDateOnDashboard.equalsIgnoreCase(""))
					{
						currentDateOnDashboard = sDate;
					}
					else
					{
						
					}
				}
				
				
				if(isChecked.equalsIgnoreCase("true") && sEnable.equalsIgnoreCase("true"))
				{
					achievedPoints += Integer.parseInt(sPoints);
					todo_achieved_points += Integer.parseInt(sPoints);
					
					OverallPoints += Integer.parseInt(sPoints);
					todo_overall_points += Integer.parseInt(sPoints);
					
					todo_add_points += Integer.parseInt(sPoints);
				}
				
				if(isChecked.equalsIgnoreCase("false") && sEnable.equalsIgnoreCase("true"))
				{
					todo_add_points += Integer.parseInt(sPoints);
				}
				
			}while(c.moveToNext());
		}
		
	}
	
	public static double morningPercentage(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		Double dTotal=0.0;
		boolean isToday=false;
		boolean isDate=false;
		String sDate="";
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			sDate = am.get_date();
			currentDateOnDashboard = sDate;
			
			if( am.get_mypoints()!=null)
			{
				dTotal = dTotal + Double.parseDouble(am.get_mypoints());
			}
		}

		if(mList.isEmpty())
		{
			return dTotal;
		}
		else
		{
			Utils.total_count = Utils.total_count+100;
			morningPoints = (dTotal/(mList.size()*100))*100;
			return morningPoints ;
			
		}
	}

	public static double noonPercentage(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		Double dTotal=0.0;
		boolean isToday=false;
		boolean isDate=false;
		String sDate="";
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getNoonActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			sDate = am.get_date();
			Utils.currentDateOnDashboard = sDate;

			if( am.get_mypoints()!=null)
			{
				dTotal = dTotal + Double.parseDouble(am.get_mypoints());
			}
			}

		if(mList.isEmpty())
		{
			return dTotal;
		}
		else
		{
			Utils.total_count = Utils.total_count+100;
			noonPoints = (dTotal/(mList.size()*100))*100;
			return noonPoints;
		}
	}

	public static double eveningPercentage(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		Double dTotal=0.0;
		boolean isToday=false;
		boolean isDate=false;
		String sDate="";
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getEveningActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			
			sDate = am.get_date();
			Utils.currentDateOnDashboard = sDate;

			if( am.get_mypoints()!=null)
			{
				dTotal = dTotal + Double.parseDouble(am.get_mypoints());
			}
		}

		if(mList.isEmpty())
		{
			return dTotal;
		}
		else
		{
			Utils.total_count = Utils.total_count+100;
			eveningPoints = (dTotal/(mList.size()*100))*100;
			return eveningPoints;
			
		}
	}

	public static double nightPercentage(Context con)
	{
		LoginDB logDB = new LoginDB(con);
		Double dTotal=0.0;
		boolean isToday=false;
		boolean isDate=false;
		String sDate="";
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getNightActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			
			sDate = am.get_date();
			Utils.currentDateOnDashboard = sDate;

			if( am.get_mypoints()!=null)
			{
				dTotal = dTotal + Double.parseDouble(am.get_mypoints());
			}
		}

		if(mList.isEmpty())
		{
			return dTotal;
		}
		else
		{
			Utils.total_count = Utils.total_count+100;
			nightPoints = (dTotal/(mList.size()*100))*100;
			return nightPoints;
			
		}
	}
}



/*************************************************/
/*public static double noonPercentage(Context con)
{
	LoginDB logDB = new LoginDB(con);
	Double dTotal=0.0;
	boolean isToday=false;
	boolean isDate=false;
	String sDate="";
	List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
	mList = logDB.getNoonActivityContacts();

	for(int i=0;i<mList.size();i++)
	{
		ActivitiesModel am = mList.get(i);
		
		sDate = am.get_date();
		String today = Utils.getTodaysDate(con);
		Date d1 = stringTodate(con, sDate);
		Date d2 = stringTodate(con, today);

		if(d1 == null)
		{
			//isDate = false;
		}
		else
		{
			currentDateOnDashboard = sDate;
			isDate = true;
			if(d1.equals(d2))
			{
				isToday = true;
				if(am.get_mypoints()!=null)
				{
					dTotal = dTotal + Double.parseDouble(am.get_mypoints());
				}
			}
			else
			{
				isToday = false;
				if(am.get_mypoints()!=null)
				{
					dTotal = dTotal + Double.parseDouble(am.get_mypoints());
				}
			}
		}

	}

	if(mList.isEmpty())
	{
		return dTotal;
	}
	else
	{
		if(isDate)
		{
			noonPoints = (dTotal/(mList.size()*100))*100;
			if(isToday)
			{
				return noonPoints ;
			}
			else
			{
				//logDB.addActivitiesPoints(Double.toString(noonPoints), sDate, Utils.NOON);
				//previous_noonPoints = noonPoints;
				return noonPoints;
			}
		}
		else
		{
			return 0.0;
		}
	}
}*/
/********************************************************************/