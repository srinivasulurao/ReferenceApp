package com.texastech.activity;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.texastech.app.BaseActivity;
import com.texastech.app.MyApplication;
import com.texastech.app.R;
import com.texastech.bean.GetVirtualTours.VirtualTour;
import com.texastech.helper.AppUtil;

public class VirtualTourGalleryActivity extends BaseActivity{

	@InjectView(R.id.pager)
	ViewPager pager;
	
	String imgTag="";
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_virtual_tours);
	}

	@Override
	protected void initXmlView() {
		VirtualTour tour = (VirtualTour)getIntent().getSerializableExtra("TOUR");
		pager.setAdapter(new ImagePagerAdapter(tour.album_images));
	}
	
	
	
	
	
	private class ImagePagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;
		private List<String> list;

		
		
		ImagePagerAdapter(List<String> list) {
			inflater = getLayoutInflater();
			this.list = list;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View view, int position) {
			View imageLayout = inflater.inflate( R.layout.item_pager_image, null);
			
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			
			ImageView imageView = (ImageView) imageLayout .findViewById(R.id.image);
			MyApplication.getLoader().displayImage(IMAGE_URL.concat(list.get(position)), imageView, AppUtil.getImageSetting(), new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					spinner.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					spinner.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					spinner.setVisibility(View.GONE);
					/*Animation anim = AnimationUtils.loadAnimation(HowItWorks.this, R.anim.fade_in);
					imageView.setAnimation(anim);
					anim.start();*/
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					
				}
			});
			
			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
			
		}
	}
}
