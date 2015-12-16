package com.youflik.youflik.chat;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.youflik.youflik.R;
import com.youflik.youflik.chatadapters.ChatOthersMessageListAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class ChatFragmentOthers extends Fragment{
	
	private ListView mChatOtherMessagesList;
	private TextView mChatOtherNoMessages;
	private ProgressDialog mPDialog;
	private static final String  CHAT_OTHER_MESSAGES_API= Util.API+"other_messages";
	private ArrayList<ChatOthersMessagesModel> mChatOthersArrayList ;
	private ChatOthersMessageListAdapter mChatOthersAdapter;
	private AlertDialogManager  mAlert = new AlertDialogManager();
	private int mDeletePosition;
	private AlertDialog.Builder mBuilder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_chat_others, container, false);
        mChatOtherMessagesList = (ListView) rootView.findViewById(R.id.fragment_chat_others_list);
        mChatOtherNoMessages = (TextView)rootView.findViewById(R.id.fragment_chat_others_no_messages);
        ConnectionDetector conn = new ConnectionDetector(getActivity());
        if(conn.isConnectingToInternet()){
        new GetChatOtherUsers().execute();
        } else {
        	mAlert.showAlertDialog(getActivity(),"Connection Error","Check your internet connection",false);
        }
        return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mChatOtherMessagesList.setOnItemClickListener(new OnItemClickListener(){
           
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				ChatOthersMessagesModel chatData = (ChatOthersMessagesModel) mChatOthersArrayList.get(position);
				ConnectionDetector conn = new ConnectionDetector(getActivity());
				if(conn .isConnectingToInternet()){
				Intent chatIntent = new Intent(getActivity(),ChatOthersConversationActivity.class);
			    chatIntent.putExtra("username",chatData.getMessageusers());
			    chatIntent.putExtra("firstname",chatData.getFirstname());
			    chatIntent.putExtra("lastname",chatData.getLastname());
			    chatIntent.putExtra("profile_image_path",chatData.getProfile_photo());
			    chatIntent.putExtra("jid",chatData.getJid());
			  
			    System.out.println("UserName while starting the Chat others Intent:"+chatData.getMessageusers());
			    startActivity(chatIntent); 
			    } else {
			        mAlert.showAlertDialog(getActivity(),"Connection Error","Check your internet connection",false);    	
			    }
			}	
		});
		
		mChatOtherMessagesList.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mDeletePosition = position;
				final ChatOthersMessagesModel othersModel = (mChatOthersArrayList).get(position);
				mBuilder = new AlertDialog.Builder(getActivity());
				mBuilder.setTitle("Delete Conversation");
				mBuilder.setMessage("Are you sure to remove the conversation from list?");
				mBuilder.setPositiveButton("YES",new OnClickListener(){
					@Override
				  	public void onClick(DialogInterface dialog, int which) {
						
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
						new DeleteOthersConversation().execute(othersModel.getMessageusers());
						} else {
							mAlert.showAlertDialog(getActivity(),"Connection Error","Check Your Internet Connection",false);
						}
					    dialog.dismiss();
					}
				});
				mBuilder.setNegativeButton("NO", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog dialog = mBuilder.create();
				dialog.show();
				
				return true;
			}
    		
		});
	}
	class GetChatOtherUsers extends AsyncTask<Void,Void,ArrayList<ChatOthersMessagesModel>>{
		
		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(getActivity());
				mPDialog.show();
			} else mPDialog.show();
		}
		@Override
		protected ArrayList<ChatOthersMessagesModel> doInBackground(Void... params) {
			JSONObject receivedJSONObject = HttpGetClient.sendHttpPost(CHAT_OTHER_MESSAGES_API);
			if(receivedJSONObject!=null){
				try {
				JSONArray otherMessageArray =  receivedJSONObject.getJSONArray("last_conversations");
				mChatOthersArrayList = new ArrayList<ChatOthersMessagesModel>();
				for(int i=0;i<otherMessageArray.length();i++){
					JSONObject otherMessageObject = otherMessageArray.getJSONObject(i);
					
					ChatOthersMessagesModel chatModel =  new ChatOthersMessagesModel();
					chatModel.setMessageusers(otherMessageObject.getString("messageusers"));
					chatModel.setConvid(otherMessageObject.getString("convid"));
					chatModel.setTime(otherMessageObject.getString("time"));
					chatModel.setBody(otherMessageObject.getString("body"));
					chatModel.setProfile_photo(otherMessageObject.getString("profile_photo"));
					chatModel.setFirstname(otherMessageObject.getString("firstname"));
					chatModel.setLastname(otherMessageObject.getString("lastname"));
					chatModel.setLocation_name(otherMessageObject.getString("location_name"));
					chatModel.setBio(otherMessageObject.getString("bio"));
					chatModel.setJid(otherMessageObject.getString("jid"));
					chatModel.setCreated_date(otherMessageObject.getString("created_date"));
					mChatOthersArrayList.add(chatModel);
				}
				} catch (JSONException e) { e.printStackTrace(); }
			}
			return mChatOthersArrayList;
		   }
				
		protected void onPostExecute(ArrayList<ChatOthersMessagesModel> result){
			mPDialog.dismiss();
			if(result == null){
				mChatOtherNoMessages.setVisibility(View.VISIBLE);
				mChatOtherMessagesList.setVisibility(View.GONE);
				
			} else if (result.size()== 0){
				mChatOtherNoMessages.setVisibility(View.VISIBLE);
				mChatOtherMessagesList.setVisibility(View.GONE);
			} else {
				mChatOtherNoMessages.setVisibility(View.GONE);
				mChatOtherMessagesList.setVisibility(View.VISIBLE);
				mChatOthersAdapter = new ChatOthersMessageListAdapter(getActivity(),result);
				mChatOtherMessagesList.setAdapter(mChatOthersAdapter);
			}  
		}
	}
	
	class DeleteOthersConversation extends AsyncTask<String,Void,String>{
		String status;
		
       @Override
       protected void onPreExecute(){
    	  if(mPDialog == null){
    		  ProgressDialog mPDialog = Util.createProgressDialog(getActivity());
    		  mPDialog.show();
    	  } else mPDialog.show();
       }
		
		@Override
		protected String doInBackground(String... params) {
			String username =params[0];
			JSONObject deleteObject = new JSONObject();
			JSONObject receivedJSONResponse = null;
		
			System.out.println("API to delete the conversation:"+Util.API+"delete_other_messages/"+username);
			receivedJSONResponse = HttpPostClient.sendHttpPost(Util.API+"delete_other_messages/"+username,deleteObject);
			if(receivedJSONResponse!=null){
				try {
					status = receivedJSONResponse.getString("status");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return status;
		}
		@Override
		protected void onPostExecute(String result){
			mPDialog.dismiss();
			if(result==null){
			}
			if(result.equalsIgnoreCase("1")){
				mChatOthersArrayList.remove(mDeletePosition);
				mChatOthersAdapter.notifyDataSetChanged();
			}
	}
}
}