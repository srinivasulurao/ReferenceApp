package com.fivestarchicken.lms;

import java.util.ArrayList;
import java.util.List;

import com.fivestarchicken.lms.adapter.AdapterExamCategory;
import com.fivestarchicken.lms.adapter.AdapterExamModules;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.Exam;
import com.fivestarchicken.lms.model.ExamModule;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class ActivityExamModule extends ActionBarActivity {
	
	ListView lvExamCategory;
	AdapterExamModules adapterExamModules;
	List<ExamModule> examModuleList = new ArrayList<ExamModule>();
	private DbAdapter dh;
	private ActionBar actionbar;
	String starLevel=null,languageType;
	SharedPreferences sharedPreferences;
	Gson gson;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   
	    setContentView(R.layout.activity_exam_module);
		actionbar = getSupportActionBar();

		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		
		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
        actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);
		
		Bundle b=getIntent().getExtras();
		starLevel=b.getString("star_level");
		Integer star=new Integer(starLevel);
		starLevel=String.valueOf(star+1);
	    this.dh = new DbAdapter(ActivityExamModule.this); 
	    lvExamCategory=(ListView)findViewById(R.id.lvexamcategory);
	    sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityExamModule.this);
	    languageType = sharedPreferences.getString("languageType", null);
	    examModuleList=dh.getExamModule(starLevel,languageType);
	    
	   
	    gson = new Gson();
	    adapterExamModules = new AdapterExamModules(ActivityExamModule.this,
				R.layout.adapter_exam_category, examModuleList);

	    lvExamCategory.setAdapter(adapterExamModules);
	    
	    lvExamCategory.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				
				ExamModule	examModule= (ExamModule) parent
						.getItemAtPosition(position);
				
				
				String examModuleString = gson.toJson(examModule);
				
				Intent i = new Intent(ActivityExamModule.this,
						ActivityExamDeatail.class);
				 
				i.putExtra("examModule", examModuleString);
				startActivity(i);
				
			}
	    });
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
	

}
