package com.example.fivestarchickenapp;

import java.util.ArrayList;
import java.util.List;

import com.example.fivestarchickenapp.adapter.AdapterExamCategory;
import com.example.fivestarchickenapp.adapter.AdapterViewResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityViewResult extends Activity {
	
	ListView lvViewResult;
	AdapterViewResult adapterViewResult;
	List<String> viewResultList = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_view_result);
	    
	    lvViewResult=(ListView)findViewById(R.id.lvexamcategory);
	    
	    viewResultList.add("Republic Day");
	    viewResultList.add("First World War");
	    viewResultList.add("Indian Geography");
	    viewResultList.add("Indian cricket");
	    viewResultList.add("Mahabharata | Hindu literature");
	   
	    
	    adapterViewResult = new AdapterViewResult(ActivityViewResult.this,
				R.layout.adapter_view_result, viewResultList);

	    lvViewResult.setAdapter(adapterViewResult);
	    
	    lvViewResult.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				
				Intent i = new Intent(ActivityViewResult.this,
						ActivityViewResultDeatail.class);
				

				startActivity(i);
			}
				
			
			

	    
	    });
	}
	
	
	

}
