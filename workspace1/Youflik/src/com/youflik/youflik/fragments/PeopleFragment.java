package com.youflik.youflik.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youflik.youflik.R;


public class PeopleFragment extends Fragment {

	public PeopleFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//   View rootView = inflater.inflate(R.layout.fragment_people, container, false);
		View rootView = inflater.inflate(R.layout.fragment_people, container, false);
		return rootView;
	}
}
