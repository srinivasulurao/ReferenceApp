package com.example.fivestarchickenapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keyes.youtube.OpenYouTubePlayerActivity;

public class ActivityExamDeatail extends Activity { 
	
	String description;
	TextView tvdescription;
	ImageView ivvideo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_examp_detail);
	    tvdescription=(TextView)findViewById(R.id.tvdescription);
	    description="Much effort is put towards organizing events and celebrations that occur on Republic Day in India. Large military parades are held in New Delhi and the state capitals. Representatives of the Indian Army, Navy and Air Force and traditional dance troupes take part in the parades.A grand parade is held in New Delhi and the event starts with India's prime minister laying a wreath at the Amar Jawan Jyoti at India Gate, to remember soldiers who sacrificed their lives for their country. India's president takes the military salute during the parade in New Delhi while state governors take the military salutes in state capitals. A foreign head of state is the president's chief guest on Republic Day.Awards and medals of bravery are given to the people from the armed forces and also to civilians. Helicopters from the armed forces then fly past the parade area showering rose petals on the audience. School children also participate in the parade by dancing and singing patriotic songs. Armed Forces personnel also showcase motorcycle rides. The parade concludes with a fly past by the Indian Air Force, which involves fighter planes of flying past the dais, symbolically saluting the president. These leave trails of smoke in the colors of the Indian flag.There are many national and local cultural programs focusing on the history and culture of India. Children have a special place in these programs. Many children receive gifts of sweets or small toys. A prime minister's rally also takes place around this time of the year, as well as the Lok Tarang – National Folk Dance Festival, which occurs annually from January 24-29."; 
	    
	    tvdescription.setText(description);
	    ivvideo=(ImageView)findViewById(R.id.ivvideo);
	    
	    ivvideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				
				if(isNetworkAvailable(ActivityExamDeatail.this)){

			      
			       /* String url = "http://www.youtube.com/watch?v=1VwZ1tnDZbs";
			        
			        Uri uri = Uri.parse(url);
			        String videoId = uri.getQueryParameter("v");
			        Intent videoIntent = new Intent(null, Uri.parse("ytv://" + videoId), ActivityExamDeatail.this,
			                OpenYouTubePlayerActivity.class);
			        startActivity(videoIntent);*/
					
					 Intent i=new Intent();
				      i.setClass(ActivityExamDeatail.this, YoutubePlayer.class);
				      startActivity(i);
			       

			    }
			     else{
			        Toast.makeText(ActivityExamDeatail.this, "No Internet Connection ",  Toast.LENGTH_LONG).show();
			   }
				
			}
		});
	}
	
	public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
