package com.texastech.helper;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
 
public class DatePickerFragment extends DialogFragment {
	
	OnDateSetListener ondateSet;

	/**
	 * Default constructor.
	 */
	public DatePickerFragment() { }

	/** Getting the reference to the OnDateSetListener instantiated in Activity class */
	public void setCallBack(OnDateSetListener ondate) {
		ondateSet = ondate;
	}

	private int year, month, day;


	/** Creating a bundle object to pass currently set time to the fragment */
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		
		/** Getting the year from bundle */
		year = args.getInt("year");
		
		/** Getting the month from bundle */
		month = args.getInt("month");
		
		/** Getting the day from bundle */
		day = args.getInt("day");
	}

	
	/**
	 * Create a new instance of DatePickerDialog and return it
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
	}
}