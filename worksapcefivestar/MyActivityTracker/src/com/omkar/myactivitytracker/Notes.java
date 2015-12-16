package com.omkar.myactivitytracker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.omkar.myactivitytracker.Activities.ActivitiesAdapter.ViewHolder;

import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivitiesModel;

import com.personaltrainer.widgets.Utils;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Notes extends Activity {

	private Menu menu;
	ListView list_notes;
	Intent intent;
	LoginDB logDB;
	NotesModel notesList;
	List<NotesModel> nList;
	NotesAdapter nAdapter;
	boolean isMultiple=false, isFirst=false;
	SparseBooleanArray sp;
	List<Integer> listSelected;
	int archieve_count = 0;
	

	void initilizeUI()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(" Notes List");

		list_notes = (ListView)findViewById(R.id.list_notes);
		logDB = new LoginDB(this);
		nList = new ArrayList<NotesModel>();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes);

		initilizeUI();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		BackgroundTask bg = new BackgroundTask();
		bg.execute("");
		

	}

	class BackgroundTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			nList = new ArrayList<NotesModel>();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			Cursor cursor = logDB.getNotes();
			if (cursor.moveToFirst()) {
				do {
					notesList = new NotesModel();

					if(cursor.getString(3).equalsIgnoreCase("false"))
					{
						notesList.setId(cursor.getString(0));
						notesList.setDate(cursor.getString(1));
						notesList.setContent(cursor.getString(2));
						notesList.setArchieve(cursor.getString(3));
						notesList.setAudio(cursor.getString(4));
						notesList.setPicture(cursor.getString(5));

						nList.add(notesList);
					}

				}while(cursor.moveToNext());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(nList.isEmpty())
			{

				ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("TAG", "Click plus button to Add Notes.");
				oslist.add(map);

				ListAdapter adapter = new SimpleAdapter(Notes.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
						new int[] {R.id.txtNoData});

				list_notes.setAdapter(adapter);

			}
			else
			{
				Collections.reverse(nList);
				nAdapter = new NotesAdapter(Notes.this, nList);
				list_notes.setAdapter(nAdapter);

				list_notes.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						// TODO Auto-generated method stub

						if(!nList.isEmpty())
						{
							TextView txtDate = (TextView)view.findViewById(R.id.noteDate);
							TextView txtContent = (TextView)view.findViewById(R.id.txtContent);


							Utils.showAlertBoxSingle(Notes.this, "Notes", txtContent.getText().toString().trim());
						}

					}
				});

				list_notes.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						if(!nList.isEmpty())
						{
							TextView txtId = (TextView)view.findViewById(R.id.notesId);
							TextView txtAudio = (TextView)view.findViewById(R.id.notes_audio);
							TextView txtPicture= (TextView)view.findViewById(R.id.notes_picture);
							TextView txtArchieve = (TextView)view.findViewById(R.id.notes_archieve);

							int mid = Integer.parseInt(txtId.getText().toString().trim());
							ShowNotesOptions(mid,txtAudio.getText().toString().trim(),txtArchieve.getText().toString().trim(),txtPicture.getText().toString());
						}
						return false;

					}
				});

			}
		}
		
	}
	
	class BackgroundTaskArchieveList extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			nList = new ArrayList<NotesModel>();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			Cursor cursor = logDB.getNotes();
			if (cursor.moveToFirst()) {
				do {
					notesList = new NotesModel();

					if(cursor.getString(3).equalsIgnoreCase("true"))
					{
						notesList.setId(cursor.getString(0));
						notesList.setDate(cursor.getString(1));
						notesList.setContent(cursor.getString(2));
						notesList.setArchieve(cursor.getString(3));
						notesList.setAudio(cursor.getString(4));
						notesList.setPicture(cursor.getString(5));

						nList.add(notesList);
						
						
					}

				}while(cursor.moveToNext());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			archieve_count = nList.size();
			if(nList.isEmpty())
			{

				ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("TAG", "Click plus button to Add Notes.");
				oslist.add(map);

				ListAdapter adapter = new SimpleAdapter(Notes.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
						new int[] {R.id.txtNoData});

				list_notes.setAdapter(adapter);

			}
			else
			{
				nAdapter = new NotesAdapter(Notes.this, nList);
				list_notes.setAdapter(nAdapter);

				list_notes.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						// TODO Auto-generated method stub

						if(!nList.isEmpty())
						{
							TextView txtDate = (TextView)view.findViewById(R.id.noteDate);
							TextView txtContent = (TextView)view.findViewById(R.id.txtContent);


							Utils.showAlertBoxSingle(Notes.this, "Notes", txtContent.getText().toString().trim());
						}

					}
				});

				list_notes.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						if(!nList.isEmpty())
						{
							TextView txtId = (TextView)view.findViewById(R.id.notesId);
							TextView txtAudio = (TextView)view.findViewById(R.id.notes_audio);
							TextView txtArchieve = (TextView)view.findViewById(R.id.notes_archieve);
							TextView txtPicture = (TextView)view.findViewById(R.id.notes_picture);

							int mid = Integer.parseInt(txtId.getText().toString().trim());
							ShowNotesOptions(mid,txtAudio.getText().toString().trim(),txtArchieve.getText().toString().trim(),txtPicture.getText().toString());
						}
						return false;

					}
				});

			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		this.menu = menu;
		getMenuInflater().inflate(R.menu.splash, menu);

		MenuItem item = menu.findItem(R.id.action_rate);
		item.setIcon(R.drawable.archieve);
		
		
		/*TextView tv = new TextView(this);
        tv.setText(Integer.toString(archieve_count));
        tv.setTextSize(20);
        
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,      
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 25, 0);
        tv.setLayoutParams(params);
        
        tv.setBackgroundResource(R.drawable.circle);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setPadding(20, 5, 0, 0);
        tv.setTypeface(null, Typeface.BOLD);
        
        menu.add(0, 100, 1, R.string.login).setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BackgroundTaskArchieveList bg = new BackgroundTaskArchieveList();
				bg.execute("");
			}
		});*/

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) 
		{
		case R.id.action_add:
			intent = new Intent(Notes.this,AddNote.class);
			intent.putExtra("from", "add");
			startActivity(intent);
			break;
			
		case R.id.action_rate:
			BackgroundTaskArchieveList bg = new BackgroundTaskArchieveList();
			bg.execute("");
			break;
			

		

		}
		return true;
	}

	void ShowNotesOptions(final int id, final String sAudio, final String sArchieve, final String sPicture)
	{
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				Notes.this);
		builderSingle.setTitle("Choose the option.");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				Notes.this,
				android.R.layout.select_dialog_singlechoice);

		if(sArchieve.equalsIgnoreCase("false"))
		{
			arrayAdapter.add("Edit");
			arrayAdapter.add("Archive");
			arrayAdapter.add("Delete");
		}
		else
		{
			arrayAdapter.add("Edit");
			arrayAdapter.add("UnArchive");
			arrayAdapter.add("Delete");
		}

		builderSingle.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if(which == 0)
				{
					Intent in = new Intent(Notes.this,AddNote.class);
					in.putExtra("from", "edit");
					in.putExtra("id", Integer.toString(id));
					startActivity(in);
				}
				else if(which == 1)
				{
					if(sArchieve.equalsIgnoreCase("false"))
					{
						logDB.UpdateNotesArchieve("true",id);
						BackgroundTask bg = new BackgroundTask();
						bg.execute("");
					}
					else
					{
						logDB.UpdateNotesArchieve("false",id);
						BackgroundTaskArchieveList bg = new BackgroundTaskArchieveList();
						bg.execute("");
					}
				}
				else
				{
					logDB.NotesDeleteRowById(id);

					if(sAudio.equals("")){}
					else
					{
						File file = new File(Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+sAudio+".3gp");
						file.delete();
					}
					
					if(sPicture.equals(""))
					{}
					else
					{
						File file = new File(sPicture);
						file.delete();
					}
					BackgroundTask bg = new BackgroundTask();
					bg.execute("");
				}

			}
		});
		builderSingle.show();
	}

	void loadArchieveList()
	{

		Cursor cursor = logDB.getNotes();
		nList = new ArrayList<NotesModel>();

		if (cursor.moveToFirst()) {
			do {
				notesList = new NotesModel();

				if(cursor.getString(3).equalsIgnoreCase("true"))
				{
					notesList.setId(cursor.getString(0));
					notesList.setDate(cursor.getString(1));
					notesList.setContent(cursor.getString(2));
					notesList.setAudio(cursor.getString(4));
					notesList.setPicture(cursor.getString(5));

					nList.add(notesList);
				}

			}while(cursor.moveToNext());
		}

		if(nList.isEmpty())
		{

			ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("TAG", "Click plus button to Add Notes.");
			oslist.add(map);

			MenuItem menu_add = menu.findItem(R.id.action_add);
			menu_add.setIcon(R.drawable.add);

			ListAdapter adapter = new SimpleAdapter(this, oslist, R.layout.nodata, new String[] {"TAG"}, 
					new int[] {R.id.txtNoData});

			list_notes.setAdapter(adapter);

		}
		else
		{
			nAdapter = new NotesAdapter(Notes.this, nList);
			list_notes.setAdapter(nAdapter);
		}

	}

	void refreshNotesList()
	{
		Cursor cursor = logDB.getNotes();
		List<NotesModel> nList = new ArrayList<NotesModel>();
		nList.clear();
		
		if (cursor.moveToFirst()) {
			do {
				NotesModel notesList = new NotesModel();

				if(cursor.getString(3).equalsIgnoreCase("false"))
				{
					notesList.setId(cursor.getString(0));
					notesList.setDate(cursor.getString(1));
					notesList.setContent(cursor.getString(2));
					notesList.setAudio(cursor.getString(3));
					notesList.setPicture(cursor.getString(4));

					nList.add(notesList);
				}

			}while(cursor.moveToNext());
		}

		if(nList.isEmpty())
		{

			ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("TAG", "Click plus button to Add Notes.");
			oslist.add(map);

			MenuItem menu_add = menu.findItem(R.id.action_add);
			menu_add.setIcon(R.drawable.add);

			ListAdapter adapter = new SimpleAdapter(this, oslist, R.layout.nodata, new String[] {"TAG"}, 
					new int[] {R.id.txtNoData});

			list_notes.setAdapter(adapter);

		}
		else
		{
			nAdapter = new NotesAdapter(Notes.this, nList);
			list_notes.setAdapter(nAdapter);
		}
	}

	public static class NotesAdapter extends BaseAdapter
	{
		private  LayoutInflater inflater=null;
		public Context mContext;

		LoginDB logDB;
		int num;
		List<NotesModel> cn;
		MediaRecorder myAudioRecorder;
		MediaPlayer m;
		String sTag="";

		public  class ViewHolder
		{
			public TextView txtDate;
			public TextView txtContent;
			public TextView txtId;
			public TextView txtAudio;
			public TextView txtPicture;
			public TextView notes_archieve;
			public ImageView imgAudio;
			public ImageView imgPicture;
			public LinearLayout linMain;
		}

		public NotesAdapter(Context context, List<NotesModel> mList)
		{
			mContext = context;
			logDB = new LoginDB(mContext);
			cn = mList;

			inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			num = cn.size();
			if(num<=0)
				return 0;
			return num;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View vi = convertView;

			ViewHolder holder = new ViewHolder();
			holder = new ViewHolder();

			if(convertView==null)
			{
				vi = inflater.inflate(R.layout.notes_helper,null);

				holder.txtDate= (TextView)vi.findViewById(R.id.noteDate);
				holder.txtContent = (TextView)vi.findViewById(R.id.txtContent);
				holder.txtId = (TextView)vi.findViewById(R.id.notesId);
				holder.txtAudio = (TextView)vi.findViewById(R.id.notes_audio);
				holder.txtPicture = (TextView)vi.findViewById(R.id.notes_picture);
				holder.notes_archieve = (TextView)vi.findViewById(R.id.notes_archieve);
				holder.linMain = (LinearLayout)vi.findViewById(R.id.linMain);
				holder.imgAudio = (ImageView)vi.findViewById(R.id.imgAudio);
				holder.imgPicture = (ImageView)vi.findViewById(R.id.imgPicture);

				vi.setTag( holder );
			}
			else
			{
				holder=(ViewHolder)vi.getTag();
			}

			
			if(num<=0){}

			else
			{
				NotesModel am = cn.get(position);
				String sId = am.getId();
				String sDate = am.getDate();
				String sContent = am.getContent();
				final String sAudio = am.getAudio();
				final String sPicture = am.getPicture(); 
				String sArchieve = am.getArchieve();

				holder.txtId.setText(sId);
				holder.txtDate.setText(sDate);
				holder.txtContent.setText(sContent);
				holder.txtAudio.setText(sAudio);
				holder.txtPicture.setText(sPicture);
				holder.notes_archieve.setText(sArchieve);

				holder.imgAudio.setFocusable(false);
				holder.imgPicture.setFocusable(false);

				if(!sPicture.equals(""))
				{
					holder.imgPicture.setVisibility(View.VISIBLE);
					
					holder.imgPicture.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.parse("file://"+sPicture), "image/*");
							mContext.startActivity(intent);
							
							
							
							
						}
					});
				}
				else
				{
					holder.imgPicture.setVisibility(View.GONE);
				}

				if(sAudio.equals("") || sAudio.equalsIgnoreCase("false"))
				{
					holder.imgAudio.setVisibility(View.GONE);
				}
				else
				{
					holder.imgAudio.setVisibility(View.VISIBLE);
					
					holder.imgAudio.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							LinearLayout lin = (LinearLayout)v.getParent();
							final ImageView imgAudio = (ImageView)lin.getChildAt(0);
							
							if(imgAudio.getTag().toString().trim().equalsIgnoreCase("1"))
							{
								sTag="1";
								try
								{
									String outputFile = Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+sAudio+".3gp";
									m = new MediaPlayer();
									m.setDataSource(outputFile);
									m.prepare();
									m.start();
									m.setOnCompletionListener(new OnCompletionListener() {
										
										@Override
										public void onCompletion(MediaPlayer mp) {
											// TODO Auto-generated method stub
											imgAudio.setTag("1");
											imgAudio.setBackgroundResource(R.drawable.play_small);
											imgAudio.setBackgroundResource(R.drawable.play_small);
										}
									});

								}catch(Exception e)
								{
									Toast.makeText(mContext, "Unable to play the video", Toast.LENGTH_SHORT).show();
								}
							}
							
							if(imgAudio.getTag().toString().trim().equalsIgnoreCase("2"))
							{
								sTag="2";
								try
								{
									m.stop();
								}catch(Exception e)
								{
									Toast.makeText(mContext, "Unable to stop the video", Toast.LENGTH_SHORT).show();
								}
							}
							
							
							if(sTag.equalsIgnoreCase("1"))
							{
								imgAudio.setTag("2");
								imgAudio.setBackgroundResource(R.drawable.stop_small);
							}
							
							if(sTag.equalsIgnoreCase("2"))
							{
								imgAudio.setTag("1");
								imgAudio.setBackgroundResource(R.drawable.play_small);
							}
							
						}
					});
				}


				if(position % 2 == 0)
				{
					holder.linMain.setBackgroundResource(R.drawable.lefta);
				}
				else
				{
					holder.linMain.setBackgroundResource(R.drawable.lefta);
				}
			}
			return vi;
		}
		
		
		private void play(String audio_file_name) throws IllegalArgumentException,   
		SecurityException, IllegalStateException, IOException{

			String outputFile = Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+audio_file_name+".3gp";
			
			m = new MediaPlayer();
			m.setDataSource(outputFile);
			m.prepare();
			m.start();
			
			m.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					
				}
			});

		}
	
	}



}
