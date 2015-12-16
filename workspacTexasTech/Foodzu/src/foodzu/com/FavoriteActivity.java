package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;
import foodzu.com.models.Products;

public class FavoriteActivity extends Activity {

	private ListView myfavoritelist;
	SharedPreferences sharedpreferences;
	public static final String MyLogin = "Login";
	private String user_id;

	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;

	private ArrayList<String> productIdArray = new ArrayList<String>();
	private String formatedproductIdArray;

	public static ArrayList<Products> Cart_Arraylist;
	private Products item_of_product, item;
	private ArrayList<Products> favList_Array = new ArrayList<Products>();
	private FavoriteAdapter FL_Adapter;

	private TextView no_favorite;

	private String favorite_product_id;
	static double item_total_cost = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		no_favorite = (TextView) findViewById(R.id.nofavoritesTxt);
		myfavoritelist = (ListView) findViewById(R.id.myfavorite_list);
		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		alertDialogBuilder = new AlertDialog.Builder(FavoriteActivity.this);
		user_id = sharedpreferences.getString("user_id", "");
		Cart_Arraylist = new ArrayList<Products>();
		favList_Array = new ArrayList<Products>();
		if (user_id != null)
			fav_product();

		// myfavoritelist.setOnItemClickListener(this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	void fav_product() {
		if (IsInternetPresent()) {
			new getFavProductsTask().execute();
		} else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									fav_product();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	public class getFavProductsTask extends AsyncTask<Void, Void, String> {

		Dialog dialog;
		String Main = "Y";

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(FavoriteActivity.this);
			dialog.setCancelable(false);
			if (dialog.isShowing())
				dialog.dismiss();
			dialog.show();
		}

		protected String doInBackground(Void... params) {

			InputStream inputStream = null;
			String result = null;
			String URL = URLs.FAV_ITEM_URL+user_id;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = httpclient
						.execute(new HttpGet(URL));
				inputStream = httpResponse.getEntity().getContent();
				if (inputStream != null)
					result = convertInputStreamToString(inputStream);
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;

		}

		protected void onPostExecute(String result) {

			// if (Products_Arraylist != null)
			// Products_Arraylist.clear();
			// if (Main.equals("Y"))
			 parsejson(result);
			// else if (Main.equals("N"))
			// parsesubjson(result);
			//
			 if (favList_Array != null)
			  FL_Adapter = new FavoriteAdapter(FavoriteActivity.this,
			 favList_Array);
			// if (Products_Arraylist.size() == 0) {
			// noproduct_icon.setVisibility(View.VISIBLE);
			// homeproductlist.setVisibility(View.GONE);
			// } else
			 myfavoritelist.setAdapter(FL_Adapter);
			//
			// if (check.equals("SHOW")) {
			// navigation_bar.setVisibility(View.VISIBLE);
			// maincat_name.setText(menu_name);
			// subcat_name.setText(submenu_name);
			// if (Cart_Arraylist.size() > 0)
			// buttombarAction(Cart_Arraylist, item_total_cost);
			// } else {
			// navigation_bar.setVisibility(View.GONE);
			// if (Cart_Arraylist.size() > 0)
			// buttombarAction(Cart_Arraylist, item_total_cost);
			// }
			dialog.dismiss();
		}
	}

	ArrayList<Products> parsejson(String homeproducts) {
		try {
			if (favList_Array != null)
				favList_Array.clear();
			if (homeproducts != null && homeproducts.length() > 0) {

				JSONObject jObj_main = new JSONObject(homeproducts);
				if (!jObj_main.getString("success").equals("0")) {
					JSONArray result = jObj_main.getJSONArray("result");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							JSONObject cat = result.getJSONObject(i);
							int total = cat.getInt("total_product");
							if (total > 0) {
								JSONArray product = cat
										.getJSONArray("products");
								if (product.length() > 0) {
									for (int j = 0; j < product.length(); j++) {
										item_of_product = new Products();
										JSONObject items = product
												.getJSONObject(j);

										// -------------------------------------item
										// 1-----------------------------------------
										if (!items.getString("item_name")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_1")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name(items.getString(
													"item_name").toString());
											item.setactual_price(items
													.getString("product_mrp_1")
													.toString());
											item.setfinal_price(items
													.getString(
															"product_actual_price_1")
													.toString());
											item.setvaliddiscount(items
													.getString(
															"product_offer_1")
													.toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_wieght(items.getString(
													"product_wt_1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setitem_in_cart("NO");
											item.setitem_qty_count(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_0() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_0()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_0()
																	.getitem_qty_count() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_0()
																			.getitem_in_cart()
																			.equals("YES")) {
																item.setitem_qty_count(Cart_Arraylist
																		.get(c)
																		.getprod_0()
																		.getitem_qty_count());
																item.setitem_in_cart("YES");
															} else {
																item.setitem_qty_count(0);
																item.setitem_in_cart("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count(0);
												item.setitem_in_cart("NO");
											}

											item_of_product
													.setlocation_specific(items
															.getString(
																	"islocspecific")
															.toString());
											item_of_product
													.setlocation_availability(items
															.getString(
																	"loc_available")
															.toString());
											item_of_product
													.setlocation_address(items
															.getString(
																	"loc_specificvalue")
															.toString());
											item_of_product.setprod_0(item);
											item_of_product.setisProduct("Y");

										} else
											item_of_product.setisProduct("N");

										// ---------------------------------------item
										// 2--------------------------------------------------------------
										if (!items.getString("item_name1")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_2")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_1(items
													.getString("item_name1")
													.toString());
											item.setactual_price1(items
													.getString("product_mrp_2")
													.toString());
											item.setfinal_price1(items
													.getString(
															"product_actual_price_2")
													.toString());
											item.setvaliddiscount1(items
													.getString(
															"product_offer_2")
													.toString());
											item.setpd_wieght1(items.getString(
													"product_wt_2").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct1("Y");
											item.setitem_in_cart1("NO");
											item.setitem_qty_count1(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_1() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_1()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_1()
																	.getitem_qty_count1() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_1()
																			.getitem_in_cart1()
																			.equals("YES")) {
																item.setitem_qty_count1(Cart_Arraylist
																		.get(c)
																		.getprod_1()
																		.getitem_qty_count1());
																item.setitem_in_cart1("YES");
															} else {
																item.setitem_qty_count1(0);
																item.setitem_in_cart1("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count1(0);
												item.setitem_in_cart1("NO");
											}

											item_of_product.setprod_1(item);
											item_of_product.setisProduct1("Y");
										} else
											item_of_product.setisProduct1("N");
										// ---------------------------------------item
										// 3--------------------------------------------------------------
										if (!items.getString("item_name2")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_3")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_2(items
													.getString("item_name2")
													.toString());
											item.setactual_price2(items
													.getString("product_mrp_3")
													.toString());
											item.setfinal_price2(items
													.getString(
															"product_actual_price_3")
													.toString());
											item.setvaliddiscount2(items
													.getString(
															"product_offer_3")
													.toString());
											item.setpd_wieght2(items.getString(
													"product_wt_3").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct2("Y");
											item.setitem_in_cart2("NO");
											item.setitem_qty_count2(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_2() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_2()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_2()
																	.getitem_qty_count2() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_2()
																			.getitem_in_cart2()
																			.equals("YES")) {
																item.setitem_qty_count2(Cart_Arraylist
																		.get(c)
																		.getprod_2()
																		.getitem_qty_count2());
																item.setitem_in_cart2("YES");
															} else {
																item.setitem_qty_count2(0);
																item.setitem_in_cart2("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count2(0);
												item.setitem_in_cart2("NO");
											}

											item_of_product.setprod_2(item);
											item_of_product.setisProduct2("Y");
										} else
											item_of_product.setisProduct2("N");
										// ---------------------------------------item
										// 4--------------------------------------------------------------
										if (!items.getString("item_name3")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_4")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_3(items
													.getString("item_name3")
													.toString());
											item.setactual_price3(items
													.getString("product_mrp_4")
													.toString());
											item.setfinal_price3(items
													.getString(
															"product_actual_price_4")
													.toString());
											item.setvaliddiscount3(items
													.getString(
															"product_offer_4")
													.toString());
											item.setpd_wieght3(items.getString(
													"product_wt_4").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct3("Y");
											item.setitem_in_cart3("NO");
											item.setitem_qty_count3(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_3() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_3()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_3()
																	.getitem_qty_count3() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_3()
																			.getitem_in_cart3()
																			.equals("YES")) {
																item.setitem_qty_count3(Cart_Arraylist
																		.get(c)
																		.getprod_3()
																		.getitem_qty_count3());
																item.setitem_in_cart3("YES");
															} else {
																item.setitem_qty_count3(0);
																item.setitem_in_cart3("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count3(0);
												item.setitem_in_cart3("NO");
											}

											item_of_product.setprod_3(item);
											item_of_product.setisProduct3("Y");
										} else
											item_of_product.setisProduct3("N");
										// ---------------------------------------item
										// 5--------------------------------------------------------------
										if (!items.getString("item_name4")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_5")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_4(items
													.getString("item_name4")
													.toString());
											item.setactual_price4(items
													.getString("product_mrp_5")
													.toString());
											item.setfinal_price4(items
													.getString(
															"product_actual_price_5")
													.toString());
											item.setvaliddiscount4(items
													.getString(
															"product_offer_5")
													.toString());
											item.setpd_wieght4(items.getString(
													"product_wt_5").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct4("Y");
											item.setitem_in_cart4("NO");
											item.setitem_qty_count4(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_4() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_4()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_4()
																	.getitem_qty_count4() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_4()
																			.getitem_in_cart4()
																			.equals("YES")) {
																item.setitem_qty_count4(Cart_Arraylist
																		.get(c)
																		.getprod_4()
																		.getitem_qty_count4());
																item.setitem_in_cart4("YES");
															} else {
																item.setitem_qty_count4(0);
																item.setitem_in_cart4("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count4(0);
												item.setitem_in_cart4("NO");
											}

											item_of_product.setprod_4(item);
											item_of_product.setisProduct4("Y");
										} else
											item_of_product.setisProduct4("N");
										// ---------------------------------------------end-----------------------------------------------------

										favList_Array.add(item_of_product);
									}// End of for-loop
								}
							}// total products count
						}
					}
				}
			}
			return favList_Array;
		} catch (JSONException e) {
			System.out.println(e);
		}
		return null;
	}

	// Favorite Adapter
	public class FavoriteAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Products> data;
		private LayoutInflater inflater = null;

		SharedPreferences sharedPreferences, pref;
		private ImageView imgThumbnail, plus, closeit, minus;
		private TextView prod_name, prod_cost, prod_offer_cost, prod_desc,
				prod_quantity, count, tab1, tab2, tab3, tab4, tab5;
		JSONObject promoJson;
		HashMap<String, String> item = new HashMap<String, String>();
		private Context mContext;
		int value = 0;
		String control = "";
		Dialog dialog;

		public FavoriteAdapter(Activity a, ArrayList<Products> d) {
			activity = a;
			data = d;
			this.mContext = a;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			pref = PreferenceManager.getDefaultSharedPreferences(activity);

		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			private ImageView imgThumbnail, plus, minus, more;
			private TextView lblDateEnd, tvmrp, tvoffcost, tvProdName,
					prod_qty, count;
			RelativeLayout view, tick_me;
			// ImageLoader imgLoader;
		}

		public View getView(final int p, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.adapter_home, parent,
						false);
				holder = new ViewHolder();
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.view = (RelativeLayout) convertView
					.findViewById(R.id.wrap_rl);
			holder.tick_me = (RelativeLayout) convertView
					.findViewById(R.id.lytTitle);
			holder.lblDateEnd = (TextView) convertView
					.findViewById(R.id.lblEndDate);
			holder.tvProdName = (TextView) convertView
					.findViewById(R.id.lblstore);
			holder.prod_qty = (TextView) convertView.findViewById(R.id.p_qty);
			holder.imgThumbnail = (ImageView) convertView
					.findViewById(R.id.imgThumbnail);
			holder.plus = (ImageView) convertView.findViewById(R.id.plus);
			holder.minus = (ImageView) convertView.findViewById(R.id.minus);
			holder.more = (ImageView) convertView.findViewById(R.id.more);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.tvmrp = (TextView) convertView.findViewById(R.id.lblAddress);
			holder.tvoffcost = (TextView) convertView
					.findViewById(R.id.prod_off_cost);

			if (data.get(p).getisProduct1().equals("Y"))
				holder.more.setVisibility(View.VISIBLE);
			else
				holder.more.setVisibility(View.GONE);

			holder.plus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String GO = "N";
					if (data.get(p).getlocation_specific().equals("1")) {
						if (data.get(p).getlocation_availability().equals("1")) {
							GO = "Y";
						} else if (data.get(p).getlocation_availability()
								.equals("2")) {
							String add = Utils.ADD;
							if (add.matches("(.*)india(.*)"))
								GO = "Y";
							else
								GO = "N";
							// india
						} else if (data.get(p).getlocation_availability()
								.equals("3")) {
							// location specific
							String ad = Utils.ADD;
							String keyword[] = data.get(p)
									.getlocation_address().split("@#");
							System.out.println(data.get(p)
									.getlocation_address());
							for (int i = 0; i < keyword.length; i++) {
								String sub_key[] = keyword[i].toString().split(
										",");
								if (sub_key.length == 3) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())) {
										GO = "Y";
									}
								}
								if (sub_key.length == 4) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())
											&& ad.replaceAll("[0-9]+", "")
													.contains(
															sub_key[1]
																	.toString())) {
										GO = "Y";
									}
								}
							}

						} else {
							alertDialogBuilder
									.setMessage(
											"Item not available for the Shipping region!")
									.setCancelable(false)
									.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													alertDialog.dismiss();
												}
											});
							alertDialog = alertDialogBuilder.create();
							alertDialog
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialog.show();
						}
					} else
						GO = "Y";

					if (GO.equals("N")) {
						alertDialogBuilder
								.setMessage(
										"Item not available for the Shipping region!")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												alertDialog.dismiss();
											}
										});
						alertDialog = alertDialogBuilder.create();
						alertDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						alertDialog.show();
					} else {

						value = data.get(p).getprod_0().getitem_qty_count();
						if (value >= 0) {
							value = value + 1;
							data.get(p).getprod_0().setitem_qty_count(value);

							if (data.get(p).getprod_0().getitem_in_cart()
									.equals("NO")) {
								data.get(p).getprod_0().setcontrol("S");
								data.get(p).getprod_0().setitem_in_cart("YES");
								Cart_Arraylist.add(data.get(p));
							}
							if (Cart_Arraylist.size() == 0)
								item_total_cost = 0;
							item_total_cost = item_total_cost
									+ Double.parseDouble(data.get(p)
											.getprod_0().getfinal_price());

							for (int i = 0; i < Cart_Arraylist.size(); i++) {
								if (Cart_Arraylist.get(i).getprod_0() != null) {
									if (Cart_Arraylist.get(i).getprod_0()
											.getitem_in_cart().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_0().getcontrol()
													.equals("D")) {
										data.get(i).getprod_0().setcontrol("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_0()
												.getitem_id()
												.equals(data.get(p).getprod_0()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_0()
													.setitem_qty_count(value);
										}
									}
								}

								if (Cart_Arraylist.get(i).getprod_1() != null) {
									if (Cart_Arraylist.get(i).getprod_1()
											.getitem_in_cart1().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_1().getcontrol1()
													.equals("D")) {
										data.get(i).getprod_1()
												.setcontrol1("S");
										if (Cart_Arraylist
												.get(i)
												.getprod_1()
												.getitem_id()
												.equals(data.get(p).getprod_1()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_1()
													.setitem_qty_count1(value);
										}
									}
								}
								if (Cart_Arraylist.get(i).getprod_2() != null) {
									if (Cart_Arraylist.get(i).getprod_2()
											.getitem_in_cart2().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_2().getcontrol2()
													.equals("D")) {
										data.get(i).getprod_2()
												.setcontrol2("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_2()
												.getitem_id()
												.equals(data.get(p).getprod_2()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_2()
													.setitem_qty_count2(value);
										}
									}
								}
								if (Cart_Arraylist.get(i).getprod_3() != null) {
									if (Cart_Arraylist.get(i).getprod_3()
											.getitem_in_cart3().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_3().getcontrol3()
													.equals("D")) {
										data.get(i).getprod_3()
												.setcontrol3("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_3()
												.getitem_id()
												.equals(data.get(p).getprod_3()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_3()
													.setitem_qty_count3(value);
										}
									}
								}
								if (Cart_Arraylist.get(i).getprod_4() != null) {
									if (Cart_Arraylist.get(i).getprod_4()
											.getitem_in_cart4().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_4().getcontrol4()
													.equals("D")) {
										data.get(i).getprod_4()
												.setcontrol4("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_4()
												.getitem_id()
												.equals(data.get(p).getprod_4()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_4()
													.setitem_qty_count4(value);
										}
									}
								}

							}
							notifyDataSetChanged();
							((HomeActivity) mContext).buttombarAction(
									Cart_Arraylist, item_total_cost);
						}
					}
				}
			});

			holder.minus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					int cnt_val = 0;
					for (int m = 0; m < Cart_Arraylist.size(); m++) {
						if (Cart_Arraylist.get(m).getprod_0().getitem_id()
								.equals(data.get(p).getprod_0().getitem_id()))
							cnt_val = m;
					}
					value = data.get(p).getprod_0().getitem_qty_count();
					if (value > 0) {

						value = value - 1;
						data.get(p).getprod_0().setitem_qty_count(value);
						Cart_Arraylist.get(cnt_val).getprod_0()
								.setitem_qty_count(value);
						if (Cart_Arraylist.size() == 0)
							item_total_cost = 0;
						item_total_cost = item_total_cost
								- Double.parseDouble(data.get(p).getprod_0()
										.getfinal_price());

						if (value == 0) {
							if (data.get(p).getprod_0().getitem_in_cart()
									.equals("YES")) {
								data.get(p).getprod_0().setitem_in_cart("NO");
								Cart_Arraylist.remove(Cart_Arraylist
										.get(cnt_val));
							}
						}
						for (int i = 0; i < Cart_Arraylist.size(); i++) {
							if (Cart_Arraylist.get(i).getprod_0() != null) {
								if (Cart_Arraylist.get(i).getprod_0()
										.getitem_in_cart().equals("YES")
										&& Cart_Arraylist.get(i).getprod_0()
												.getcontrol().equals("D"))
									Cart_Arraylist.get(i).getprod_0()
											.setcontrol("S");
							}
							if (Cart_Arraylist.get(i).getprod_1() != null) {
								if (Cart_Arraylist.get(i).getprod_1()
										.getitem_in_cart1().equals("YES")
										&& Cart_Arraylist.get(i).getprod_1()
												.getcontrol1().equals("D"))
									Cart_Arraylist.get(i).getprod_1()
											.setcontrol1("S");
							}
							if (Cart_Arraylist.get(i).getprod_2() != null) {
								if (Cart_Arraylist.get(i).getprod_2()
										.getitem_in_cart2().equals("YES")
										&& Cart_Arraylist.get(i).getprod_2()
												.getcontrol2().equals("D"))
									Cart_Arraylist.get(i).getprod_2()
											.setcontrol2("S");
							}
							if (Cart_Arraylist.get(i).getprod_3() != null) {
								if (Cart_Arraylist.get(i).getprod_3()
										.getitem_in_cart3().equals("YES")
										&& Cart_Arraylist.get(i).getprod_3()
												.getcontrol3().equals("D"))
									Cart_Arraylist.get(i).getprod_3()
											.setcontrol3("S");
							}
							if (Cart_Arraylist.get(i).getprod_4() != null) {
								if (Cart_Arraylist.get(i).getprod_4()
										.getitem_in_cart4().equals("YES")
										&& Cart_Arraylist.get(i).getprod_4()
												.getcontrol4().equals("D"))
									Cart_Arraylist.get(i).getprod_4()
											.setcontrol4("S");
							}
						}
						notifyDataSetChanged();
						((HomeActivity) mContext).buttombarAction(
								Cart_Arraylist, item_total_cost);
					}

				}
			});

			holder.view.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					dialog = new Dialog(
							FavoriteActivity.this,
							android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
					dialog.setContentView(R.layout.view_product_details);

					tab1 = (TextView) dialog.findViewById(R.id.product_wt1);
					tab2 = (TextView) dialog.findViewById(R.id.product_wt2);
					tab3 = (TextView) dialog.findViewById(R.id.product_wt3);
					tab4 = (TextView) dialog.findViewById(R.id.product_wt4);
					tab5 = (TextView) dialog.findViewById(R.id.product_wt5);

					prod_name = (TextView) dialog.findViewById(R.id.prod_name);
					prod_cost = (TextView) dialog.findViewById(R.id.prod_cost);
					prod_offer_cost = (TextView) dialog
							.findViewById(R.id.prod_offer_cost);
					prod_desc = (TextView) dialog
							.findViewById(R.id.desc_details);
					prod_quantity = (TextView) dialog
							.findViewById(R.id.prod_quantity);
					imgThumbnail = (ImageView) dialog
							.findViewById(R.id.imgThumbnail);

					closeit = (ImageView) dialog.findViewById(R.id.closeit);
					plus = (ImageView) dialog.findViewById(R.id.plus);
					minus = (ImageView) dialog.findViewById(R.id.minus);
					count = (TextView) dialog.findViewById(R.id.count);
					data.get(p).setcontrol("TAB1");
					control = "TAB1";

					prod_name.setText(data.get(p).getprod_0().getitem_name());
					prod_offer_cost.setText("\u20B9  "
							+ data.get(p).getprod_0().getfinal_price());
					if (!data.get(p).getprod_0().getvaliddiscount().equals("1"))
						prod_cost.setVisibility(View.INVISIBLE);
					prod_cost.setText("\u20B9  "
							+ data.get(p).getprod_0().getactual_price());
					prod_cost.setPaintFlags(prod_cost.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
					prod_desc.setText(data.get(p).getprod_0()
							.getpd_short_description());
					prod_quantity.setText(data.get(p).getprod_0()
							.getpd_wieght());
					count.setText(Integer.toString(data.get(p).getprod_0()
							.getitem_qty_count()));
					System.out.println(Integer.toString(data.get(p).getprod_0()
							.getitem_qty_count()));

					closeit.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					if (data.get(p).getisProduct1().equals("Y")) {
						tab1.setVisibility(View.VISIBLE);
						tab2.setVisibility(View.VISIBLE);

						if (data.get(p).getisProduct2().equals("Y")) {
							tab3.setVisibility(View.VISIBLE);
							tab3.setText(data.get(p).getprod_2()
									.getpd_wieght2().toString());
							if (data.get(p).getisProduct3().equals("Y")) {
								tab4.setVisibility(View.VISIBLE);
								tab4.setText(data.get(p).getprod_3()
										.getpd_wieght3().toString());
								if (data.get(p).getisProduct4().equals("Y")) {
									tab5.setVisibility(View.VISIBLE);
									tab5.setText(data.get(p).getprod_4()
											.getpd_wieght4().toString());
								} else
									tab5.setVisibility(View.GONE);
							} else
								tab4.setVisibility(View.GONE);
						} else
							tab3.setVisibility(View.GONE);

						tab1.setText(data.get(p).getprod_0().getpd_wieght()
								.toString());
						tab2.setText(data.get(p).getprod_1().getpd_wieght1()
								.toString());

						tab1.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.green_bg_round));
						tab1.setTextColor(getResources().getColor(color.white));

					} else {
						tab1.setVisibility(View.GONE);
						tab2.setVisibility(View.GONE);
					}

					tab1.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab1.setTextColor(getResources().getColor(
									color.white));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_0()
									.getitem_name());
							prod_offer_cost.setText("\u20B9  "
									+ data.get(p).getprod_0().getfinal_price());
							if (!data.get(p).getprod_0().getvaliddiscount()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_0()
													.getactual_price());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_0()
									.getpd_wieght());
							count.setText(Integer.toString(data.get(p)
									.getprod_0().getitem_qty_count()));
							control = "TAB1";
							data.get(p).setcontrol("TAB1");
						}
					});

					tab2.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab2.setTextColor(getResources().getColor(
									color.white));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_1()
									.getitem_name_1());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_1()
													.getfinal_price1());
							if (!data.get(p).getprod_1().getvaliddiscount1()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_1()
											.getactual_price1());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_1()
									.getpd_wieght1());
							count.setText(Integer.toString(data.get(p)
									.getprod_1().getitem_qty_count1()));
							control = "TAB2";
							data.get(p).setcontrol("TAB2");
						}
					});

					tab3.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab3.setTextColor(getResources().getColor(
									color.white));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_2()
									.getitem_name_2());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_2()
													.getfinal_price2());
							if (!data.get(p).getprod_2().getvaliddiscount2()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_2()
											.getactual_price2());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_2()
									.getpd_wieght2());
							count.setText(Integer.toString(data.get(p)
									.getprod_2().getitem_qty_count2()));
							control = "TAB3";
							data.get(p).setcontrol("TAB3");
						}
					});

					tab4.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab4.setTextColor(getResources().getColor(
									color.white));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_3()
									.getitem_name_3());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_3()
													.getfinal_price3());
							if (!data.get(p).getprod_3().getvaliddiscount3()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_3()
											.getactual_price3());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_3()
									.getpd_wieght3());
							count.setText(Integer.toString(data.get(p)
									.getprod_3().getitem_qty_count3()));
							control = "TAB4";
							data.get(p).setcontrol("TAB4");
						}
					});

					tab5.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab5.setTextColor(getResources().getColor(
									color.white));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_4()
									.getitem_name_4());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_4()
													.getfinal_price4());
							if (!data.get(p).getprod_4().getvaliddiscount4()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_4()
											.getactual_price4());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_4()
									.getpd_wieght4());
							count.setText(Integer.toString(data.get(p)
									.getprod_4().getitem_qty_count4()));
							control = "TAB5";
							data.get(p).setcontrol("TAB5");
						}
					});

					plus.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							if (control.equals("TAB2")) {
								value = data.get(p).getprod_1()
										.getitem_qty_count1();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_1()
											.setitem_qty_count1(value);
									if (data.get(p).getprod_1()
											.getitem_in_cart1().equals("NO")) {
										data.get(p).getprod_1()
												.setitem_in_cart1("YES");
										data.get(p).getprod_1()
												.setcontrol1("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_1()
													.getfinal_price1());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_1() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_1()
													.getitem_in_cart1()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_1()
															.getcontrol1()
															.equals("D"))
												data.get(i).getprod_1()
														.setcontrol1("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}

							} else if (control.equals("TAB3")) {

								value = data.get(p).getprod_2()
										.getitem_qty_count2();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_2()
											.setitem_qty_count2(value);
									if (data.get(p).getprod_2()
											.getitem_in_cart2().equals("NO")) {
										data.get(p).getprod_2()
												.setitem_in_cart2("YES");
										data.get(p).getprod_2()
												.setcontrol2("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_2()
													.getfinal_price2());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_2() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_2()
													.getitem_in_cart2()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_2()
															.getcontrol2()
															.equals("D"))
												data.get(i).getprod_2()
														.setcontrol2("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							} else if (control.equals("TAB4")) {
								value = data.get(p).getprod_3()
										.getitem_qty_count3();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_3()
											.setitem_qty_count3(value);
									if (data.get(p).getprod_3()
											.getitem_in_cart3().equals("NO")) {
										data.get(p).getprod_3()
												.setitem_in_cart3("YES");
										data.get(p).getprod_3()
												.setcontrol3("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_3()
													.getfinal_price3());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_3() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_3()
													.getitem_in_cart3()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_3()
															.getcontrol3()
															.equals("D"))
												data.get(i).getprod_3()
														.setcontrol3("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							} else if (control.equals("TAB5")) {
								value = data.get(p).getprod_4()
										.getitem_qty_count4();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_4()
											.setitem_qty_count4(value);
									if (data.get(p).getprod_4()
											.getitem_in_cart4().equals("NO")) {
										data.get(p).getprod_4()
												.setitem_in_cart4("YES");
										data.get(p).getprod_4()
												.setcontrol4("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_4()
													.getfinal_price4());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_4() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_4()
													.getitem_in_cart4()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_4()
															.getcontrol4()
															.equals("D"))
												data.get(i).getprod_4()
														.setcontrol4("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							} else if (control.equals("TAB1")
									|| control.equals("")) {
								value = data.get(p).getprod_0()
										.getitem_qty_count();
								if (value >= 0) {

									value = value + 1;
									data.get(p).getprod_0()
											.setitem_qty_count(value);
									if (data.get(p).getprod_0()
											.getitem_in_cart().equals("NO")) {

										data.get(p).getprod_0().setcontrol("S");
										data.get(p).getprod_0()
												.setitem_in_cart("YES");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_0()
													.getfinal_price());
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_0()
												.getcontrol() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_0()
													.getitem_in_cart()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_0()
															.getcontrol()
															.equals("D"))
												data.get(i).getprod_0()
														.setcontrol("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							}
						}
					});

					minus.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							if (control.equals("TAB2")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_1() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_1()
												.getitem_id()
												.equals(data.get(p).getprod_1()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_1()
										.getitem_qty_count1();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_1()
											.setitem_qty_count1(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count1(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_1()
													.getfinal_price1());
									if (value == 0) {
										if (data.get(p).getprod_1()
												.getitem_in_cart1()
												.equals("YES")) {
											data.get(p).getprod_1()
													.setitem_in_cart1("NO");

											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_1() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_1()
													.getitem_in_cart1()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_1()
															.getcontrol1()
															.equals("D"))
												data.get(i).getprod_1()
														.setcontrol1("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB3")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_2() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_2()
												.getitem_id()
												.equals(data.get(p).getprod_2()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_2()
										.getitem_qty_count2();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_2()
											.setitem_qty_count2(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count2(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_2()
													.getfinal_price2());
									if (value == 0) {
										if (data.get(p).getprod_2()
												.getitem_in_cart2()
												.equals("YES")) {
											data.get(p).getprod_2()
													.setitem_in_cart2("NO");
											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_2() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_2()
													.getitem_in_cart2()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_2()
															.getcontrol2()
															.equals("D"))
												data.get(i).getprod_2()
														.setcontrol2("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB4")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_3() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_3()
												.getitem_id()
												.equals(data.get(p).getprod_3()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_3()
										.getitem_qty_count3();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_3()
											.setitem_qty_count3(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count3(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_3()
													.getfinal_price3());
									if (value == 0) {
										if (data.get(p).getprod_3()
												.getitem_in_cart3()
												.equals("YES")) {
											data.get(p).getprod_3()
													.setitem_in_cart3("NO");
											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_3() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_3()
													.getitem_in_cart3()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_3()
															.getcontrol3()
															.equals("D"))
												data.get(i).getprod_3()
														.setcontrol3("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB5")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_4() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_4()
												.getitem_id()
												.equals(data.get(p).getprod_4()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_4()
										.getitem_qty_count4();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_4()
											.setitem_qty_count4(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count4(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_4()
													.getfinal_price4());
									if (value == 0) {
										if (data.get(p).getprod_4()
												.getitem_in_cart4()
												.equals("YES")) {
											data.get(p).getprod_4()
													.setitem_in_cart4("NO");
											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_4() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_4()
													.getitem_in_cart4()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_4()
															.getcontrol4()
															.equals("D"))
												data.get(i).getprod_4()
														.setcontrol4("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB1")
									|| control.equals("")) {

								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist
											.get(m)
											.getprod_0()
											.getitem_id()
											.equals(data.get(p).getprod_0()
													.getitem_id()))
										cnt_val = m;
								}
								value = data.get(p).getprod_0()
										.getitem_qty_count();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_0()
											.setitem_qty_count(value);
									Cart_Arraylist.get(cnt_val).getprod_0()
											.setitem_qty_count(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_0()
													.getfinal_price());
									if (value == 0) {
										if (data.get(p).getprod_0()
												.getitem_in_cart()
												.equals("YES")) {
											data.get(p).getprod_0()
													.setitem_in_cart("NO");

											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));

									notifyDataSetChanged();
								}
							}
						}
					});

					if (data.get(p).getcontrol() != null && count != null) {
						if (data.get(p).getcontrol().equals("TAB2")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count1()));

						} else if (data.get(p).getcontrol().equals("TAB3")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count2()));
						} else if (data.get(p).getcontrol().equals("TAB4")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count3()));
						} else if (data.get(p).getcontrol().equals("TAB5")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count4()));
						} else if (data.get(p).getcontrol().equals("TAB1")) {
							count.setText(Integer.toString(data.get(p)
									.getprod_0().getitem_qty_count()));
						}
					}

					if (data.get(p).getprod_0().getitem_image() != null
							&& !data.get(p).getprod_0().getitem_image()
									.equals(""))
						Picasso.with(FavoriteActivity.this)
								.load(data.get(p).getprod_0().getitem_image())
								.fit().centerInside()
								.tag(FavoriteActivity.this).into(imgThumbnail);

					String GO = "N";
					if (data.get(p).getlocation_specific().equals("1")) {
						if (data.get(p).getlocation_availability().equals("1")) {
							GO = "Y";
						} else if (data.get(p).getlocation_availability()
								.equals("2")) {
							String add = Utils.ADD;
							if (add.matches("(.*)india(.*)"))
								GO = "Y";
							else
								GO = "N";
							// india
						} else if (data.get(p).getlocation_availability()
								.equals("3")) {
							// location specific
							String ad = Utils.ADD;
							String keyword[] = data.get(p)
									.getlocation_address().split("@#");
							System.out.println(data.get(p)
									.getlocation_address());
							for (int i = 0; i < keyword.length; i++) {
								String sub_key[] = keyword[i].toString().split(
										",");
								if (sub_key.length == 3) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())) {
										GO = "Y";
									}
								}
								if (sub_key.length == 4) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())
											&& ad.replaceAll("[0-9]+", "")
													.contains(
															sub_key[1]
																	.toString())) {
										GO = "Y";
									}
								}
							}

						} else {
							alertDialogBuilder
									.setMessage(
											"Item not available for the Shipping region!")
									.setCancelable(false)
									.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													alertDialog.dismiss();
												}
											});
							alertDialog = alertDialogBuilder.create();
							alertDialog
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialog.show();
						}
					} else
						GO = "Y";

					if (GO.equals("N")) {
						alertDialogBuilder
								.setMessage(
										"Item not available for the Shipping region!")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												alertDialog.dismiss();
											}
										});
						alertDialog = alertDialogBuilder.create();
						alertDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						alertDialog.show();
					} else {

						dialog.show();
					}

				}
			});

			if (data.get(p).getisProduct().equals("Y"))
				if (data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct1().equals("Y"))
				if (data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct2().equals("Y"))
				if (data.get(p).getprod_2().getitem_qty_count2() == 0
						&& data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct3().equals("Y"))
				if (data.get(p).getprod_3().getitem_qty_count3() == 0
						&& data.get(p).getprod_2().getitem_qty_count2() == 0
						&& data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct4().equals("Y"))
				if (data.get(p).getprod_4().getitem_qty_count4() == 0
						&& data.get(p).getprod_3().getitem_qty_count3() == 0
						&& data.get(p).getprod_2().getitem_qty_count2() == 0
						&& data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);

			holder.prod_qty.setText(data.get(p).getprod_0().getpd_wieght());
			holder.tvProdName.setText(data.get(p).getprod_0().getitem_name());
			holder.tvoffcost.setText("\u20B9  "
					+ data.get(p).getprod_0().getfinal_price());

			if (!data.get(p).getprod_0().getvaliddiscount().equals("1"))
				holder.tvmrp.setVisibility(View.INVISIBLE);
			else
				holder.tvmrp.setVisibility(View.VISIBLE);
			holder.tvmrp.setText("\u20B9  "
					+ data.get(p).getprod_0().getactual_price());
			holder.tvmrp.setPaintFlags(holder.tvmrp.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			holder.count.setText(Integer.toString(data.get(p).getprod_0()
					.getitem_qty_count()));
			if (data.get(p).getprod_0().getitem_image() == null
					|| data.get(p).getprod_0().getitem_image().equals(""))
				holder.imgThumbnail.setImageResource(R.drawable.no_image);
			else
				Picasso.with(activity)
						.load(data.get(p).getprod_0().getitem_image()).fit()
						.centerCrop().tag(activity).into(holder.imgThumbnail);

			return convertView;
		}
	}

	public boolean IsInternetPresent() {
		if (new Utils(FavoriteActivity.this).isNetworkAvailable())
			return true;
		else
			return false;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

}
