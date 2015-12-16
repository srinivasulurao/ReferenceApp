package com.youflik.youflik.chat;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.MessageEventManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.chatadapters.ChatTempAdapter;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.ConversationsMessagesModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.proxy.HttpPostImageForChat;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class ChatMessagingActivity extends ActionBarActivity {

	public static int ConversationsID;
	public static String Connversation_JID;
	private String with_user_name, with_user_image,withUserFirstName,withUserLastName,withUserId;
	public static ArrayList<ConversationsMessagesModel> messagesArrayList = new ArrayList<ConversationsMessagesModel>();
	public ChatTempAdapter adapter;
	public ListView chatMessagesList;
	private Button sendButton;
	private Button sendStickers;
	private EditText messageText;
	public static boolean isInFront = false;
	public Handler mHandler = new Handler();
	private  static String lastMsgTime;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String withUserImage;
	private String isUserBlocked;
	private String blockID;
	private String isFriend;
	private ImageView withUserPhoto;
	private TextView withUserName,withUserComposing;
	private Button postMedia;
	private File picfile, videoFile;
	private File mFileTemp, mVideoFileTemp;
	private Bitmap bitmap_postImage, scaledBitmap;
	private static String videoUrl,ImageUrl;
	private ThumbnailUtils video_thumbnail;
	private ProgressDialog pDialog;
	private static final int SELECT_PICTURE = 1;
	/*private static final int SELECT_VIDEO = 2;
	private static final int SELECT_CAMERA_IMAGE_REQUEST = 3;
	private static final int SELECT_CAMERA_VIDEO_REQUEST = 4;*/
	private static int postFlagFileCheck = 0;
	private Button OptionsButton;
	private GridView stickersGridview;
	private ArrayList<StickersModel> stickersList = new ArrayList<StickersModel>();

	private View popUpView;

	private LinearLayout emoticonsCover;
	private PopupWindow popupWindow;

	private int keyboardHeight;	

	private LinearLayout parentLayout;

	private LinearLayout bottomLayoutchat;

	private boolean isKeyBoardVisible;

	AlertDialogManager alert = new AlertDialogManager();

	private StickersAdapter stickersAdapter;

	PacketFilter Chatfilter = new MessageTypeFilter(Message.Type.chat);
	PacketListener packetListener ;
	public Handler incomingHandler = new Handler();
	public Handler statusHandler = new Handler();


	DocumentBuilder documentBuilder;
	InputSource inputSource;

	private Roster roster;
	Collection<RosterEntry> entries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		Bundle extras = getIntent().getExtras();
		ConversationsID = extras.getInt("Connversation_ID");
		Connversation_JID = extras.getString("Connversation_JID");
		with_user_name = extras.getString("Conversation_Name");
		with_user_image = extras.getString("Conversations_Name_Image");
		Util.with_User_Image = with_user_image;
		setContentView(R.layout.activity_chat_messages_with_stickers);
		chatMessagesList = (ListView) findViewById(R.id.chatlistview);
		messageText = (EditText) findViewById(R.id.messageText);
		sendButton = (Button) findViewById(R.id.sendMessage);
		withUserPhoto = (ImageView) findViewById(R.id.with_user_image);
		withUserName = (TextView) findViewById(R.id.withUserName);
		withUserComposing = (TextView) findViewById(R.id.withComposing);
		OptionsButton = (Button) findViewById(R.id.OptionsButton);
		bottomLayoutchat = (LinearLayout) findViewById(R.id.bottomLayoutchat);
		postMedia = (Button) findViewById(R.id.sendMedia);
		sendStickers = (Button) findViewById(R.id.sendStickers);

		parentLayout = (LinearLayout) findViewById(R.id.list_parent);
		emoticonsCover = (LinearLayout) findViewById(R.id.footer_for_emoticons);

		popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);

		stickersGridview = (GridView) popUpView.findViewById(R.id.stickersGridview);

		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		roster = Util.connection.getRoster();
		Presence presence;
		presence = roster.getPresence(Connversation_JID);
		if(presence.isAvailable()){
			withUserComposing.setText("online");
		}else if(presence.isAway()){
			withUserComposing.setText("away");
		}else{
			withUserComposing.setText("offline");
		}
		Util.IncomingChatMessage = false;
		//firstLoad();

		//new LoadConvMessagesFromDB().execute();
		setMessages();



		//new code 

		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		inputSource = new InputSource();

		packetListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub

				Message message = (Message) packet;
				if (message.getBody() != null) {
					String fromName = StringUtils.parseBareAddress(message.getFrom());
					//Log.i("XMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
					if(fromName.equalsIgnoreCase(Connversation_JID)){
						ConversationsMessagesModel messageModel = new ConversationsMessagesModel();
						messageModel.setConvmessage_type("Chat");
						messageModel.setConvmessage_direction("in");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String strDate = sdf.format(new Date());
						messageModel.setConvmessage_time(strDate);
						messageModel.setConvmessage_message(message.getBody());
						messageModel.setConversation_id(ConversationsID);
						messagesArrayList.add(messageModel);
						incomingHandler.post(new Runnable() {
							public void run() {
								setIncomingMessage();
							}
						});
					}

				}else{
					String fromName = StringUtils.parseBareAddress(message.getFrom());
					System.out.println("RAJESH +++");
					if(fromName.equalsIgnoreCase(Connversation_JID)){

						System.out.println("RAJESH ++++++++++++++++++++++++++++++++++++++++");
						System.out.println("message.setXML() " + message.toXML());

						// Checking for Compose 
						inputSource.setCharacterStream(new StringReader(message.toXML()));

						try {
							Document doc = documentBuilder.parse(inputSource);
							NodeList composingNodes = doc.getElementsByTagName("composing");
							NodeList pausedNodes = doc.getElementsByTagName("paused");
							NodeList activeNodes = doc.getElementsByTagName("active");
							NodeList inactiveNodes = doc.getElementsByTagName("inactive");
							NodeList goneNodes = doc.getElementsByTagName("gone");
							if(composingNodes.getLength() > 0 ){
								incomingHandler.post(new Runnable() {
									public void run() {
										setStatusMessage("Typing...");
									}
								});
							}
							if(pausedNodes.getLength() > 0 ){
								incomingHandler.post(new Runnable() {
									public void run() {
										setStatusMessage("active...");
									}
								});
							}
							if(activeNodes.getLength() > 0 ){
								incomingHandler.post(new Runnable() {
									public void run() {
										setStatusMessage("active...");
									}
								});
							}
							if(inactiveNodes.getLength() > 0 ){
								incomingHandler.post(new Runnable() {
									public void run() {
										setStatusMessage("inactive...");
									}
								});
							}
							if(goneNodes.getLength() > 0 ){
								incomingHandler.post(new Runnable() {
									public void run() {
										setStatusMessage("gone...");
									}
								});
							}

						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}
		};

		Util.connection.addPacketListener(packetListener, Chatfilter);
		// new code end

		/* // Accept only messages from friend@gmail.com
        PacketFilter filter 
            = new AndFilter(new PacketTypeFilter(Message.class), 
                            new FromContainsFilter("friend@gmail.com"));

        // Collect these messages
        PacketCollector collector = Util.connection.createPacketCollector(filter);

        while(true) {
            Packet packet = collector.nextResult();

            if (packet instanceof Message) {
              Message msg = (Message) packet;
              // Process message
              Log.v("Rajesh", "Got message:" + msg.getBody());
            }
          }*/



		ConnectionDetector conn = new ConnectionDetector(ChatMessagingActivity.this);
		if(conn.isConnectingToInternet()){
			//loading user image

			new LoadImageUrlFromUser().execute();
			DataBaseHandler db = new DataBaseHandler(ChatMessagingActivity.this);
			stickersList = db.getStickers();
			stickersAdapter = new StickersAdapter(ChatMessagingActivity.this, stickersList);
			stickersGridview.setAdapter(stickersAdapter);
			enablePopUpView();
			checkKeyboardHeight(parentLayout);
		}else{
			alert.showAlertDialog(ChatMessagingActivity.this, "Connection Error", "Please check your internet connection", false);
		}

		lastMsgTime = "";

		isInFront = true;

		withUserName.setText(with_user_name);

		//messageText.requestFocusFromTouch();
		//setConnection(Util.connection);

		stickersGridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				Object object = stickersGridview.getItemAtPosition(position);
				StickersModel sticker = (StickersModel) object;
				// TODO Auto-generated method stub
				Log.i("XMPPChatActivity", "Sending text " + "$$STICKER$##$"+sticker.getStickerUrl() + " to " + Connversation_JID);
				Message msg = new Message(Connversation_JID, Message.Type.chat);
				msg.setBody("$$STICKER$##$"+sticker.getStickerUrl());				
				if (Util.connection != null) {
					Util.connection.sendPacket(msg);

					DataBaseHandler datebasehandler = new DataBaseHandler(ChatMessagingActivity.this);
					ConversationsMessagesModel messageModel = new ConversationsMessagesModel();

					messageModel.setConvmessage_type("Chat");
					messageModel.setConvmessage_direction("out");
					messageModel.setConvmessage_time("time");
					messageModel.setConvmessage_message("$$STICKER$##$"+sticker.getStickerUrl());
					messageModel.setConversation_id(ConversationsID);

					messagesArrayList.add(messageModel);

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					lastMsgTime = sdf.format(new Date());

					try{
						datebasehandler.insertConversionsMessages(messageModel);
						datebasehandler.updateLastMessageinConversations(Integer.toString(ConversationsID), "$$STICKER$##$"+sticker.getStickerUrl()
								, "time", "out" ,"yes", "0");
					}catch(Exception e){
						System.out.println(e);
					}finally{
						datebasehandler.close();
					}
				}

				if (!popupWindow.isShowing()) {

					popupWindow.setHeight((int) (keyboardHeight));

					if (isKeyBoardVisible) {
						emoticonsCover.setVisibility(LinearLayout.GONE);
					} else {
						emoticonsCover.setVisibility(LinearLayout.VISIBLE);
					}
					popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);

				} else {
					popupWindow.dismiss();
				}

				stickersGridview.scrollBy(1, 0);
				updateMessages();
			}
		});

		chatMessagesList.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility") @Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow.isShowing())
					popupWindow.dismiss();	
				return false;
			}
		});

		messageText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (popupWindow.isShowing()) {

					popupWindow.dismiss();

				}

			}
		});

		// Defining default height of keyboard which is equal to 230 dip
		final float popUpheight = getResources().getDimension(
				R.dimen.keyboard_height);
		changeKeyboardHeight((int) popUpheight);


		sendStickers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!popupWindow.isShowing()) {

					popupWindow.setHeight((int) (keyboardHeight));

					if (isKeyBoardVisible) {
						emoticonsCover.setVisibility(LinearLayout.GONE);
					} else {
						emoticonsCover.setVisibility(LinearLayout.VISIBLE);
					}
					popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);

				} else {
					popupWindow.dismiss();
				}


			}
		});

		OptionsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builderSingle = new AlertDialog.Builder(
						ChatMessagingActivity.this);
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						ChatMessagingActivity.this,
						android.R.layout.simple_list_item_1);
				if(isUserBlocked.equalsIgnoreCase("0")){
					arrayAdapter.add("View Profile");
				}
				if(adapter.getCount() > 0 ){
					arrayAdapter.add("Clear Conversations");
				}
				if(isUserBlocked.equalsIgnoreCase("0")){
					arrayAdapter.add("Block User");
				}else{
					arrayAdapter.add("Un-Block User");
				}
				builderSingle.setNegativeButton("cancel",
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

						final String strName = arrayAdapter.getItem(which);

						if(strName.equalsIgnoreCase("View Profile")){

							Intent intent = new Intent(ChatMessagingActivity.this,ThirdPartyUserDetailActivity.class);
							intent.putExtra("UserID", withUserId);
							Util.THIRD_PARTY_USER_NAME = withUserFirstName;
							Util.THIRD_PARTY_USER_ID = withUserId;
							startActivity(intent);

						}else{

							AlertDialog.Builder builderInner = new AlertDialog.Builder(
									ChatMessagingActivity.this);

							if(strName.equalsIgnoreCase("Clear Conversations"))
							{
								builderInner.setMessage("Are you sure do you want to delete conversations");
							}else if(strName.equalsIgnoreCase("Block User")) {
								builderInner.setMessage("Are you sure do you want to Block this user");
							}if(strName.equalsIgnoreCase("Un-Block User")){
								builderInner.setMessage("Are you sure do you want to Un-Block this user");
							}
							builderInner.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if(strName.equalsIgnoreCase("Un-Block User")){
										isUserBlocked = "0";
										new UnBlockUser().execute(withUserId);
									}else if(strName.equalsIgnoreCase("Block User")){
										isUserBlocked = "1";
										new BlockUser().execute(withUserId);
									}else if(strName.equalsIgnoreCase("Clear Conversations")){
										DataBaseHandler db = new DataBaseHandler(getApplicationContext());
										db.updateLastMessageinConversations(Integer.toString(ConversationsID), ""
												, "time", "out" ,"yes", "0");
										int i =	db.clearConversations(String.valueOf(ConversationsID));
										if(i > 0){
											messagesArrayList.clear();
											adapter.notifyDataSetChanged();
										}
									}

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
						}
					}
				});
				builderSingle.show();
			}
		});

		postMedia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ConnectionDetector conn = new ConnectionDetector(ChatMessagingActivity.this);
				if(conn.isConnectingToInternet()){
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(
							Intent.createChooser(intent, "Select Picture"),
							SELECT_PICTURE);
				}else{
					alert.showAlertDialog(ChatMessagingActivity.this, "Connection Error", "Please check your internet connection", false);
				}
			}
		});


		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConnectionDetector conn = new ConnectionDetector(ChatMessagingActivity.this);
				if(conn.isConnectingToInternet()){
					if(messageText.getText().toString().length() > 0){
						Log.i("XMPPChatDemoActivity", "Sending text " + messageText.getText().toString() + " to " + Connversation_JID);
						Message msg = new Message(Connversation_JID, Message.Type.chat);
						msg.setBody(messageText.getText().toString());				
						if (Util.connection != null) {
							Util.connection.sendPacket(msg);

							DataBaseHandler datebasehandler = new DataBaseHandler(ChatMessagingActivity.this);
							ConversationsMessagesModel messageModel = new ConversationsMessagesModel();

							messageModel.setConvmessage_type("Chat");
							messageModel.setConvmessage_direction("out");
							SimpleDateFormat sdfinsert = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String strDate = sdfinsert.format(new Date());
							messageModel.setConvmessage_time(strDate);
							messageModel.setConvmessage_message(messageText.getText().toString());
							messageModel.setConversation_id(ConversationsID);

							messagesArrayList.add(messageModel);

							adapter = new ChatTempAdapter(ChatMessagingActivity.this, messagesArrayList);
							adapter.notifyDataSetChanged();

							chatMessagesList.setSelection(chatMessagesList.getCount());

							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							lastMsgTime = sdf.format(new Date());

							try{
								datebasehandler.insertConversionsMessages(messageModel);
								datebasehandler.updateLastMessageinConversations(Integer.toString(ConversationsID), messageText.getText().toString()
										, "time", "out" , "yes" , "0");
							}catch(Exception e){
								System.out.println(e);
							}finally{
								datebasehandler.close();
							}

							/*	InputMethodManager inputManager = (InputMethodManager)
									getSystemService(Context.INPUT_METHOD_SERVICE); 
							inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);*/
							messageText.setText("");
						}
					}else{
						Toast.makeText(ChatMessagingActivity.this, "Add something", Toast.LENGTH_SHORT).show();
					}
				}else{
					alert.showAlertDialog(ChatMessagingActivity.this, "Connection Error", "Please check your internet connection", false);
				}
			}
		});
		//updateMessages();

		messageText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub


				Message msg1 = new Message(Connversation_JID, Message.Type.chat);
				msg1.setFrom(Util.USERNAME+"@youflik.me"); 
				MessageEventManager event=new MessageEventManager(Util.connection);
				event.sendComposingNotification(Connversation_JID,msg1.getPacketID()); 


				/*final IQ iq = new IQ() {
					public String getChildElementXML() { 
						return "<message from='rajesh@youflik.me' to='riya@youflik.me' type='chat'> <thread>act2scene2chat1</thread><away xmlns='http://jabber.org/protocol/chatstates'/></message>"; // here is your query
						//"<iq type='get' from='9f30dacb@web.vlivetech.com/9f30dacb' id='1'> <query xmlns='http://jabber.org/protocol/disco#info'/></iq>";
					}};

					// send the request
					Util.connection.sendPacket(iq);*/

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Message msg1 = new Message(Connversation_JID, Message.Type.chat);
				msg1.setFrom(Util.USERNAME+"@youflik.me"); 
				MessageEventManager event=new MessageEventManager(Util.connection);
				event.sendCancelledNotification(Connversation_JID, msg1.getPacketID());
			}
		});
	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 * 
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	private void changeKeyboardHeight(int height) {

		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			emoticonsCover.setLayoutParams(params);
		}

	}

	/**
	 * Defining all components of emoticons keyboard
	 */
	private void enablePopUpView() {

		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				emoticonsCover.setVisibility(LinearLayout.GONE);
			}
		});
	}

	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;
	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);

						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);

						if (previousHeightDiffrence - heightDifference > 50) {							
							popupWindow.dismiss();
						}

						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {

							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {

							isKeyBoardVisible = false;

						}

					}
				});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE
					&& resultCode == Activity.RESULT_OK && null != data) {

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getApplicationContext().getContentResolver()
						.query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				picfile = new File(picturePath);
				bitmap_postImage = BitmapFactory.decodeFile(picturePath);
				postFlagFileCheck = 1;
				new PostUploadFile().execute("2");
			}

			/*if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK
					&& null != data) {

				Uri selectedVideo = data.getData();
				String[] filePathColumn = { MediaStore.Video.Media.DATA };
				Cursor cursor = getApplicationContext().getContentResolver()
						.query(selectedVideo, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String videoPath = cursor.getString(columnIndex);
				cursor.close();
				videoFile = new File(videoPath);
				bitmap_postImage = ThumbnailUtils.createVideoThumbnail(
						videoPath, Thumbnails.MICRO_KIND);
				postFlagFileCheck = 2;
			}
			if (requestCode == SELECT_CAMERA_IMAGE_REQUEST
					&& resultCode == Activity.RESULT_OK) {
				picfile = mFileTemp;
				postFlagFileCheck = 1;
				bitmap_postImage = BitmapFactory.decodeFile(picfile
						.getAbsolutePath());
			}
			if (requestCode == SELECT_CAMERA_VIDEO_REQUEST
					&& resultCode == Activity.RESULT_OK) {

				videoFile = mVideoFileTemp;
				postFlagFileCheck = 2;
				bitmap_postImage = ThumbnailUtils.createVideoThumbnail(
						videoFile.getPath(), Thumbnails.MICRO_KIND);
			}*/
		}
	}

	public void setMessages(){

		DataBaseHandler db = new DataBaseHandler(getApplicationContext());
		messagesArrayList = db.getConversionMessages(String.valueOf(ConversationsID));
		adapter = new ChatTempAdapter(ChatMessagingActivity.this,messagesArrayList);
		chatMessagesList.setAdapter(adapter);
		chatMessagesList.setSelection(adapter.getCount());

	}

	public void setIncomingMessage(){
		adapter.notifyDataSetChanged();
		chatMessagesList.setSelection(chatMessagesList.getCount());
	}

	public void setStatusMessage(String status){
		withUserComposing.setText(status);
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	public void updateMessages(){
		mHandler.post(new Runnable() {
			public void run() {

				if(Util.IncomingChatMessage){

					//setMessages();

					try{

						DataBaseHandler db = new DataBaseHandler(ChatMessagingActivity.this);

						if(!messagesArrayList.isEmpty()){
							messagesArrayList.clear();
							/*adapter.notifyDataSetChanged();
							chatMessagesList.invalidate();*/

							messagesArrayList.addAll(db.getConversionMessages(String.valueOf(ConversationsID)));
							//db.changeIsseen(String.valueOf(ConversationsID), "yes", "0");
							adapter = new ChatTempAdapter(getApplicationContext(),messagesArrayList);
							chatMessagesList.setAdapter(adapter);
							//Toast.makeText(getApplicationContext(), "Working" ,Toast.LENGTH_SHORT).show();
							//adapter.notifyDataSetChanged();

							//Show soft-keyboard:
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
							//hide keyboard :
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
							//adapter.notifyDataSetChanged();
							chatMessagesList.setSelection(messagesArrayList.size() -1);

						}

						//	messagesArrayList = db.getConversionMessages(String.valueOf(ConversationsID));

						for(ConversationsMessagesModel cmd :messagesArrayList){
							System.out.println(cmd.getConvmessage_message());
						}
						//	adapter.notifyDataSetChanged();
						db.close();

					}
					catch(Exception e){
						e.printStackTrace();
					}

				}
				Util.IncomingChatMessage = false;
			}
		});

	}

	private class LoadImageUrlFromUser extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API + "getUserDet?user_mail_id="+Connversation_JID);
			JSONArray userarray;
			JSONObject details;
			try {
				if(jsonObjectRecived!=null){
					if(jsonObjectRecived.getString("error").equalsIgnoreCase("false")){
						userarray = jsonObjectRecived.getJSONArray("userDet");
						details = userarray.getJSONObject(0);

						Util.with_User_Image = "";

						withUserImage = details.getString("user_profile_picture_path");
						withUserFirstName = details.getString("firstname");
						withUserLastName = details.getString("lastname");
						withUserId = details.getString("user_id");
						isUserBlocked = details.getString("is_block");
						blockID = details.getString("block_id");
						isFriend = details.getString("is_friend");
						// Storing the Image 
						Util.with_User_Image = withUserImage;

						//withUserImage = jsonObjectRecived.getString("user_profile_picture_path");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			DataBaseHandler db = new DataBaseHandler(ChatMessagingActivity.this);
			String withUserFullName; 
			if(withUserLastName.equalsIgnoreCase("null") || withUserLastName.equalsIgnoreCase("") ){
				withUserFullName = withUserFirstName;
			}else
			{
				withUserFullName = withUserFirstName + " " + withUserLastName	;
			}
			withUserName.setText(withUserFullName);
			db.updateImageUrlinConverse(Connversation_JID, withUserImage , withUserFirstName  ,withUserId);

			if(isUserBlocked.equalsIgnoreCase("1") || isFriend.equalsIgnoreCase("0")){

				messageText.setVisibility(View.GONE);
				sendButton.setVisibility(View.GONE);
				postMedia.setVisibility(View.GONE);
				sendStickers.setVisibility(View.GONE);

			}

			imageLoader.getInstance().displayImage(withUserImage, withUserPhoto, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

				}
			}, new ImageLoadingProgressListener() {
				@Override
				public void onProgressUpdate(String imageUri, View view, int current,
						int total) {

				}
			}
					);

		}


	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//Util.connection.removePacketListener(packetListener);
		//ChatMessagingActivity.this.finish();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Util.connection.removePacketListener(packetListener);
		DataBaseHandler db = new DataBaseHandler(ChatMessagingActivity.this);
		db.changeIsseen(Integer.toString(ConversationsID), "yes", "0");
		db.close();
		Intent intent = new  Intent();
		setResult(Activity.RESULT_OK,intent);
		super.finish();
	}

	private class PostUploadFile extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			pDialog = new ProgressDialog(ChatMessagingActivity.this);
			pDialog.setMessage("Please Wait..");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			String flag = params[0];

			if (flag.equalsIgnoreCase("2") || flag.equalsIgnoreCase("4")) {
				JSONObject jsonObj = HttpPostImageForChat.sendHttpPostImage(
						Util.API + "chat_img_upload", picfile,1);
				//System.out.println(jsonObj.toString());
				if (jsonObj != null) {
					try {
						if (jsonObj.getString("error")
								.equalsIgnoreCase("false")) {
							ImageUrl = jsonObj.getString("chat_img_url").toString();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (flag.equalsIgnoreCase("3") || flag.equalsIgnoreCase("5")) {
				JSONObject jsonObj = HttpPostImageForChat.sendHttpPostImage(
						Util.API + "media", videoFile,2);
				System.out.println(jsonObj.toString());
				if (jsonObj != null) {
					try {
						if (jsonObj.getString("error")
								.equalsIgnoreCase("false")) {
							videoUrl = jsonObj.getString("actual_video_path").toString();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			new SendMediaAsTextMsg().execute();
		}

	}

	private class SendMediaAsTextMsg extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			pDialog = new ProgressDialog(ChatMessagingActivity.this);
			pDialog.setMessage("Please Wait..");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.i("XMPPChatDemoActivity", "Sending text " + "$$IMAGE$##$"+ImageUrl + " to " + Connversation_JID);
			Message msg = new Message(Connversation_JID, Message.Type.chat);
			msg.setBody("$$IMAGE$##$"+ImageUrl);				
			if (Util.connection != null) {
				Util.connection.sendPacket(msg);

				DataBaseHandler datebasehandler = new DataBaseHandler(ChatMessagingActivity.this);
				ConversationsMessagesModel messageModel = new ConversationsMessagesModel();

				messageModel.setConvmessage_type("Chat");
				messageModel.setConvmessage_direction("out");
				messageModel.setConvmessage_time("time");
				messageModel.setConvmessage_message("$$IMAGE$##$"+ImageUrl);
				messageModel.setConversation_id(ConversationsID);

				messagesArrayList.add(messageModel);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				lastMsgTime = sdf.format(new Date());

				try{
					datebasehandler.insertConversionsMessages(messageModel);
					datebasehandler.updateLastMessageinConversations(Integer.toString(ConversationsID), "$$IMAGE$##$"+ImageUrl
							, "time", "out" ,"yes", "0");
				}catch(Exception e){
					System.out.println(e);
				}finally{
					datebasehandler.close();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			adapter = new ChatTempAdapter(ChatMessagingActivity.this, messagesArrayList);
			adapter.notifyDataSetChanged();
			chatMessagesList.setSelection(chatMessagesList.getCount());
		}

	}

	private class BlockUser extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String userId = params[0];
			String responseMessage = null;
			JSONObject sendMessageJSONObject = new JSONObject();
			try{
				sendMessageJSONObject.put("blocked_user_id",userId);
				JSONObject receivedJSONResponse = HttpPostClient.sendHttpPost(Util.API+"block",sendMessageJSONObject);
				if(receivedJSONResponse!=null){
					responseMessage = receivedJSONResponse.getString("message");
				}
			}
			catch(JSONException e){
			}
			return responseMessage;
		}
	}

	private class UnBlockUser extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String userId = params[0];
			String responseMessage = null;
			JSONObject sendMessageJSONObject = new JSONObject();
			try{
				sendMessageJSONObject.put("blocked_user_id",userId);
				JSONObject receivedJSONResponse = HttpPutClient.sendHttpPost(Util.API+"block/" +blockID,sendMessageJSONObject);
				if(receivedJSONResponse!=null){
					responseMessage = receivedJSONResponse.getString("message");
				}
			}
			catch(JSONException e){
			}
			return responseMessage;
		}
	}

}
