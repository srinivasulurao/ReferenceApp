package com.aotd.activities;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aotd.helpers.DownLoadPDFAsyncForPending;
import com.aotd.helpers.PdfUploadAsync;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.Question;
import com.aotd.parsers.DispatchOrderParser;
import com.aotd.parsers.UploadImageDataParser;
import com.aotd.utils.Utils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PendingOrders extends Activity {

	JSONArray ja=null;
	String deliveryType="first";
	String condition="updateSingDelivered";

	String sLastName = "";
	String sNotes= "";
	String sURL= "";
	String sImgStr= "";
	String sFileName = "";
	String _id = "";
	String currentDateandTime="";
	int position_;
	byte[] decodedString;
	String OrderNum="";
	String yes_no="";
	
	FileOutputStream fout;
	private ProgressDialog progressdialog;
	String sIn="";
	public ImageView imgOnline;
	boolean isClicked = false;
	String driverId="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pending_orders);
		
		SharedPreferences loginprefs = this.getSharedPreferences("loginprefs", 0);
		driverId = loginprefs.getString("username", "none");
		
		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);

		OfflineDB db = new OfflineDB(this);
		final ArrayList<DispatchAllListModel> contacts = db.getAllContacts__();

		ListView lvPending = (ListView)findViewById(R.id.pending_listView);
		PendingAdapter PAdapter = new PendingAdapter(contacts);
		lvPending.setAdapter(PAdapter);
		
		
		ToggleButton toggle = (ToggleButton)findViewById(R.id.toggle);
		Button btnResendAll = (Button)findViewById(R.id.btnResendAll);
		
		if(Utils.checkNetwork(PendingOrders.this))
		{
			toggle.setChecked(true);
		}
		else
		{
			toggle.setChecked(false);
		}
		
		toggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				boolean on = ((ToggleButton) v).isChecked();
				
				if(on)
				{
					
					WifiManager wifiManager ;
					wifiManager  = (WifiManager)getSystemService(WIFI_SERVICE);
					wifiManager.setWifiEnabled(true);   
					
					try{
					ConnectivityManager dataManager;
					dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
					Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
					dataMtd.setAccessible(true);
					dataMtd.invoke(dataManager, true);
					
					}catch(Exception e){}
				}
				else
				{
					
					WifiManager wifiManager ;
					wifiManager  = (WifiManager)getSystemService(WIFI_SERVICE);
					wifiManager.setWifiEnabled(false);   
					
					try{
					ConnectivityManager dataManager;
					dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
					Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
					dataMtd.setAccessible(true);
					dataMtd.invoke(dataManager, false);
					
					}catch(Exception e){}
				}
			}
		});


		btnResendAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(Utils.checkNetwork(PendingOrders.this)){
				progressdialog = ProgressDialog.show(PendingOrders.this,null,null);
				progressdialog.setContentView(R.layout.loader);
				
				progressdialog = ProgressDialog.show(PendingOrders.this, "",	"Prcessing..", true);
				progressdialog.show();
				
				for(int i=0;i<contacts.size();i++)
				{
					DispatchAllListModel dm = contacts.get(i);
					
					sLastName = dm.getLastname();
					sNotes= dm.getNotes();
					sURL= dm.getUrl();
					sImgStr= dm.getImgstr();
					yes_no = dm.getYesno();
					sFileName =dm.getFilename();
					_id = dm.getId();
					currentDateandTime = dm.getDatetime();
					position_ = i;
					OrderNum=dm.getOrder_id();

					if(dm.getOrder_Status().equalsIgnoreCase("Picked up"))
					{

						try{
							List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

							pairs.add(new BasicNameValuePair("roleName",Utils.ROLENAME));  
							pairs.add(new BasicNameValuePair("order_ids",dm.getOrder_id()));
							pairs.add(new BasicNameValuePair("datetime",currentDateandTime));  


							DefaultHttpClient httpClient = new DefaultHttpClient();
							HttpPost httpPost = new HttpPost(Utils.PICKUP_URL);
							httpPost.setEntity(new UrlEncodedFormEntity(pairs));

							HttpResponse httpResponse = httpClient.execute(httpPost);
							HttpEntity httpEntity = httpResponse.getEntity();
							String str2 = EntityUtils.toString(httpEntity);

							if(str2.indexOf("<info>") != -1)
							{
								OfflineDB db = new OfflineDB(PendingOrders.this);
								db.delete_byID(Integer.parseInt(_id));

							}
							else
							{
								Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
							}

						}catch(Exception e){}

					
					}
					else if(dm.getOrder_Status().equalsIgnoreCase("Dispatch"))
					{
						sIn="dispatch";
						try{
						String encodedURoleName = URLEncoder.encode(Utils.ROLENAME.trim(), "UTF-8");
						String encodedOrderIds = URLEncoder.encode(OrderNum, "UTF-8");
						String data = "roleName=" + encodedURoleName + "&order_ids="+ encodedOrderIds + "&driver_id=" + driverId;
						
						DispatchOrderParser mResponseParser = null;
						mResponseParser = new DispatchOrderParser(Utils.UPLOAD_ORDERS_URL,new DispatchHandler(_id));
						mResponseParser.isPost = true;
						mResponseParser.postData = data;
						mResponseParser.start();
						
						}catch(Exception e){}
						
					
					}
					else
					{
						decodedString = Base64.decode(sImgStr, Base64.DEFAULT);

						String OrderNum = dm.getOrder_id();
						String pdfURL = "http://50.63.55.253/Mobile/downloadPDFSignature.php?orderNumber="+OrderNum;

						sIn ="all";
						
						//1. Download the PDF:
						downloadFile(pdfURL, OrderNum);
						
						String file__ = Environment.getExternalStorageDirectory()+"/AOTD/"+OrderNum+".pdf";
						File f = new File(file__);
						if(f.exists())
						{
						//2. Attach Signature:
						Bitmap scaled = getResizedBitmap(decodedString, 80, 350);
						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						scaled.compress(Bitmap.CompressFormat.JPEG, 100, bs);

						try{
							String file = Environment.getExternalStorageDirectory()+"/AOTD/"+OrderNum+".pdf";
							PdfReader reader = new PdfReader(file);

							String dir = Environment.getExternalStorageDirectory() + "/AOTD/"; // 1page
							String dlOrRT="";
							if(condition.equalsIgnoreCase("updateSingDelivered"))
							{
								dlOrRT = "dl";
							}else{
								dlOrRT = "rt";
							}

							String orderNumber = OrderNum;
							String signFile = dir +dlOrRT+orderNumber+ "_sign.pdf";
							Document document = new Document();

							PdfStamper pdfStamper = new PdfStamper(reader,	new FileOutputStream(signFile));
							Image image = Image.getInstance(bs.toByteArray());

							DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							Date date = new Date();
							String sDate = dateFormat.format(date).toString();

							
							for (int j = 1; j <= reader.getNumberOfPages(); j++) {

								PdfContentByte content = pdfStamper.getUnderContent(j);
								content = pdfStamper.getOverContent(j);
								image.setAbsolutePosition(0,0);
								content.addImage(image);
							}


							document.close();
							pdfStamper.close();

						}catch(Exception e){
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
						}
						
						// UPLOAD PDF TO SERVER:
						pdfUpload(OrderNum, condition, yes_no);
						
						UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new DataUploadHandler(position_, _id),sURL, "aotd");

						Hashtable parameters = new Hashtable<String, String>();
						parameters.put("notes", sNotes);
						parameters.put("lastname", sLastName);
						parameters.put("datetime", currentDateandTime);

						mUploadImageDataParser.isMultipart=true;
						mUploadImageDataParser.params = parameters;
						mUploadImageDataParser.imgBytes = decodedString;
						mUploadImageDataParser.mFileName = sFileName;
						mUploadImageDataParser.start();
						
						}
						else
						{
							pdfUpload(OrderNum, condition, yes_no);
							
							UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new DataUploadHandler(position_, _id),sURL, "aotd");

							Hashtable parameters = new Hashtable<String, String>();
							parameters.put("notes", sNotes);
							parameters.put("lastname", sLastName);
							parameters.put("datetime", currentDateandTime);

							mUploadImageDataParser.isMultipart=true;
							mUploadImageDataParser.params = parameters;
							mUploadImageDataParser.imgBytes = decodedString;
							mUploadImageDataParser.mFileName = sFileName;
							mUploadImageDataParser.start();
						}
						
						
					}
				}
				
				
				progressdialog.dismiss();
				startActivity(new Intent(PendingOrders.this,MainDispatchScreenTabView.class)
				.putExtra("from", "login"));				
				finish();
				
				}
				else
					Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
		imgOnline.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(isClicked)
				{
					if(Utils.NetworkType(PendingOrders.this).equalsIgnoreCase(Utils.wifi))
					{
						Utils.wifiOFF(PendingOrders.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					else if(Utils.NetworkType(PendingOrders.this).equalsIgnoreCase(Utils.mobile))
					{
						Utils.mobileDataOFF(PendingOrders.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					
				}
				else
				{
					imgOnline.setBackgroundResource(R.drawable.online);
					isClicked = true;
					Utils.switchOnInternet(PendingOrders.this);
				}

				return false;
			}
		});

		Button bBack = (Button)findViewById(R.id.back);
		Button bClear = (Button)findViewById(R.id.clear);
		
		bBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		bClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				OfflineDB db = new OfflineDB(PendingOrders.this);
				db.deleteAll();
				finish();
			}
		});
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(Utils.NetworkType(PendingOrders.this).equalsIgnoreCase(Utils.wifi))
		{
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else if(Utils.NetworkType(PendingOrders.this).equalsIgnoreCase(Utils.mobile))
		{
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else
		{
			imgOnline.setBackgroundResource(R.drawable.offline);
			isClicked = false;
		}

	}
	
	void pdfUpload(String orderNumber, String condition, String yesno)
	{
		String fileName="", dlOrRt ="";
		String urlServer = Utils.PDF_SIGN_UPLOAD_URL;
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;
		String boundary = "*****";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		
		if (condition.equals("updateSingDelivered")) {
			fileName = "dl" + orderNumber + ".pdf";
			dlOrRt = "dl";
		} else if (condition.equals("UpdateSecondSignatureForDeliver")) {
			fileName = "rt" + orderNumber + ".pdf";
			dlOrRt = "rt";
		}
		
		if (new File(Environment.getExternalStorageDirectory() + "/AOTD/"+dlOrRt
				+ orderNumber + "_sign.pdf").exists())
		{
			try
			{

				String pathToOurFile = Environment.getExternalStorageDirectory()
						+ "/AOTD/" + dlOrRt+orderNumber + "_sign.pdf";
				FileInputStream fileInputStream = new FileInputStream(new File(
						pathToOurFile));

				URL url = new URL(urlServer);
				connection = (HttpURLConnection) url.openConnection();
				
//				connection.setConnectTimeout(30*1000);// 1 min
//				connection.setReadTimeout(30000);
				// Allow Inputs & Outputs
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				// Enable POST method
				connection.setRequestMethod("POST");

				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				outputStream = new DataOutputStream(
						connection.getOutputStream());
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
								+ fileName + "\"" + lineEnd);
				
				outputStream.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// Read file
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					outputStream.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens
						+ lineEnd);

				// Responses from the server (code and message)
				int serverResponseCode = connection.getResponseCode();
				String serverResponseMessage = connection.getResponseMessage();

				Log.i("", "kkk uploadpdf response massage is: "
						+ serverResponseMessage);

				fileInputStream.close();
				outputStream.flush();
				outputStream.close();

				
			}catch(Exception e){}
		}

	}
	void downloadFile(String url__, String ordernum){
        
        try {
            URL url = new URL(url__);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
 
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
 
            //connect
            urlConnection.connect();
 
            //set the path where we want to save the file           
            File SDCardRoot = Environment.getExternalStorageDirectory(); 
            //create a new file, to save the downloaded file 
           // File file = new File(SDCardRoot,ordernum+".pdf");
            String file = Environment.getExternalStorageDirectory()+"/AOTD/"+OrderNum+".pdf";
            FileOutputStream fileOutput = new FileOutputStream(file);
 
            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
 
            //this is the total size of the file which we are downloading
           final int totalSize = urlConnection.getContentLength();
 
            runOnUiThread(new Runnable() {
                public void run() {
                   // pb.setMax(totalSize);
                }               
            });
            
             
            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
 
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            //   final int downloadedSize += bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                       
                        //float per = ((float)downloadedSize/totalSize) * 100;
                       // cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    // pb.dismiss(); // if you want close it..
                }
            });         
         
        } catch (final MalformedURLException e) {
            //showError("Error : MalformedURLException " + e);        
            e.printStackTrace();
        } catch (final IOException e) {
            //showError("Error : IOException " + e);          
            e.printStackTrace();
        }
        catch (final Exception e) {
            //showError("Error : Please <span id="IL_AD9" class="IL_AD">check your</span> internet connection " + e);
        }       
    }
	private void downloadPDF(String pdfurl, String ordernum)
	{
		try{
		InputStream is = downloadPdf(pdfurl);
		
		if (is != null ) {
			setFileName(pdfurl, ordernum);
			writePdfFile(is, fout);
			
		}
		
		
		}
		catch(Exception e){}
	}

	private void setFileName(String pdfUrl, String orderName) throws Exception {
		Log.i("", "kkk pdf url: "+pdfUrl);
		// set local filename to last part of URL
		try {
			String mFileName = orderName + ".pdf";

			// String[] strURLParts = pdfUrl.split("/");
			// if (strURLParts.length > 0) {
			// mFileName = strURLParts[strURLParts.length - 1];
			// if (!(mFileName.contains(".pdf"))) {
			// mFileName = mFileName + ".pdf";
			// }
			// } else {
			// mFileName = orderName + ".pdf";
			// }

			String newFolder = "/AOTD";
			String extStorageDirectory = Environment
					.getExternalStorageDirectory().toString();
			File myNewFolder = new File(extStorageDirectory + newFolder);
			if (!myNewFolder.exists()) {
				myNewFolder.mkdir();
			}
			File file = new File(myNewFolder, mFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fout = new FileOutputStream(file);
		} catch (IOException e) {
			
		}
	}
	
	private void writePdfFile(InputStream is, FileOutputStream fout)
			throws FileNotFoundException, IOException {
		// byte[] dataBuffer = new byte[4096];
		// int nRead = 0;
		// BufferedInputStream bufferedStreamInput = new
		// BufferedInputStream(is);
		// // must be world readable so external Intent can open!
		// @SuppressWarnings("deprecation")
		// FileOutputStream outputStream = mContext.openFileOutput(mFileName,
		// Context.MODE_WORLD_WRITEABLE);
		// while ((nRead = bufferedStreamInput.read(dataBuffer)) > 0) {
		// outputStream.write(dataBuffer, 0, nRead);
		// }
		// is.close();
		// outputStream.close();

		try {

			byte[] buffer = new byte[4096];
			int readLen = 0;
			BufferedInputStream bufferedStreamInput = new BufferedInputStream(
					is);
			while ((readLen = bufferedStreamInput.read(buffer)) > 0) {
				fout.write(buffer, 0, readLen);
			}
			fout.close();
		} catch (Exception e) {
			

		}

	}
	
	private InputStream downloadPdf(String pdfUrl) {
		Log.i("", "kkk download pdf url :"+pdfUrl);
		
		try {
			URL u = new URL(pdfUrl);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			


			Log.i("", "kkk download pdf file respone code is :"+c.getResponseCode());
			Log.i("", "kkk download pdf file respone meassage is :"+c.getResponseMessage());

			int code = c.getResponseCode();
			String sMsg = c.getResponseMessage();
			
			if(c.getResponseCode() == 200){
				InputStream in = c.getInputStream();
				c.disconnect();
				return in;
			}else{
				return null;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;

	}

	
	public class PendingAdapter extends BaseAdapter
	{
		ArrayList<DispatchAllListModel> mDataFeeds = null;

		public PendingAdapter(ArrayList<DispatchAllListModel> mDataFeeds ) 
		{
			this.mDataFeeds = mDataFeeds;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(mDataFeeds.size() > 0){
				return mDataFeeds.size();}

			else
			{
				return 0;
			}
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null){
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.resend, null);

			}

			final Button mOrderNum  	= (Button)v.findViewById(R.id.dispatch_order_num_btn);
			final Button mOrderStatus  = (Button)v.findViewById(R.id.dispathch_list_row_btn_order_status);
			final Button bResend = (Button)v.findViewById(R.id.resend);
			bResend.setFocusable(false);

			TextView mTime		= (TextView)v.findViewById(R.id.dispathch_listrow_date);
			TextView puCompany	= (TextView)v.findViewById(R.id.dispathch_listrow_PU_company);
			TextView puAddress	= (TextView)v.findViewById(R.id.dispathch_listrow_PU_address);		
			TextView dlCompany	= (TextView)v.findViewById(R.id.dispathch_listrow_DL_company);
			TextView dlAddress	= (TextView)v.findViewById(R.id.dispathch_listrow_DL_address);
			final TextView txtTag	= (TextView)v.findViewById(R.id.tag);

			String sUserName = mDataFeeds.get(position).getUsername();
			String sPassword = mDataFeeds.get(position).getPassword();

			String sateTime = mDataFeeds.get(position).getAccountnotes();

			SharedPreferences loginprefs1;
			loginprefs1	= getSharedPreferences("loginprefs", 0);	
			String prefUserid = loginprefs1.getString("username","");
			String userRole = loginprefs1.getString("role","");
			String password = loginprefs1.getString("password","");

			if(sUserName.equals(prefUserid) && sPassword.equals(password))
			{


				if(mDataFeeds.get(position).getCompany() != null)
					puCompany.setText("P/U : "+mDataFeeds.get(position).getCompany().trim());
				else{
					puCompany.setVisibility(View.GONE);
					puAddress.setVisibility(View.GONE);
				}

				if(mDataFeeds.get(position).getAddress() != null && mDataFeeds.get(position).getAddress()!= null)
					puAddress.setText("Address: "+((mDataFeeds.get(position).getAddress().length()>0) ? mDataFeeds.get(position).getAddress()+"\n" : ""));
				else
					puAddress.setVisibility(View.GONE);

				if(mDataFeeds.get(position).getDlcompany() != null)
					dlCompany.setText("D/L : "+mDataFeeds.get(position).getDlcompany().trim());	
				else{

					dlCompany.setVisibility(View.GONE);
					dlAddress.setVisibility(View.GONE);
				}

				if(mDataFeeds.get(position).getDladdress() != null && mDataFeeds.get(position).getDlcompany() != null)
					dlAddress.setText("Address: "+((mDataFeeds.get(position).getDladdress().length()>0) ? mDataFeeds.get(position).getDladdress()+"\n" : ""));
				else
					dlAddress.setVisibility(View.GONE);

				mTime.setText(mDataFeeds.get(position).getRDDate().trim());					 
				mTime.setTextColor(Color.BLACK); 

				mOrderNum.setText(mDataFeeds.get(position).getOrder_id().trim());
				mOrderNum.setTextColor(Color.BLACK);

				if(mDataFeeds.get(position).getOrder_Status().equals("Picked up"))
				{ 
					mOrderStatus.setText("Picked up");
					mOrderStatus.setBackgroundResource(R.drawable.btn_pick_green);
					txtTag.setText("0");
				}
				else if(mDataFeeds.get(position).getOrder_Status().equals("Dispatch"))
				{
					mOrderStatus.setText("Dispatch");
					mOrderStatus.setBackgroundResource(R.drawable.btn_dispatch_confirm);
				}
				else{

					mOrderStatus.setText("Delivered"); 
					mOrderStatus.setBackgroundResource(R.drawable.btn_delivered);
					txtTag.setText("1");

				}

				mTime.setText(mDataFeeds.get(position).getRDDate());

				String signDelivery = mDataFeeds.get(position).getSignDelivery().trim();
				String signRT = mDataFeeds.get(position).getSignRoundTrip().trim();
				String RT = mDataFeeds.get(position).getIsRoundTrip();

				final String sTag  = mDataFeeds.get(position).getTag();



				mOrderStatus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if(mOrderStatus.getText().toString().trim().equalsIgnoreCase("Picked up"))
						{

							ArrayList<DispatchAllListModel> al = new ArrayList<DispatchAllListModel>();
							DispatchAllListModel aaa = mDataFeeds.get(position);
							al.add(aaa);

							Bundle bundleObject = new Bundle();
							bundleObject.putSerializable("key", al);

							Question.offDel = al;

							Intent intent = new Intent(PendingOrders.this, Off_delivered_page.class);
							intent.putExtra("orderNumber", mDataFeeds.get(position).getOrder_id());
							intent.putExtra("openorder", "false");
							intent.putExtra("condition", "updateSingDelivered");
							Utils.isRoundTrip = mDataFeeds.get(position).getIsRoundTrip();
							intent.putExtras(bundleObject);
							startActivity(intent);
						}
						else if(mOrderStatus.getText().toString().trim().equalsIgnoreCase("Delivered"))
						{
							
						}
						else
						{
							ArrayList<DispatchAllListModel> al = new ArrayList<DispatchAllListModel>();
							DispatchAllListModel aaa = mDataFeeds.get(position);
							al.add(aaa);

							Bundle bundleObject = new Bundle();
							bundleObject.putSerializable("key", al);

							Question.mListClass = al;
							Intent intent = new Intent(PendingOrders.this, DeliveryOrderInfoPickUp.class);
							intent.putExtra("orderNumber", mDataFeeds.get(position).getOrder_id());
							intent.putExtras(bundleObject);
							startActivity(intent);
						}


					}
				});

				bResend.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						sLastName = mDataFeeds.get(position).getLastname();
						sNotes= mDataFeeds.get(position).getNotes();
						sURL= mDataFeeds.get(position).getUrl();
						sImgStr= mDataFeeds.get(position).getImgstr();
						yes_no = mDataFeeds.get(position).getYesno();
						sFileName = mDataFeeds.get(position).getFilename();
						_id = mDataFeeds.get(position).getId();
						currentDateandTime = mDataFeeds.get(position).getDatetime();
						position_ = position;
						OrderNum=mDataFeeds.get(position).getOrder_id();

						String s = yes_no;
						String date = currentDateandTime;
						String tag = mDataFeeds.get(position).getTag();

						if(Utils.checkNetwork(PendingOrders.this))
						{

							if(mDataFeeds.get(position).getOrder_Status().equals("Picked up"))
							{

								try{
									List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

									pairs.add(new BasicNameValuePair("roleName",Utils.ROLENAME));  
									pairs.add(new BasicNameValuePair("order_ids",mDataFeeds.get(position).getOrder_id()));
									pairs.add(new BasicNameValuePair("datetime",currentDateandTime));  


									DefaultHttpClient httpClient = new DefaultHttpClient();
									HttpPost httpPost = new HttpPost(Utils.PICKUP_URL);
									httpPost.setEntity(new UrlEncodedFormEntity(pairs));

									HttpResponse httpResponse = httpClient.execute(httpPost);
									HttpEntity httpEntity = httpResponse.getEntity();
									String str2 = EntityUtils.toString(httpEntity);

									if(str2.indexOf("<info>") != -1)
									{
										OfflineDB db = new OfflineDB(PendingOrders.this);
										db.delete_byID(Integer.parseInt(_id));

										startActivity(new Intent(PendingOrders.this,MainDispatchScreenTabView.class)
										.putExtra("from", "login"));				
										finish();
									}
									else
									{
										Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
									}

								}catch(Exception e){}

							}
							else if(mDataFeeds.get(position).getOrder_Status().equals("Dispatch"))
							{
								try{
								String encodedURoleName = URLEncoder.encode(Utils.ROLENAME.trim(), "UTF-8");
								String encodedOrderIds = URLEncoder.encode(OrderNum, "UTF-8");
								String data = "roleName=" + encodedURoleName + "&order_ids="+ encodedOrderIds + "&driver_id=" + driverId;
								
								DispatchOrderParser mResponseParser = null;
								mResponseParser = new DispatchOrderParser(Utils.UPLOAD_ORDERS_URL,new DispatchHandler(_id));
								mResponseParser.isPost = true;
								mResponseParser.postData = data;
								mResponseParser.start();
								
								}catch(Exception e){}
								
							}
							else
							{

								sIn="single";
								decodedString = Base64.decode(sImgStr, Base64.DEFAULT);

								String OrderNum = mDataFeeds.get(position).getOrder_id();
								String pdfURL = "http://50.63.55.253/Mobile/downloadPDFSignature.php?orderNumber="+OrderNum;

								pdfDownloadedView(pdfURL,OrderNum);

							}
						}
						else
						{
							Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
						}

					}
				});

			}
			return v;


		}

	}

	@SuppressLint("HandlerLeak")
	class DispatchHandler extends Handler 
	{
		String pId;
		public DispatchHandler(String mId) 
		{
			this.pId = mId;
		}
		
		public void handleMessage(android.os.Message msg) 
		{
			String errorMsg = msg.getData().getString("HttpError");
			
			if(errorMsg.length()>0)
			{
				
			}
			else
			{
				if(sIn.equalsIgnoreCase("dispatch"))
				{
				OfflineDB db = new OfflineDB(PendingOrders.this);
				db.delete_byID(Integer.parseInt(pId));
				}
				else{
					
					OfflineDB db = new OfflineDB(PendingOrders.this);
					db.delete_byID(Integer.parseInt(pId));
					
					startActivity(new Intent(PendingOrders.this,MainDispatchScreenTabView.class)
					.putExtra("from", "login"));				
					finish();
				}
				
				
			}
		}
	}
	
	class UploadPdfHandler extends Handler{

		public void handleMessage(android.os.Message msg)
		{
			if(msg.what==200)
			{
				UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new DataUploadHandler(position_, _id),sURL, "aotd");

				Hashtable parameters = new Hashtable<String, String>();
				parameters.put("notes", sNotes);
				parameters.put("lastname", sLastName);
				parameters.put("datetime", currentDateandTime);

				mUploadImageDataParser.isMultipart=true;
				mUploadImageDataParser.params = parameters;
				mUploadImageDataParser.imgBytes = decodedString;
				mUploadImageDataParser.mFileName = sFileName;
				mUploadImageDataParser.start();
			}
		}
	}
	private Bitmap getResizedBitmap(byte[] bs2,int h,int w){
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeByteArray(bs2, 0, bs2.length, bmpFactoryOptions);

		int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)h);
		int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)w);

		if (heightRatio > 1 || widthRatio > 1)
		{
			if (heightRatio > widthRatio){
				bmpFactoryOptions.inSampleSize = heightRatio; 
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;  
			}  
		}
		bmpFactoryOptions.inDither = false;
		bmpFactoryOptions.inScaled = false;
		bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

		bmpFactoryOptions.inJustDecodeBounds = false;

		bm = BitmapFactory.decodeByteArray(bs2, 0, bs2.length, bmpFactoryOptions);
		//		// Resize
		//	      Matrix matrix = new Matrix();
		//	      matrix.postScale(heightRatio, widthRatio);
		//	      
		//	      bm = Bitmap.createBitmap(bm, 0, 0, h, w, matrix, true);

		return bm;

	}

	class DataUploadHandler extends Handler
	{
		int pos;
		String mId;
		DataUploadHandler(int position, String id)
		{
			this.pos = position;
			this.mId = id;
		}

		public void handleMessage(android.os.Message msg)
		{ 


			String errorMsg=msg.getData().getString("HttpError");
			String successMsg=msg.getData().getString("success");


			if(errorMsg.length()>0){

				Log.v("erromsg",errorMsg);
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

			}
			else{

				OfflineDB db = new OfflineDB(PendingOrders.this);
				db.delete_byID(Integer.parseInt(mId));

				if(sIn.equalsIgnoreCase("single"))
				{
				startActivity(new Intent(PendingOrders.this,MainDispatchScreenTabView.class)
				.putExtra("from", "login"));				
				finish();
				}
			}
		}



	}

	@SuppressLint("HandlerLeak")
	public class PdfHandler extends Handler{

		public PdfHandler()
		{

		}

		@Override
		public void handleMessage(Message msg) {

			if(msg.what == 0){

			


				UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new DataUploadHandler(position_, _id),sURL, "aotd");

				Hashtable parameters = new Hashtable<String, String>();
				parameters.put("notes", sNotes);
				parameters.put("lastname", sLastName);
				parameters.put("datetime", currentDateandTime);

				mUploadImageDataParser.isMultipart=true;
				mUploadImageDataParser.params = parameters;
				mUploadImageDataParser.imgBytes = decodedString;
				mUploadImageDataParser.mFileName = sFileName;
				mUploadImageDataParser.start();

			}
			else{

				

				//ATTACH SIGNATURE :

				Bitmap scaled = getResizedBitmap(decodedString, 80, 350);
				ByteArrayOutputStream bs = new ByteArrayOutputStream();
				scaled.compress(Bitmap.CompressFormat.JPEG, 100, bs);

				try{
					String file = Environment.getExternalStorageDirectory()+"/AOTD/"+OrderNum+".pdf";
					PdfReader reader = new PdfReader(file);

					String dir = Environment.getExternalStorageDirectory() + "/AOTD/"; // 1page
					String dlOrRT="";
					if(condition.equalsIgnoreCase("updateSingDelivered"))
					{
						dlOrRT = "dl";
					}else{
						dlOrRT = "rt";
					}

					String orderNumber = OrderNum;
					String signFile = dir +dlOrRT+orderNumber+ "_sign.pdf";
					Document document = new Document();

					PdfStamper pdfStamper = new PdfStamper(reader,	new FileOutputStream(signFile));
					Image image = Image.getInstance(bs.toByteArray());

					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					String sDate = dateFormat.format(date).toString();

					/*for (int i = 1; i <= reader.getNumberOfPages(); i++) {

						PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(signFile));
						Font FONT = new Font(Font.FontFamily.TIMES_ROMAN, 06, Font.BOLD, new BaseColor(0, 0, 0));
						PdfContentByte canvas = pdfStamper.getOverContent(1);
						ColumnText.showTextAligned(
								canvas,
								Element.ALIGN_BASELINE, 
								new Phrase(sDate, FONT), 
								05, 05, 0
								);
						document.open();
						writer.setPageEmpty(false);
						document.newPage();

					}*/
					for (int i = 1; i <= reader.getNumberOfPages(); i++) {

						PdfContentByte content = pdfStamper.getUnderContent(i);
						content = pdfStamper.getOverContent(i);
						image.setAbsolutePosition(0,0);
						content.addImage(image);
					}


					document.close();
					pdfStamper.close();

				}catch(Exception e){
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
				}

				// UPLOAD PDF TO SERVER:
				PdfUploadAsync mPdfUploadAsync = new PdfUploadAsync(PendingOrders.this,OrderNum,condition,new UploadPdfHandler(),yes_no);
				mPdfUploadAsync.execute();




			}
		}

	}

	protected void pdfDownloadedView(String pdfUrl, String ordernum) 
	{
		if (Utils.checkNetwork(PendingOrders.this)) {

			DownLoadPDFAsyncForPending pdfDownload = new DownLoadPDFAsyncForPending(PendingOrders.this, new PdfHandler());
			pdfDownload.execute(pdfUrl, ordernum);
		}

	}



}

















