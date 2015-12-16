package com.youflik.youflik.chat;

import java.util.ArrayList;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.youflik.youflik.R;
import com.youflik.youflik.chatadapters.ConversationsAdapter;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.utils.Util;

public class ChatFragmentInbox extends Fragment{

	private ListView conversationslistview;
	private ImageButton createNewChat;
	private TextView noconversations;
	private ConversationsAdapter adapter;
	private static final int result = 1111;
	private static final int CONVERSATIONS_UPDATE_RESULT = 2222;
	private static final int NEW_CHAT_RESULT = 3333;
	public Handler mHandler = new Handler();
	private ArrayList<ConversationsModel> conversationsList =  new ArrayList<ConversationsModel>();

	PacketFilter ChatfilterConversations = new MessageTypeFilter(Message.Type.chat);
	PacketListener packetConversationsListener ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_chat_inbox, container, false);
		conversationslistview = (ListView) rootView.findViewById(R.id.conversationslistview);
		createNewChat = (ImageButton) rootView.findViewById(R.id.createNewChat);
		noconversations = (TextView) rootView.findViewById(R.id.noconversations);
		//new LoadConversationsFromDB().execute();
		return rootView;
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		updateConversations(getActivity());
		Fragment fragment = this;


		conversationslistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Object object = conversationslistview.getItemAtPosition(position);
				ConversationsModel conversations = (ConversationsModel) object;
				DataBaseHandler db = new DataBaseHandler(getActivity());
				db.changeIsseen(Integer.toString(conversations.getConversation_id()), "yes", "0");
				db.close();
				Intent intent = new Intent(getActivity(), ChatMessagingActivity.class);
				intent.putExtra("Conversation_Name", conversations.getWith_user_display_name());
				intent.putExtra("Conversations_Name_Image", conversations.getWith_user_profilepicurl());
				intent.putExtra("Connversation_ID", conversations.getConversation_id());
				intent.putExtra("Connversation_JID", conversations.getWith_user_jid());
				getParentFragment().startActivityForResult(intent,CONVERSATIONS_UPDATE_RESULT);
			}
		});

		conversationslistview.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int pos, long id) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builderInner = new AlertDialog.Builder(
						getActivity());
				builderInner.setMessage("Are you sure do you want to delete conversations");
				builderInner.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {

						Object object = conversationslistview.getItemAtPosition(pos);
						ConversationsModel conversations = (ConversationsModel) object;
						DataBaseHandler db = new DataBaseHandler(getActivity());
						db.deleteConversations(Integer.toString(conversations.getConversation_id()));
						/*conversationsList.remove(pos);
						adapter.notifyDataSetChanged();*/
						updateConversations(getActivity());
						dialog.dismiss();
					}
				});
				builderInner.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builderInner.show();
				return true;
			}
		}); 

		createNewChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),AddFriendToChat.class);
				startActivity(intent);
			}
		});

		//new code 

		packetConversationsListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub

				Message message = (Message) packet;
				if (message.getBody() != null) {
					updateConversations(getActivity());
				}

			}
		};

		Util.connection.addPacketListener(packetConversationsListener, ChatfilterConversations);
		// new code end

		//You need to add the following line for this solution to work; thanks skayred
		fragment.getView().setFocusableInTouchMode(true);

		fragment.getView().setOnKeyListener( new OnKeyListener()
		{
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event )
			{
				if( keyCode == KeyEvent.KEYCODE_BACK )
				{
					Util.connection.removePacketListener(packetConversationsListener);
					return true;
				}
				return false;
			}


		} );
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == CONVERSATIONS_UPDATE_RESULT
				&& resultCode == Activity.RESULT_OK && null != data)  {

			updateConversations(getActivity());
			//new LoadConversationsFromDB().execute();
		}

		/*switch (requestCode) {

		case result:
			String JID = data.getStringExtra("Jid");
			int conId = data.getIntExtra("Conversation_ID", 0);
			Intent intent = new Intent(getActivity(), ChatMessagingActivity.class);
			intent.putExtra("Connversation_ID", conId);
			intent.putExtra("Connversation_JID", JID);
			startActivity(intent);
			//getActivity().finish();
			break;

		case CONVERSATIONS_UPDATE_RESULT:
			System.out.println("Where ru ");

			System.out.println("Where ru ");
			//new LoadConversationsFromDB().execute();
			break;

		}*/


	}

	public void updateConversations(final Context context){
		mHandler.post(new Runnable() {
			public void run() {
				DataBaseHandler db = new DataBaseHandler(context);
				try{
					adapter = new ConversationsAdapter(context, db.getConversions());
					if(adapter.getCount() > 0){
						conversationslistview.setAdapter(adapter);
						conversationslistview.setVisibility(View.VISIBLE);
						noconversations.setVisibility(View.GONE);
					}else{
						conversationslistview.setVisibility(View.GONE);
						noconversations.setVisibility(View.VISIBLE);
					}
				}catch(Exception e){
					System.out.println("rajesh Testing " +e);
				}finally{
					db.close();
				}
			}
		});

	}

	/*private class LoadConversationsFromDB extends AsyncTask<Void, Void, ArrayList<ConversationsModel>>{

		@Override
		protected ArrayList<ConversationsModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			DataBaseHandler db = new DataBaseHandler(getActivity());
			return db.getConversions();
		}

		@Override
		protected void onPostExecute(ArrayList<ConversationsModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			conversationsList = result;
			adapter = new ConversationsAdapter(getActivity(), conversationsList);
			conversationslistview.setAdapter(adapter);
		}
	}
	 */
}
