package com.youflik.youflik.chat;

import java.util.ArrayList;

import com.youflik.youflik.R;
import com.youflik.youflik.chatadapters.ConversationsAdapter;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.models.ConversationsModel;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChatConversationsActivity extends ActionBarActivity {

	private ListView conversationslistview;
	private ConversationsAdapter adapter;
	public Menu menuInstance;
	public static final int result = 111;
	private static final int CONVERSATIONS_UPDATE_RESULT = 222;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatconversations);
		conversationslistview = (ListView) findViewById(R.id.conversationslistview);
		new LoadConversationsFromDB().execute();

		conversationslistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Object object = conversationslistview.getItemAtPosition(position);
				ConversationsModel conversations = (ConversationsModel) object;
				DataBaseHandler db = new DataBaseHandler(ChatConversationsActivity.this);
				db.changeIsseen(Integer.toString(conversations.getConversation_id()), "yes", "0");
				Intent intent = new Intent(ChatConversationsActivity.this, ChatMessagingActivity.class);
				intent.putExtra("Conversation_Name", conversations.getWith_user_display_name());
				intent.putExtra("Conversations_Name_Image", conversations.getWith_user_profilepicurl());
				intent.putExtra("Connversation_ID", conversations.getConversation_id());
				intent.putExtra("Connversation_JID", conversations.getWith_user_jid());
				startActivityForResult(intent,CONVERSATIONS_UPDATE_RESULT);
			}
		});


	}

	private class LoadConversationsFromDB extends AsyncTask<Void, Void, ArrayList<ConversationsModel>>{

		@Override
		protected ArrayList<ConversationsModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			DataBaseHandler db = new DataBaseHandler(ChatConversationsActivity.this);
			return db.getConversions();
		}

		@Override
		protected void onPostExecute(ArrayList<ConversationsModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter = new ConversationsAdapter(ChatConversationsActivity.this, result);
			conversationslistview.setAdapter(adapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		menuInstance = menu;
		getMenuInflater().inflate(R.menu.chat_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.chatconnections_add: 
			/*Intent addcon = new Intent(ChatConversationsActivity.this,Select_Connections_Messages.class);
			startActivityForResult(addcon,result);*/
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode != RESULT_OK) {

			return;
		}

		switch (requestCode) {

		case result:
			String JID = data.getStringExtra("Jid");
			int conId = data.getIntExtra("Conversation_ID", 0);
			Intent intent = new Intent(ChatConversationsActivity.this, ChatMessagingActivity.class);
			intent.putExtra("Connversation_ID", conId);
			intent.putExtra("Connversation_JID", JID);
			startActivity(intent);
			finish();
			break;

		case CONVERSATIONS_UPDATE_RESULT:
			System.out.println("Where ru ");
			new LoadConversationsFromDB().execute();
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
