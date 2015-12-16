package com.aotd.activities;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.DownLoadPDFAsync;
import com.aotd.model.DetailDeliveryModel;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.Question;
import com.aotd.parsers.DispatchDeliveryParser;
import com.aotd.utils.Utils;

public class DeliveryOrderInfoDelivery extends Activity {

	private Button delivered, scanBtn, scanBottom, deliverBottom, btnCamera;
	private TextView orderNum, accountName, accountNotes, pickUp_from,
			delivery_to, rfc, puinstruction, dlinstruction, serviceType,
			pickupReady, requestedBy, pieces, weight, roundTrip, adminNotes;

	private ProgressDialog progress = null;
	private DetailDeliveryModel mDetailDeliveryModel = null;
	private String openorderStatus;
	public ImageView imgOnline;

	String orderId = "", deliveryType = "first";
	boolean isClicked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		orderId = getIntent().getStringExtra("orderNumber");

		setContentView(R.layout.aotd_delivery_order_info_delivery);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		intializeUI();

		Bundle bundleObject = getIntent().getExtras();
		final ArrayList<DispatchAllListModel> classObject = (ArrayList<DispatchAllListModel>) bundleObject
				.getSerializable("key");

		boolean a = getIntent().getStringExtra("condition").equalsIgnoreCase(
				"UpdateSecondSignatureForDeliver");
		if (getIntent().getStringExtra("condition").equalsIgnoreCase(
				"UpdateSecondSignatureForDeliver")) {
			delivered
					.setBackgroundResource(R.drawable.btn_past_delivered_roundtrip);
			deliverBottom
					.setBackgroundResource(R.drawable.btn_past_delivered_roundtrip);
			deliveryType = "second";
		}

		if (Utils.checkNetwork(getApplicationContext())) {
			progress = ProgressDialog.show(DeliveryOrderInfoDelivery.this,
					null, null);
			progress.setContentView(R.layout.loader);
			try {
				String encodedUserId = URLEncoder.encode(Utils.ROLENAME.trim(),
						"UTF-8");

				String encodedorderId = URLEncoder.encode(getIntent()
						.getStringExtra("orderNumber"), "UTF-8");

				String url = String.format(Utils.DISPATCH_DELIVERY_URL,
						encodedUserId, encodedorderId);

				DispatchDeliveryParser mDispatchparser = new DispatchDeliveryParser(
						new DispatchDeliveryHandler(), url,
						mDetailDeliveryModel);
				mDispatchparser.start();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Error in porcessing login details, contact support",
						Toast.LENGTH_LONG).show();
			}
		} else {

			/*
			 * AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
			 * mDetailDeliveryModel = _AOTDDataBase.getOrderDetails(orderId); if
			 * (mDetailDeliveryModel != null) setData();
			 */

			Question.mListClass = classObject;
			setOfflineData(classObject.get(0));

		}

		delivered.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Utils.checkNetwork(DeliveryOrderInfoDelivery.this)) {

					String DLName_ = mDetailDeliveryModel.getDLcompany();
					String sPeice = mDetailDeliveryModel.getPiece();
					Question.setDLName(DLName_);
					Question.setPIECE(sPeice);

					String orderNum = getIntent().getStringExtra("orderNumber");

					ArrayList<DetailDeliveryModel> aListClass = new ArrayList<DetailDeliveryModel>();
					Question.aListClass = aListClass;
					aListClass.add(mDetailDeliveryModel);
					Question.aListClass = aListClass;

					String pdfUrl = String.format(Utils.PDF_DOWNLOAD_URL,
							orderNum);
					String newFolder = "/AOTD";
					String extStorageDirectory = Environment
							.getExternalStorageDirectory().toString();
					File myNewFolder = new File(extStorageDirectory + newFolder);
					if (myNewFolder.exists()) {
						File file = new File(myNewFolder, getIntent()
								.getStringExtra("orderNumber") + ".pdf");
						if (file.exists()) {
							Intent sign_intent = new Intent(
									DeliveryOrderInfoDelivery.this,
									PdfSignatureActivity.class);
							sign_intent.putExtra("orderNumber", getIntent()
									.getStringExtra("orderNumber"));
							sign_intent.putExtra("roundtrip", getIntent()
									.getStringExtra("roundtrip"));
							sign_intent.putExtra("condition", getIntent()
									.getStringExtra("condition"));
							sign_intent.putExtra("deliveryType", deliveryType);
							sign_intent.putExtra("company",
									mDetailDeliveryModel.getDLcompany());
							sign_intent.putExtra("from", "aotd");
							sign_intent.putExtra("Piece", sPeice);
							startActivity(sign_intent);
						} else {
							// download pdf async calling
							if (Utils
									.checkNetwork(DeliveryOrderInfoDelivery.this)) {

								pdfDownloadedView(pdfUrl);
							} else {

								// alertDialog("AOTD","There is no active network, Please try again.");

								/* latest modification for offline mode */
								Log.i("",
										"kkk orderNumber"
												+ getIntent().getStringExtra(
														"orderNumber"));
								String DLName = mDetailDeliveryModel
										.getDLcompany();
								Question.setDLName(DLName);
								Intent sign_intent = new Intent(
										DeliveryOrderInfoDelivery.this,
										SignatureActivity.class);
								sign_intent.putExtra("orderNumber", getIntent()
										.getStringExtra("orderNumber"));
								sign_intent.putExtra("roundtrip", getIntent()
										.getStringExtra("roundtrip"));
								sign_intent.putExtra("condition", getIntent()
										.getStringExtra("condition"));
								sign_intent.putExtra("deliveryType",
										deliveryType);
								sign_intent.putExtra("company",
										mDetailDeliveryModel.getDLcompany());
								sign_intent.putExtra("from", "aotd");
								sign_intent.putExtra("Piece", sPeice);

								startActivity(sign_intent);
							}
						}
					} else {
						// download pdf async calling
						if (Utils.checkNetwork(DeliveryOrderInfoDelivery.this)) {
							pdfDownloadedView(pdfUrl);

						} else {

							Intent sign_intent = new Intent(
									DeliveryOrderInfoDelivery.this,
									SignatureActivity.class);
							sign_intent.putExtra("orderNumber", getIntent()
									.getStringExtra("orderNumber"));
							sign_intent.putExtra("roundtrip", getIntent()
									.getStringExtra("roundtrip"));
							sign_intent.putExtra("condition", getIntent()
									.getStringExtra("condition"));
							sign_intent.putExtra("deliveryType", deliveryType);
							sign_intent.putExtra("from", "aotd");
							sign_intent.putExtra("Piece", sPeice);
							startActivity(sign_intent);

						}

					}

				} else {
					Question.mListClass = classObject;

					String DLName_ = classObject.get(0).getDlcompany();
					String sPeice = classObject.get(0).getPeice();
					Question.setDLName(DLName_);
					Question.setPIECE(sPeice);

					Intent sign_intent = new Intent(
							DeliveryOrderInfoDelivery.this,
							SignatureActivity.class);
					sign_intent.putExtra("orderNumber", getIntent()
							.getStringExtra("orderNumber"));
					sign_intent.putExtra("roundtrip", getIntent()
							.getStringExtra("roundtrip"));
					sign_intent.putExtra("condition", getIntent()
							.getStringExtra("condition"));
					sign_intent.putExtra("deliveryType", deliveryType);
					sign_intent.putExtra("from", "aotd");
					sign_intent.putExtra("Piece", sPeice);
					startActivity(sign_intent);
					finish();

					/*
					 * OfflineDB db = new
					 * OfflineDB(DeliveryOrderInfoDelivery.this);
					 * db.addData("Picked up",Question.mListClass.get(0));
					 * 
					 * Intent in_pending = new
					 * Intent(DeliveryOrderInfoDelivery.this,
					 * PendingOrders.class); startActivity(in_pending);
					 * finish();
					 */
				}

			}
		});

		deliverBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Utils.checkNetwork(DeliveryOrderInfoDelivery.this)) {

					String DLName_ = mDetailDeliveryModel.getDLcompany();
					String sPeice = mDetailDeliveryModel.getPiece();
					Question.setDLName(DLName_);
					Question.setPIECE(sPeice);

					String orderNum = getIntent().getStringExtra("orderNumber");

					ArrayList<DetailDeliveryModel> aListClass = new ArrayList<DetailDeliveryModel>();
					Question.aListClass = aListClass;
					aListClass.add(mDetailDeliveryModel);
					Question.aListClass = aListClass;

					String pdfUrl = String.format(Utils.PDF_DOWNLOAD_URL,
							orderNum);
					String newFolder = "/AOTD";
					String extStorageDirectory = Environment
							.getExternalStorageDirectory().toString();
					File myNewFolder = new File(extStorageDirectory + newFolder);
					if (myNewFolder.exists()) {
						File file = new File(myNewFolder, getIntent()
								.getStringExtra("orderNumber") + ".pdf");
						if (file.exists()) {
							Intent sign_intent = new Intent(
									DeliveryOrderInfoDelivery.this,
									PdfSignatureActivity.class);
							sign_intent.putExtra("orderNumber", getIntent()
									.getStringExtra("orderNumber"));
							sign_intent.putExtra("roundtrip", getIntent()
									.getStringExtra("roundtrip"));
							sign_intent.putExtra("condition", getIntent()
									.getStringExtra("condition"));
							sign_intent.putExtra("deliveryType", deliveryType);
							sign_intent.putExtra("company",
									mDetailDeliveryModel.getDLcompany());
							sign_intent.putExtra("from", "aotd");
							sign_intent.putExtra("Piece", sPeice);
							startActivity(sign_intent);
						} else {
							// download pdf async calling
							if (Utils
									.checkNetwork(DeliveryOrderInfoDelivery.this)) {

								pdfDownloadedView(pdfUrl);
							} else {

								// alertDialog("AOTD","There is no active network, Please try again.");

								/* latest modification for offline mode */
								Log.i("",
										"kkk orderNumber"
												+ getIntent().getStringExtra(
														"orderNumber"));
								String DLName = mDetailDeliveryModel
										.getDLcompany();
								Question.setDLName(DLName);
								Intent sign_intent = new Intent(
										DeliveryOrderInfoDelivery.this,
										SignatureActivity.class);
								sign_intent.putExtra("orderNumber", getIntent()
										.getStringExtra("orderNumber"));
								sign_intent.putExtra("roundtrip", getIntent()
										.getStringExtra("roundtrip"));
								sign_intent.putExtra("condition", getIntent()
										.getStringExtra("condition"));
								sign_intent.putExtra("deliveryType",
										deliveryType);
								sign_intent.putExtra("company",
										mDetailDeliveryModel.getDLcompany());
								sign_intent.putExtra("from", "aotd");
								sign_intent.putExtra("Piece", sPeice);

								startActivity(sign_intent);
							}
						}
					} else {
						// download pdf async calling
						if (Utils.checkNetwork(DeliveryOrderInfoDelivery.this)) {
							pdfDownloadedView(pdfUrl);

						} else {

							Intent sign_intent = new Intent(
									DeliveryOrderInfoDelivery.this,
									SignatureActivity.class);
							sign_intent.putExtra("orderNumber", getIntent()
									.getStringExtra("orderNumber"));
							sign_intent.putExtra("roundtrip", getIntent()
									.getStringExtra("roundtrip"));
							sign_intent.putExtra("condition", getIntent()
									.getStringExtra("condition"));
							sign_intent.putExtra("deliveryType", deliveryType);
							sign_intent.putExtra("from", "aotd");
							sign_intent.putExtra("Piece", sPeice);
							startActivity(sign_intent);

						}

					}

				} else {
					Question.mListClass = classObject;

					String DLName_ = classObject.get(0).getDlcompany();
					String sPeice = classObject.get(0).getPeice();
					Question.setDLName(DLName_);
					Question.setPIECE(sPeice);

					Intent sign_intent = new Intent(
							DeliveryOrderInfoDelivery.this,
							SignatureActivity.class);
					sign_intent.putExtra("orderNumber", getIntent()
							.getStringExtra("orderNumber"));
					sign_intent.putExtra("roundtrip", getIntent()
							.getStringExtra("roundtrip"));
					sign_intent.putExtra("condition", getIntent()
							.getStringExtra("condition"));
					sign_intent.putExtra("deliveryType", deliveryType);
					sign_intent.putExtra("from", "aotd");
					sign_intent.putExtra("Piece", sPeice);
					startActivity(sign_intent);
					finish();

					/*
					 * OfflineDB db = new
					 * OfflineDB(DeliveryOrderInfoDelivery.this);
					 * db.addData("Picked up",Question.mListClass.get(0));
					 * 
					 * Intent in_pending = new
					 * Intent(DeliveryOrderInfoDelivery.this,
					 * PendingOrders.class); startActivity(in_pending);
					 * finish();
					 */
				}

			}
		});

		scanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent("com.vinscan.barcode.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);
			}
		});

		scanBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent("com.vinscan.barcode.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);
			}
		});

		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent in = new Intent(DeliveryOrderInfoDelivery.this,
						CameraActivity.class);
				in.putExtra("ORDERID", orderId);
				startActivity(in);

			}
		});

		imgOnline.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (isClicked) {
					if (Utils.NetworkType(DeliveryOrderInfoDelivery.this)
							.equalsIgnoreCase(Utils.wifi)) {
						Utils.wifiOFF(DeliveryOrderInfoDelivery.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					} else if (Utils
							.NetworkType(DeliveryOrderInfoDelivery.this)
							.equalsIgnoreCase(Utils.mobile)) {
						Utils.mobileDataOFF(DeliveryOrderInfoDelivery.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}

				} else {
					imgOnline.setBackgroundResource(R.drawable.online);
					isClicked = true;
					Utils.switchOnInternet(DeliveryOrderInfoDelivery.this);
				}

				return false;
			}
		});

	}

	@Override
	protected void onResume() {

		super.onResume();

		if (Utils.NetworkType(DeliveryOrderInfoDelivery.this).equalsIgnoreCase(
				Utils.wifi)) {
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		} else if (Utils.NetworkType(DeliveryOrderInfoDelivery.this)
				.equalsIgnoreCase(Utils.mobile)) {
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		} else {
			imgOnline.setBackgroundResource(R.drawable.offline);
			isClicked = false;
		}
	}

	protected void pdfDownloadedView(String pdfUrl) {
		if (Utils.checkNetwork(DeliveryOrderInfoDelivery.this)) {
			DownLoadPDFAsync downLoadPDFAsync = new DownLoadPDFAsync(
					DeliveryOrderInfoDelivery.this, new PdfHandler());

			downLoadPDFAsync.execute(pdfUrl,
					getIntent().getStringExtra("orderNumber"));

		}

	}

	private void intializeUI() {

		openorderStatus = getIntent().getExtras().getString("openorder");

		delivered = (Button) findViewById(R.id.aotd_delivery_delivery_btn);
		scanBtn = (Button) findViewById(R.id.aotd_delivery_scan_btn);
		orderNum = (TextView) findViewById(R.id.aotd_delivery_orderNum_btn);
		accountName = (TextView) findViewById(R.id.aotd_delivery_accountName_txt);
		accountNotes = (TextView) findViewById(R.id.aotd_delivery_accountNotes_txt);
		rfc = (TextView) findViewById(R.id.aotd_delivery_rfc_txt);
		pickUp_from = (TextView) findViewById(R.id.aotd_delivery_PUaddress_txt);
		delivery_to = (TextView) findViewById(R.id.aotd_delivery_DLaddress_txt);
		puinstruction = (TextView) findViewById(R.id.aotd_delivery_PUInstruction_txt);
		dlinstruction = (TextView) findViewById(R.id.aotd_delivery_DLInstruction_txt);
		serviceType = (TextView) findViewById(R.id.aotd_delivery_serviceType_txt);
		pickupReady = (TextView) findViewById(R.id.aotd_delivery_pickupReady_txt);
		requestedBy = (TextView) findViewById(R.id.aotd_delivery_requiredBy_txt);
		pieces = (TextView) findViewById(R.id.aotd_delivery_piece_txt);
		weight = (TextView) findViewById(R.id.aotd_delivery_weight_txt);
		roundTrip = (TextView) findViewById(R.id.aotd_roundtrip);
		adminNotes = (TextView) findViewById(R.id.aotd_delivery_adminNotes_txt);
		scanBottom = (Button) findViewById(R.id.aotd_scan_bottom);
		deliverBottom = (Button) findViewById(R.id.atod_delivered_bottom);
		btnCamera = (Button) findViewById(R.id.camImage);

		mDetailDeliveryModel = new DetailDeliveryModel();

		imgOnline = (ImageView) findViewById(R.id.aotd_img_mode);
		if (Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);

	}

	class DispatchDeliveryHandler extends Handler {
		public void handleMessage(android.os.Message msg) {
			if (progress != null)
				progress.dismiss();

			String errorMsg = msg.getData().getString("HttpError");
			if (errorMsg.length() > 0) {

				alertDialogWithMsg("AOTD", errorMsg);

			} else {

				/*
				 * ArrayList<DispatchAllListModel> al = new
				 * ArrayList<DispatchAllListModel>(); DispatchAllListModel dm =
				 * new DispatchAllListModel();
				 * dm.setAccountName(mDetailDeliveryModel.getAccountName());
				 * dm.setAccountnotes(mDetailDeliveryModel.getAccountnotes());
				 * dm.setCompany(mDetailDeliveryModel.getPUcompany());
				 */

				mDetailDeliveryModel = (DetailDeliveryModel) msg.getData()
						.getSerializable("dispatchdetail");
				if (mDetailDeliveryModel != null)
					setData();

			}
		}

	}

	@SuppressLint("HandlerLeak")
	public class PdfHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				// Toast.makeText(DeliveryOrderInfoDelivery.this,
				// "Error occured while downloading.",
				// Toast.LENGTH_LONG).show();

				Intent sign_intent = new Intent(DeliveryOrderInfoDelivery.this,
						SignatureActivity.class);
				sign_intent.putExtra("orderNumber",
						getIntent().getStringExtra("orderNumber"));
				sign_intent.putExtra("roundtrip",
						getIntent().getStringExtra("roundtrip"));
				sign_intent.putExtra("condition",
						getIntent().getStringExtra("condition"));
				sign_intent.putExtra("deliveryType", deliveryType);
				sign_intent.putExtra("from", "aotd");

				startActivity(sign_intent);

			} else {
				File file = new File(Environment.getExternalStorageDirectory()
						.toString(), "/AOTD/"
						+ getIntent().getStringExtra("orderNumber") + ".pdf");
				if (file.exists()) {
					// calling PdfSignatureActivity
					Intent sign_intent = new Intent(
							DeliveryOrderInfoDelivery.this,
							PdfSignatureActivity.class);
					sign_intent.putExtra("orderNumber", getIntent()
							.getStringExtra("orderNumber"));
					sign_intent.putExtra("roundtrip", getIntent()
							.getStringExtra("roundtrip"));
					sign_intent.putExtra("condition", getIntent()
							.getStringExtra("condition"));
					sign_intent.putExtra("deliveryType", deliveryType);
					sign_intent.putExtra("from", "aotd");
					startActivity(sign_intent);
				} else {
					Toast.makeText(DeliveryOrderInfoDelivery.this,
							"File not found.", Toast.LENGTH_LONG).show();
				}
			}
		}

	}

	public void setOfflineData(DispatchAllListModel mList) {

		orderNum.setText(getIntent().getStringExtra("orderNumber").trim());
		accountName.setText(mList.getAccountName().trim());
		rfc.setText("");

		final String sFrom = mList.getAddress() + " " + mList.getCity() + " "
				+ mList.getState() + " " + mList.getZip();

		final String sTo = mList.getDladdress() + " " + mList.getDlcity() + " "
				+ mList.getDlstate() + " " + mList.getDlzip();

		String dl_Company = mList.getDlcompany();
		String dl_Address = mList.getDladdress();
		String dl_Suite = mList.getDlsuit();
		String dl_City = mList.getDlcity();
		String dl_State = mList.getDlstate();
		String dl_Zip = mList.getDlzip();

		String pu_Company = mList.getCompany();
		String pu_Address = mList.getAddress();
		String pu_Suite = mList.getSuit();
		String pu_City = mList.getCity();
		String pu_State = mList.getState();
		String pu_Zip = mList.getZip();

		TextView tv = (TextView) findViewById(R.id.showMap);

		TextView tvDLCOmpany = (TextView) findViewById(R.id.DL_Company);
		tvDLCOmpany.setText("Company: " + dl_Company);
		TextView tvDLAddress = (TextView) findViewById(R.id.DL_Address);
		tvDLAddress.setText("Address: " + dl_Address);
		TextView tvDlSite = (TextView) findViewById(R.id.DL_Suite);
		tvDlSite.setText("Suite: " + dl_Suite);
		TextView tvDLCity = (TextView) findViewById(R.id.DL_City);
		tvDLCity.setText("City: " + dl_City);
		TextView tvDLState = (TextView) findViewById(R.id.DL_State);
		tvDLState.setText("State: " + dl_State);
		TextView tvDLZip = (TextView) findViewById(R.id.DL_Zip);
		tvDLZip.setText("Zip: " + dl_Zip);

		TextView tvPUCOmpany = (TextView) findViewById(R.id.PU_Company);
		tvPUCOmpany.setText("Company: " + pu_Company);
		TextView tvPUAddress = (TextView) findViewById(R.id.PU_Address);
		tvPUAddress.setText("Address: " + pu_Address);
		TextView tvPUSite = (TextView) findViewById(R.id.PU_Suite);
		tvPUSite.setText("Suite: " + pu_Suite);
		TextView tvPUCity = (TextView) findViewById(R.id.PU_City);
		tvPUCity.setText("City: " + pu_City);
		TextView tvPUState = (TextView) findViewById(R.id.PU_State);
		tvPUState.setText("State: " + pu_State);
		TextView tvPUZip = (TextView) findViewById(R.id.PU_Zip);
		tvPUZip.setText("Zip: " + pu_Zip);

		final TextView tvPh1 = (TextView) findViewById(R.id.phone1);
		final TextView tvPh2 = (TextView) findViewById(R.id.phone2);

		final TextView tvFPh1 = (TextView) findViewById(R.id.Fphone1);
		final TextView tvFPh2 = (TextView) findViewById(R.id.Fphone2);

		tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		if (mList.getDlCellPhone().length() > 0) {
			tvPh1.setText(mList.getDlCellPhone());
		}

		if (mList.getDlHomePhone().length() > 0) {
			tvPh2.setText(mList.getDlHomePhone());
		}

		if (mList.getPuCellPhone().length() > 0) {
			tvFPh1.setText(mList.getPuCellPhone());
		}

		if (mList.getPuHomephone().length() > 0) {
			tvFPh2.setText(mList.getPuHomephone());
		}

		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent in = new Intent(DeliveryOrderInfoDelivery.this,
						MapView.class);
				in.putExtra("FROM", sFrom);
				in.putExtra("TO", sTo);
				in.putExtra("ACTIVITY", "Info");

				startActivity(in);

			}
		});

		/*
		 * final TextView tvPh1 = (TextView)findViewById(R.id.phone1); final
		 * TextView tvPh2 = (TextView)findViewById(R.id.phone2);
		 * 
		 * final TextView tvFPh1 = (TextView)findViewById(R.id.Fphone1); final
		 * TextView tvFPh2 = (TextView)findViewById(R.id.Fphone2);
		 */

		tvPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		if (mList.getDlCellPhone().length() > 0) {
			tvPh1.setText(mList.getDlCellPhone());
		}

		if (mList.getDlHomePhone().length() > 0) {
			tvPh2.setText(mList.getDlHomePhone());
		}

		if (mList.getPuCellPhone().length() > 0) {
			tvFPh1.setText(mList.getPuCellPhone());
		}

		if (mList.getPuHomephone().length() > 0) {
			tvFPh2.setText(mList.getPuHomephone());
		}

		tvPh1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvPh1.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setDLPhoneCall("Phone Call made to "
												+ tvPh1.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvPh1.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		tvPh2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvPh2.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setDLHomeCall("Phone Call made to "
												+ tvPh2.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvPh2.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		tvFPh1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvFPh1.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setPUPhoneCall("Phone Call made to "
												+ tvFPh1.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvFPh1.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		tvFPh2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvFPh2.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setPUHomeCall("Phone Call made to "
												+ tvFPh2.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvFPh2.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		serviceType.setText(mList.getHour().trim());
		pickupReady.setText(mList.getRDDate().trim());
		requestedBy.setText("");
		pieces.setText(mList.getPeice().trim());
		weight.setText(mList.getWeight().trim());

		/*
		 * if(mList.get.length()>1)
		 * puinstruction.setText(mDetailDeliveryModel.getPUInstruction
		 * ().trim()); else puinstruction.setText(" Not Provided");
		 * 
		 * if(mDetailDeliveryModel.getDLInstruction().length()>1)
		 * dlinstruction.setText
		 * (mDetailDeliveryModel.getDLInstruction().trim()); else
		 * dlinstruction.setText(" Not Provided");
		 */

		/*
		 * if(mList.getAccountName().length()>1)
		 * accountNotes.setText(mDetailDeliveryModel.getAccountnotes().trim());
		 * else accountNotes.setText(" This is Test Notes");
		 */

		/*
		 * if(mDetailDeliveryModel.getAdminNotes().length()>1)
		 * adminNotes.setText(mDetailDeliveryModel.getAdminNotes().trim()); else
		 * adminNotes.setText(" This is Test Notes");
		 */

		/*
		 * if (mList.getIsRoundTrip().trim().equalsIgnoreCase("0"))
		 * isRoundtrip.setText("No"); else isRoundtrip.setText("Yes");
		 */

	}

	public void setData() {
		orderNum.setText(getIntent().getStringExtra("orderNumber").trim());
		accountName.setText(mDetailDeliveryModel.getAccountName().trim());
		rfc.setText(mDetailDeliveryModel.getRef().trim());

		String dl_Company = mDetailDeliveryModel.getDLcompany();
		String dl_Address = mDetailDeliveryModel.getDLaddress();
		String dl_Suite = mDetailDeliveryModel.getDLsuit();
		String dl_City = mDetailDeliveryModel.getDLcity();
		String dl_State = mDetailDeliveryModel.getDLstate();
		String dl_Zip = mDetailDeliveryModel.getDLzip();

		String pu_Company = mDetailDeliveryModel.getPUcompany();
		String pu_Address = mDetailDeliveryModel.getPUaddress();
		String pu_Suite = mDetailDeliveryModel.getPUsuit();
		String pu_City = mDetailDeliveryModel.getPUcity();
		String pu_State = mDetailDeliveryModel.getPUstate();
		String pu_Zip = mDetailDeliveryModel.getPUzip();

		serviceType.setText(mDetailDeliveryModel.getServiceName().trim());
		pickupReady.setText(mDetailDeliveryModel.getRDDate().trim());
		requestedBy.setText(mDetailDeliveryModel.getRequestor().trim());
		pieces.setText(mDetailDeliveryModel.getPiece().trim());
		weight.setText(mDetailDeliveryModel.getWeight().trim());

		final String sFrom = mDetailDeliveryModel.getPUaddress() + " "
				+ mDetailDeliveryModel.getPUcity() + " "
				+ mDetailDeliveryModel.getPUstate() + " "
				+ mDetailDeliveryModel.getPUzip();

		final String sTo = mDetailDeliveryModel.getDLaddress() + " "
				+ mDetailDeliveryModel.getDLcity() + " "
				+ mDetailDeliveryModel.getDLstate() + " "
				+ mDetailDeliveryModel.getDLzip();

		TextView tv = (TextView) findViewById(R.id.showMap);

		TextView tvDLCOmpany = (TextView) findViewById(R.id.DL_Company);
		tvDLCOmpany.setText("Company: " + dl_Company);
		TextView tvDLAddress = (TextView) findViewById(R.id.DL_Address);
		tvDLAddress.setText("Address: " + dl_Address);
		TextView tvDlSite = (TextView) findViewById(R.id.DL_Suite);
		tvDlSite.setText("Suite: " + dl_Suite);
		TextView tvDLCity = (TextView) findViewById(R.id.DL_City);
		tvDLCity.setText("City: " + dl_City);
		TextView tvDLState = (TextView) findViewById(R.id.DL_State);
		tvDLState.setText("State: " + dl_State);
		TextView tvDLZip = (TextView) findViewById(R.id.DL_Zip);
		tvDLZip.setText("Zip: " + dl_Zip);

		TextView tvPUCOmpany = (TextView) findViewById(R.id.PU_Company);
		tvPUCOmpany.setText("Company: " + pu_Company);
		TextView tvPUAddress = (TextView) findViewById(R.id.PU_Address);
		tvPUAddress.setText("Address: " + pu_Address);
		TextView tvPUSite = (TextView) findViewById(R.id.PU_Suite);
		tvPUSite.setText("Suite: " + pu_Suite);
		TextView tvPUCity = (TextView) findViewById(R.id.PU_City);
		tvPUCity.setText("City: " + pu_City);
		TextView tvPUState = (TextView) findViewById(R.id.PU_State);
		tvPUState.setText("State: " + pu_State);
		TextView tvPUZip = (TextView) findViewById(R.id.PU_Zip);
		tvPUZip.setText("Zip: " + pu_Zip);

		final TextView tvPh1 = (TextView) findViewById(R.id.phone1);
		final TextView tvPh2 = (TextView) findViewById(R.id.phone2);

		final TextView tvFPh1 = (TextView) findViewById(R.id.Fphone1);
		final TextView tvFPh2 = (TextView) findViewById(R.id.Fphone2);

		tvPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		if (mDetailDeliveryModel.getDLcellPhone().length() > 0) {
			tvPh1.setText(mDetailDeliveryModel.getDLcellPhone());
		}

		if (mDetailDeliveryModel.getDLhomephone().length() > 0) {
			tvPh2.setText(mDetailDeliveryModel.getDLhomephone());
		}

		if (mDetailDeliveryModel.getPUcellPhone().length() > 0) {
			tvFPh1.setText(mDetailDeliveryModel.getPUcellPhone());
		}

		if (mDetailDeliveryModel.getPUhomephone().length() > 0) {
			tvFPh2.setText(mDetailDeliveryModel.getPUhomephone());
		}

		tvPh1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvPh1.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setDLPhoneCall("Phone Call made to "
												+ tvPh1.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvPh1.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		tvPh2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvPh2.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setDLHomeCall("Phone Call made to "
												+ tvPh2.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvPh2.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		tvFPh1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvFPh1.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setPUPhoneCall("Phone Call made to "
												+ tvFPh1.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvFPh1.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		tvFPh2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(DeliveryOrderInfoDelivery.this)
						.setTitle("Phone Call")
						.setCancelable(false)
						.setMessage(
								"Are you sure you want to make call to "
										+ tvFPh2.getText().toString() + " ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss a");
										String currentDateandTime = sdf
												.format(new Date());

										Question.setPUHomeCall("Phone Call made to "
												+ tvFPh2.getText().toString()
												+ " at " + currentDateandTime);
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ tvFPh2.getText().toString()));
										startActivity(intent);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								})

						.show();

			}
		});

		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent in = new Intent(DeliveryOrderInfoDelivery.this,
						MapView.class);
				in.putExtra("FROM", sFrom);
				in.putExtra("TO", sTo);
				in.putExtra("ACTIVITY", "Info");

				startActivity(in);

			}
		});

		if (mDetailDeliveryModel.getPUInstruction().length() > 1)
			puinstruction.setText(mDetailDeliveryModel.getPUInstruction()
					.trim());
		else
			puinstruction.setText(" Not Provided");

		if (mDetailDeliveryModel.getDLInstruction().length() > 1)
			dlinstruction.setText(mDetailDeliveryModel.getDLInstruction()
					.trim());
		else
			dlinstruction.setText(" Not Provided");

		if (mDetailDeliveryModel.getAccountnotes().length() > 1)
			accountNotes.setText(mDetailDeliveryModel.getAccountnotes().trim());
		else
			accountNotes.setText(" This is Test Notes");

		if (mDetailDeliveryModel.getAdminNotes().length() > 1)
			adminNotes.setText(mDetailDeliveryModel.getAdminNotes().trim());
		else
			adminNotes.setText(" This is Test Notes");

		if (mDetailDeliveryModel.getRoundTrip().trim().equalsIgnoreCase("0"))
			roundTrip.setText("No");
		else
			roundTrip.setText("Yes");

	}

	public void alertDialogWithMsg(String title, String msg) {

		new AlertDialogMsg(DeliveryOrderInfoDelivery.this, title, msg)
				.setPositiveButton("ok",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();

							}

						}).create().show();
	}

	public void alertDialogWithOneMsg(String title, String msg) {

		new AlertDialogMsg(DeliveryOrderInfoDelivery.this, title, msg)
				.setPositiveButton("ok",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}

						}).create().show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 0) {

			if (resultCode == RESULT_OK) {

				String contents = intent.getStringExtra("batchid");

				try {

					Log.d("contents", "contents " + contents);

					if (contents.equalsIgnoreCase("none")) {

						alertDialogWithOneMsg("AOTD",
								"Wrong Order Id Scan Please try again.");

					} else {

						char subId = contents.substring(contents.length() - 1)
								.toCharArray()[0];

						String scanOrderId = null;

						if (subId >= '0' && subId <= '9') {

							scanOrderId = contents;

						} else {

							scanOrderId = contents.substring(0,
									contents.length() - 1);
						}

						if (scanOrderId.trim().equals(
								getIntent().getStringExtra("orderNumber")
										.trim())) {

							delivered.performClick();

						} else {

							alertDialogWithOneMsg("AOTD",
									"Wrong Order Id Scan Please try again.");

						}

					}

				} catch (Exception e) {

					e.printStackTrace();

				}

				// Toast.makeText(this,format+" "+contents,
				// Toast.LENGTH_LONG).show();

			} else if (resultCode == RESULT_CANCELED) {

				Log.d("Cancle", "scanning cancle");
			}
		}
	}

	public void alertDialog(String title, String msg) {

		new AlertDialogMsg(DeliveryOrderInfoDelivery.this, title, msg)
				.setPositiveButton("ok",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}

						}).create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "Show Navigation").setIcon(R.drawable.menu_map_icon);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);

		String FromCity = mDetailDeliveryModel.getPUcity();
		String FromAdd = mDetailDeliveryModel.getPUaddress();
		String FromState = mDetailDeliveryModel.getPUstate();
		String FromZip = mDetailDeliveryModel.getPUzip();

		String ToCity = mDetailDeliveryModel.getDLcity();
		String ToAdd = mDetailDeliveryModel.getDLaddress();
		String ToState = mDetailDeliveryModel.getDLstate();
		String ToZip = mDetailDeliveryModel.getDLzip();

		String FromAddress = FromAdd + " " + FromCity + ", " + FromState + " "
				+ FromZip;
		String ToAddress = ToAdd + " " + ToCity + ", " + ToState + " " + ToZip;

		try {
			if (Utils.checkNetwork(getApplicationContext())) {
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

				if (locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					Geocoder coder = new Geocoder(getApplicationContext());
					ArrayList<Address> adressesFrom = (ArrayList<Address>) coder
							.getFromLocationName(FromAddress, 50);
					ArrayList<Address> adressesTo = (ArrayList<Address>) coder
							.getFromLocationName(ToAddress, 50);

					double latFrom = 0.0;
					double lonFrom = 0.0;

					double latTo = 0.0;
					double lonTo = 0.0;

					for (Address add : adressesFrom) {
						lonFrom = add.getLongitude();
						latFrom = add.getLatitude();
					}

					for (Address add1 : adressesTo) {
						lonTo = add1.getLongitude();
						latTo = add1.getLatitude();
					}

					String uri = String
							.format(Locale.ENGLISH,
									"http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
									latFrom, lonFrom, FromAddress, latTo,
									lonTo, ToAddress);
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(uri));
					intent.setClassName("com.google.android.apps.maps",
							"com.google.android.maps.MapsActivity");
					startActivity(intent);

				} else {
					showGPSDisabledAlertToUser();
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"Please Check for Internet Connection",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
		}
		return false;
	}

	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

}