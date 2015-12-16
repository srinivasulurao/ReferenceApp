package com.example.fivestarchickenapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.fivestarchickenapp.adapter.AdapterExamCategory;

public class ActivityExamCategory extends Activity {
	
	ListView lvExamCategory;
	AdapterExamCategory adapterExamCategory;
	List<String> examCategoryList = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_exam_category);
	    
	    lvExamCategory=(ListView)findViewById(R.id.lvexamcategory);
	    
	    examCategoryList.add("Republic Day");
	    examCategoryList.add("First World War");
	    examCategoryList.add("Indian Geography");
	    examCategoryList.add("Indian cricket");
	    examCategoryList.add("Mahabharata | Hindu literature");
	   
	    
	    adapterExamCategory = new AdapterExamCategory(ActivityExamCategory.this,
				R.layout.adapter_exam_category, examCategoryList);

	    lvExamCategory.setAdapter(adapterExamCategory);
	    
	    lvExamCategory.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				
				Intent i = new Intent(ActivityExamCategory.this,
						ActivityExamDeatail.class);
				

				startActivity(i);
			}
				
			
			

	    
	    });
	}
	

}
