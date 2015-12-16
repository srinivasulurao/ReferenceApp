package com.youflik.youflik.chat;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.youflik.youflik.R;
import com.youflik.youflik.chat.EmoticonsGridAdapter.KeyClickListener;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.utils.AlertDialogManager;

public class EmoticonsPagerAdapter extends PagerAdapter {

	ArrayList<StickersModel> emoticons;
	private static final int NO_OF_EMOTICONS_PER_PAGE = 20;
	ChatOthersConversationActivity mActivity;
	KeyClickListener mListener;
	private AlertDialogManager alert = new AlertDialogManager();

	public EmoticonsPagerAdapter(ChatOthersConversationActivity activity,ArrayList<StickersModel> emoticons, KeyClickListener listener) {
		this.emoticons = emoticons;
		this.mActivity = activity;
		this.mListener = listener;
	}

	@Override
	public int getCount() {
		return (int) Math.ceil((double) emoticons.size()
				/ (double) NO_OF_EMOTICONS_PER_PAGE);
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		View layout = mActivity.getLayoutInflater().inflate(
				R.layout.emoticons_grid, null);

		int initialPosition = position * NO_OF_EMOTICONS_PER_PAGE;
		final ArrayList<StickersModel> emoticonsInAPage = new ArrayList<StickersModel>();

		for (int i = initialPosition; i < initialPosition
				+ NO_OF_EMOTICONS_PER_PAGE
				&& i < emoticons.size(); i++) {
			emoticonsInAPage.add(emoticons.get(i));
		}

		GridView grid = (GridView) layout.findViewById(R.id.emoticons_grid);
		EmoticonsGridAdapter adapter = new EmoticonsGridAdapter(
				mActivity.getApplicationContext(), emoticonsInAPage, position,
				mListener);
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String imagePath ="$$STICKER$##$";
				imagePath = imagePath.concat(emoticonsInAPage.get(position).getStickerUrl());
			    ConnectionDetector conn = new ConnectionDetector(mActivity);
			    if(conn.isConnectingToInternet()){
			    mActivity.new PostSendMessage().execute(imagePath);
			} else {
				alert.showAlertDialog(mActivity,"Connection Error","Check your internet connection", false);
			} 
			  
			}
			
		});

		((ViewPager) collection).addView(layout);

		return layout;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}