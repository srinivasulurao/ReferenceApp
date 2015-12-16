package com.fivestarchicken.lms;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fivestarchicken.lms.ActivityModuleDetails.AdapterFragmentExam;
import com.fivestarchicken.lms.ActivityModuleDetails.FragmentExam;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.libs.CustomViewPager;
import com.fivestarchicken.lms.model.Blog;
import com.fivestarchicken.lms.model.BlogDetail;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.ModuleDescription;
import com.fivestarchicken.lms.utils.Commons;
import com.google.gson.Gson;

public class ActivityBlogDetail extends ActionBarActivity {
	private ActionBar actionbar;
	
	static public HashMap<Integer, ArrayList<BlogDetail>> mapPaging= new HashMap<Integer, ArrayList<BlogDetail>>();
	
	ArrayList<BlogDetail> descriptionList=new ArrayList<BlogDetail>();
	ArrayList<BlogDetail> pagingDescriptionList=new ArrayList<BlogDetail>();
	private DbAdapter dh;
	TextView tvdescription;
	int totalLine,availableLine,charPerLine,pagecount;
	Gson gson;
	ImageView ivBack, ivNext;
	Blog blog;
	String blogStr;
	BlogDetail blogDescription;
	CustomViewPager viewPager;
	
	
	private final String TAG = "MainScreen";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_detail);
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);
		
		tvdescription=(TextView)findViewById(R.id.tvDescription);
		tvdescription.setTypeface(Typeface.MONOSPACE);
		this.dh = new DbAdapter(ActivityBlogDetail.this);
		gson = new Gson();
		Bundle b = getIntent().getExtras();
		blogStr = b.getString("blogStr");
		blog = gson.fromJson(blogStr, Blog.class);

		viewPager = (CustomViewPager) findViewById(R.id.pager);
		//viewPager.setAdapter(new AdapterFragmentExam());
		ivNext = (ImageView) findViewById(R.id.ivRightscrool);
		
		ivBack = (ImageView) findViewById(R.id.ivleftscrool);
		
		ivNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				int index = viewPager.getCurrentItem();

				index++;
				if (index == 1) {
					

				}

				else if (index == mapPaging.size() - 1) {
					

				} else if (index == mapPaging.size()) {

					
				}

				viewPager.setCurrentItem(index, true);

			}
		});

		
		ivBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				int index = viewPager.getCurrentItem();

				index--;

				if (index < 1) {
					

				}

				if (index < mapPaging.size()) {
					

				}
				viewPager.setCurrentItem(index, true);

			}
		});
		
		
		
	}
	
	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  // TODO Auto-generated method stub
	  super.onWindowFocusChanged(hasFocus);
	  if(hasFocus){
	  descriptionPagination();
	  }
	  //Here you can get the size!
	 }
	
	
	void descriptionPagination() {
		Boolean isMoreData = false;
		pagecount = 1;
		descriptionList.clear();
		pagingDescriptionList.clear();
		mapPaging.clear();
		descriptionList = dh.getBlogDetailList(blog.getBlogId(),blog.getLanguageType());

		totalLine = tvdescription.getHeight() / tvdescription.getLineHeight();
		availableLine = totalLine;
		charPerLine = getCharPerLine(tvdescription, Commons.Example_Text);
		for (BlogDetail bd : descriptionList) {
			if (bd.getType().equals(Commons.TYPE_TEXT)) {

				String descText = bd.getDetail();
				
			

				do {
					
					int strline = descText.length()/charPerLine;
					if (availableLine > strline) {

						availableLine = availableLine - strline;
						blogDescription = new BlogDetail(
								bd.getType(), descText);
						pagingDescriptionList.add(blogDescription);
						mapPaging.put(pagecount,pagingDescriptionList);
						 pagingDescriptionList=new ArrayList<BlogDetail>();
						 availableLine = totalLine;
						pagecount++;
						isMoreData = false;

					} else if (availableLine == strline) {
						blogDescription = new BlogDetail(
								bd.getType(), descText);
						pagingDescriptionList.add(blogDescription);
						mapPaging.put(pagecount,pagingDescriptionList);
						 pagingDescriptionList=new ArrayList<BlogDetail>();
						pagecount++;
						availableLine = totalLine;
						isMoreData = false;

					} else {
						int maxStringLength = availableLine * charPerLine;
						/*do {
							Character spaceChar = descText
									.charAt(maxStringLength);
							Boolean isWhiteSpace = Character
									.isWhitespace(spaceChar);

							if (isWhiteSpace) {*/

								String actualStr = descText.substring(0,
										maxStringLength);

								blogDescription = new BlogDetail(
										bd.getType(), actualStr);
								pagingDescriptionList.add(blogDescription);
								mapPaging.put(pagecount, pagingDescriptionList);
								pagecount++;
								 pagingDescriptionList=new ArrayList<BlogDetail>();
								
								descText=descText.substring(maxStringLength,
										descText.length());
								availableLine = totalLine;
								isMoreData = true;
								/*break;

							} else {

								maxStringLength--;
							}
						} while (maxStringLength < descText.length());*/

					}

				} while (isMoreData);
			}else if(bd.getType().equals(Commons.TYPE_VIDEO)){
				
				blogDescription = new BlogDetail(
						bd.getType(), bd.getDetail());
				pagingDescriptionList.add(blogDescription);
				mapPaging.put(pagecount, pagingDescriptionList);
				pagingDescriptionList = new ArrayList<BlogDetail>();
				pagecount++;
				availableLine = totalLine;
				isMoreData = false;
				
				/*do {

					if (availableLine > Commons.LINE_IMAGE) {
						blogDescription = new BlogDetail(
								bd.getType(), bd.getDetail());
						pagingDescriptionList.add(blogDescription);
						availableLine=availableLine-Commons.LINE_IMAGE;
						isMoreData = false;

					} else if (availableLine == Commons.LINE_IMAGE) {

						blogDescription = new BlogDetail(
								bd.getType(), bd.getDetail());
						pagingDescriptionList.add(blogDescription);
						mapPaging.put(pagecount, pagingDescriptionList);
						pagingDescriptionList = new ArrayList<BlogDetail>();
						pagecount++;
						availableLine = totalLine;
						isMoreData = false;

					}

					else {

						mapPaging.put(pagecount, pagingDescriptionList);
						pagecount++;
						pagingDescriptionList = new ArrayList<BlogDetail>();
						availableLine = totalLine;
						isMoreData = true;

					}

				} while (isMoreData);*/
			}else if(bd.getType().equals(Commons.TYPE_IMAGE)){
				
				blogDescription = new BlogDetail(
						bd.getType(), bd.getDetail());
				pagingDescriptionList.add(blogDescription);
				mapPaging.put(pagecount, pagingDescriptionList);
				pagingDescriptionList = new ArrayList<BlogDetail>();
				pagecount++;
				availableLine = totalLine;
				isMoreData = false;
				
				/*do {

					if (availableLine > Commons.LINE_IMAGE) {
						blogDescription = new BlogDetail(
								bd.getType(), bd.getDetail());
						pagingDescriptionList.add(blogDescription);
						availableLine=availableLine-Commons.LINE_IMAGE;
						isMoreData = false;

					} else if (availableLine == Commons.LINE_IMAGE) {

						blogDescription = new BlogDetail(
								bd.getType(), bd.getDetail());
						pagingDescriptionList.add(blogDescription);
						mapPaging.put(pagecount, pagingDescriptionList);
						pagingDescriptionList = new ArrayList<BlogDetail>();
						pagecount++;
						availableLine = totalLine;
						isMoreData = false;

					}

					else {

						mapPaging.put(pagecount, pagingDescriptionList);
						pagecount++;
						pagingDescriptionList = new ArrayList<BlogDetail>();
						availableLine = totalLine;
						isMoreData = true;

					}

				} while (isMoreData);*/
				
				
			}else if(bd.getType().equals(Commons.TYPE_PDF)){
				
				blogDescription = new BlogDetail(
						bd.getType(), bd.getDetail());
				pagingDescriptionList.add(blogDescription);
				mapPaging.put(pagecount, pagingDescriptionList);
				pagingDescriptionList = new ArrayList<BlogDetail>();
				pagecount++;
				availableLine = totalLine;
				isMoreData = false;
			}

		}
		
		if(pagingDescriptionList.size()>0){
			
			mapPaging.put(pagecount, pagingDescriptionList);	
			
		}

		viewPager.setAdapter(new AdapterFragmentExam());
	}
		
		
	public class AdapterFragmentExam extends FragmentPagerAdapter {
		final int PAGE_COUNT = mapPaging.size();

		public AdapterFragmentExam() {
			super(getSupportFragmentManager());
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		@Override
		public Fragment getItem(int position) {

			return FragmentExam.create(position, ActivityBlogDetail.this);
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static class FragmentExam extends Fragment {
		public static final String ARG_PAGE = "ARG_PAGE",
				QUESTION = "QUESTION";

		private int mPage;
		 Context activityContext;
		ArrayList<BlogDetail> pageFieldsList=new ArrayList<BlogDetail>();
		
		int pos;

		public static FragmentExam create(int page,Context context) {

			Bundle args = new Bundle();
			args.putInt(ARG_PAGE, page);
			
			FragmentExam fragment = new FragmentExam();
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mPage = getArguments().getInt(ARG_PAGE);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			return inflater.inflate(R.layout.fragment_module_detail, container, false);
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			
			LinearLayout llBody=(LinearLayout)view.findViewById(R.id.llbody);
			 ;
			pageFieldsList=mapPaging.get(mPage+1);
			int index=0;
			for(BlogDetail mod:pageFieldsList){
				index++;
				if(mod.getType().equals(Commons.TYPE_TEXT)){
					
					TextView valueTV = new TextView(getActivity());
					valueTV.setText(mod.getDetail());
				
					valueTV.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					valueTV.setTextSize(20); 
					llBody.addView(valueTV);
				}else if(mod.getType().equals(Commons.TYPE_VIDEO)){
					
					ImageView iv;
					
					LayoutInflater lfPrevious = (LayoutInflater) getActivity()
							.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					View comment;
					comment = lfPrevious.inflate(
							R.layout.video_fram, null);
					
					iv = (ImageView) comment
							.findViewById(R.id.ivimage);
					Bitmap bmThumbnail;

					// MICRO_KIND: 96 x 96 thumbnail
					bmThumbnail = ThumbnailUtils.createVideoThumbnail(
							Commons.app_video_folder + "/" + mod.getDetail(), Thumbnails.MICRO_KIND);
					iv.setId(index);
					iv.setImageBitmap(bmThumbnail);
					llBody.addView(comment);
					
					iv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							BlogDetail blogDetail= pageFieldsList
									.get((Integer) v.getId()-1);	
							
							File file = new File(Commons.app_video_folder + "/" + blogDetail.getDetail());
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(file), "video/*");
							startActivity(intent);
							
						}
					});
					
					
					/*Bitmap bmThumbnail;

					// MICRO_KIND: 96 x 96 thumbnail
					bmThumbnail = ThumbnailUtils.createVideoThumbnail(
							Commons.app_video_folder + "/" + mod.getDetail(), Thumbnails.MICRO_KIND);
					
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
					layoutParams.gravity=Gravity.CENTER;
					ImageView iv = new ImageView(getActivity());
					iv.setLayoutParams(layoutParams);
					iv.setId(index);
					iv.setImageBitmap(bmThumbnail);
					llBody.addView(iv);*/
					
					
					
					
					
				}else if(mod.getType().equals(Commons.TYPE_IMAGE)){
					
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					layoutParams.gravity=Gravity.CENTER;
					ImageView iv = new ImageView(getActivity());
					iv.setLayoutParams(layoutParams);
					iv.setId(index);
					Bitmap bmp = BitmapFactory.decodeFile(Commons.app_image_folder + "/" + mod.getDetail());
				
					iv.setImageBitmap(bmp);
					llBody.addView(iv);
					
					
				}
				
                 else if(mod.getType().equals(Commons.TYPE_PDF)){
					
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1000, 1000);
					layoutParams.gravity=Gravity.CENTER;
					ImageView iv = new ImageView(getActivity());
					iv.setLayoutParams(layoutParams);
					iv.setId(index);
					
					iv.setBackgroundResource(R.drawable.pdf_icon);
					
					llBody.addView(iv);
					
                         iv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							BlogDetail blogDetail= pageFieldsList
									.get((Integer) v.getId()-1);	
							
							File file = new File(Commons.app_pdf_folder + "/" + blogDetail.getDetail());
							Uri path = Uri.fromFile(file);
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(path, "application/pdf");
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							
						}
					});
					
					
				}
				
			}
			
		}
	}
		
	
	
	int getCharPerLine(final TextView view, final String toMeasure) {
		final Paint paint = view.getPaint();
		int startPos = 0;
		final int endPos = toMeasure.length();
		startPos += paint.breakText(toMeasure.substring(startPos, endPos),
				true, view.getWidth(), (float[]) null);

		return startPos;

	}
	
	int countStringLine(final TextView view, final String toMeasure) {
		
		int lineCount = 0;

	    final Paint paint = view.getPaint(); // Get the paint used by the TextView
	    int startPos = 0;
	    int breakCount = 0;
	    final int endPos = toMeasure.length();
	    

	    // Loop through the string, moving along the number of characters that will
	    // fit on a line in the TextView. The number of iterations = the number of line breaks

	    while (startPos < endPos) {
	        startPos += paint.breakText(toMeasure.substring(startPos, endPos),
	                                   true,  view.getWidth(),(float[]) null);
	       // charPerLine= startPos.    
	        lineCount++;
	    }
	    // Line count will now equal the number of line-breaks the string will require
	    return lineCount;
	}


}