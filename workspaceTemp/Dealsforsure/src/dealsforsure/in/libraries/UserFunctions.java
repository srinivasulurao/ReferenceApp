package dealsforsure.in.libraries;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import dealsforsure.in.model.Address;
import dealsforsure.in.model.Deals;
import dealsforsure.in.model.Store;
import dealsforsure.in.model.SuggestionKeyword;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserFunctions {

	private JSONParser jsonParser;

	// Web Service
	private final String Server = "http://www.dealsforsure.in/live/";

	// Folder
	private final String folderMain = "yourfolder/";
	private final String folderApi = "api/";
	public final String folderAdmin = "webadmin/";

	// Url
	private final String URLApi = Server + folderApi;
	public final String URLAdmin = Server;

	// Service
	//private final String service_latest_deals = "latest_deals?";
	
	private final String service_latest_deals = "deal_around_you?";

	private final String service_gcm = "register_id_gcm?";
	private final String service_category_list = "category_list?";
	private final String service_deal_category_list = "category_list_deal?";
	private final String service_deal_by_category = "deal_by_category?";
	private final String service_deal_detail = "deal_detail?";
	private final String service_deal_by_search = "deal_by_search_name?";
	private final String service_deal_around_you = "deal_around_you?";
	private final String service_currency = "currency";
	public final String service_view_deal = "view-deal.php?";
	private final String service_desc = "description.php?";
	private final String service_register = "register_function?";
	private final String service_login = "login_user?";
	private final String service_savestore = "add_store?";
	private final String service_getstorelist = "store_detail_user?";
	private final String service_savedeal = "add_deal?";
	private final String service_getdeallist = "latest_deals?";
	private final String service_getpromocode = "add_promocode?";
	private final String service_getmydeals = "promo_detail?";
	private final String service_getpoints = "user_points?";
	private final String service_verifypromo = "promocode_verify?";
	private final String service_confirmpromo = "apply_promo?";
	private final String service_addcomments = "add_review?";
	private final String service_reviewlist = "review_list?";
	private final String service_analyticdeal = "get_analytic_by_cid?";
	private final String service_dealstatus = "deal_status?";
	private final String service_otpverification = "sms_verification?";
	private final String service_storestatus = "store_status?";
	private final String service_saveaddress = "update_address_user?";
	private final String service_getaddress = "get_user_address?";
	private final String service_saveorder = "create_order?";
	private final String service_getcustomerlist = "get_customer_list?";
	private final String service_getcustomerdetaillist = "get_customer_detail?";
	private final String service_getsearchdeals = "search_deals?";
	private final String service_getsuggestionkeyword = "search_keyword_suggestion?";
	private final String service_getrecharege = "recharge.php?";
	private final String service_getorderlist = "get_order_list?";
	// Param
    private final String param_start_index = "start_index=";
	private final String param_items_per_page = "items_per_page=";
	private final String param_category_id = "category_id=";
	private final String param_deal_id = "deal_id=";
	private final String param_user_lat = "user_lat=";
	private final String param_user_lng = "user_lng=";
	private final String param_keyword = "keyword=";
	private final String param_register_id = "register_id=";
	private final String param_unique_id = "unique_device_id=";
	private final String param_c_id = "cid=";
	private final String param_merchant_id = "merchant_user_id=";
	private final String param_promo_code = "promo_code=";
	private final String param_name = "name=";
	private final String param_email = "email=";
	private final String param_phone = "phone=";
	private final String param_device = "device_id=";
	private final String param_passward = "password=";
	private final String param_type = "type=";
	private final String param_pid = "pid=";
	private final String param_listtype = "list_type=";
	private final String page = "page=";
	private final String param_otpcode = "code=";
	private final String param_vid = "vid=";
	private final String param_tokenKey = "token_key=";
	private final String param_tableid = "table_id=";
	private final String param_id = "id=";
	private final String param_recharge_amount = "recharge_amount=";
	private final String param_recharge_type = "recharge_type=";
	private final String param_oid = "oid=";
	
	private final String param_bssname = "bss_name=";
	private final String param_bssaddress = "bss_address=";
	private final String param_bssphone = "bss_phone=";
	private final String param_bsswebsite = "bss_website=";
	private final String param_bssfburl = "bss_fb_url=";
	
	private final String param_comments = "comments=";
	private final String param_reviewcount = "review_count=";
	
	private final String param_storename = "store_name=";
	private final String param_address = "address=";
	private final String param_placeid = "place_id=";
	private final String param_categoryid = "category_id=";
	private final String param_userid = "uid=";
	private final String param_userCode = "user_code=";
	private final String param_validDate = "valid_upto_date=";
	private final String param_merchant_userid = "merchant_user_id=";
	private final String param_status = "status=";
	private final String param_dealname = "title=";
	private final String param_description = "description=";
	private final String param_startdate = "start_date=";
	private final String param_storeId ="sid=";
	private final String param_enddate = "end_date=";
	private final String param_offerdetails = "discount_type=";
	private final String param_locality = "locality=";
	private final String param_map_address = "google_map_address=";
	private final String param_dayrestrict = "day_restrict=";
	private final String param_dealtype = "deal_type=";
	private final String param_qty = "qty=";
	private final String param_price = "price=";
	private final String param_address2 = "address2=";
	private final String param_address1 = "address1=";
	private final String param_pincode = "pincode=";
	private final String param_city = "city=";
	private final String param_state = "state=";
	private final String param_addressid = "add_id=";
	private final String param_amount = "amount=";
	private final String param_specialdeal = "special_deal=";
	private final String param_customerid = "customer_id=";

	
	
	
	//private final String param_discountprice = "after_disc_value=";

	// Key object name to get value
	public final String key_deals_id = "deal_id";
	public final String key_c_id = "cid";
	public final String key_merchant_id = "merchant_user_id";
	public final String key_deals_title = "title";
	public final String key_deals_date_start = "start_date";
	public final String key_deals_date_end = "end_date";
	public final String key_deals_after_disc_value = "after_discount_value";
	
	public final String key_deals_Offer_detail = "discount_type";
	public final String key_deals_start_value = "start_value";
	public final String key_deals_image = "image";
	public final String key_deals_image2 = "image2";
	public final String key_deals_image3 = "image3";
	public final String key_deals_image4 = "image4";
	public final String key_image_count = "image_count";
	
	public final String key_deals_company = "company";
	public final String key_deals_address = "address";
	public final String key_deals_lat = "latitude";
	public final String key_deals_lng = "longitude";
	public final String key_deals_url = "deal_url";
	public final String key_deals_disc = "discount";
	public final String key_deals_save = "save_value";
	public final String key_deals_desc = "description";
	public final String key_category_id = "category_id";
	public final String key_category_name = "category_name";
	public final String key_category_marker = "category_marker";
	public final String key_currency_code = "code";
	public final String key_status = "status";
	public final String key_total_data = "totalData";
	public final String key_valid_date = "valid_upto_date";
	public final String key_deal_point = "points_for_deal";
	public final String key_offer_detail = "discount_type";
	public final String key_deal_count= "deal_count";
	public final String key_address = "address";
	public final String key_latitudet = "latitude";
	public final String key_longitude = "longitude";
	public final String key_commentsCount = "comments_count";
	public final String key_starCount = "review_count";
	public final String key_store_name = "store_name";
	public final String key_view_count = "viewed_it_count";
	public final String key_get_count = "get_it_count";
	public final String key_buy_count = "buy_it_count";
	public final String key_day_restrict = "day_restrict";
	public final String key_description = "description";
	public final String key_dealtype = "deal_type"; 
	public final String key_price = "price"; 
	
	// Array
	public final String array_latest_deals = "result";
	public final String array_category_list = "categoryList";
	public final String array_deal_detail = "result";
	public final String array_place_by_search = "dealBySearchName";
	public final String array_around_you = "dealAroundYou";
	public final String array_deal_by_category = "dealByCategory";
	public final String array_currency = "currency";
	public final String array_register_id = "registerID";

	// LoadUrl
	public final String varLoadURL = Server + folderMain + service_desc
			+ param_deal_id;
	private String webService;
	public final int valueItemsPerPage = 6;

	// Google project id
	public static final String SENDER_ID = "630942458162";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */

	// http://your-website/your-folder/api/register_id_gcm?register_id=1234567890&unique_device_id=123456789
	public JSONObject registerIdGcm(String register_id, String unique_id) {
		webService = URLApi + service_gcm + param_register_id + register_id
				+ "&" + param_unique_id + unique_id;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject latestDeals(int valueStartIndex,String userLat,String userLong,String deviceId,String userId) {
		webService = URLApi + service_latest_deals + param_start_index
				+ valueStartIndex + "&" + param_items_per_page
				+ valueItemsPerPage+"&"+param_user_lat
				+ userLat+"&"+param_user_lng
				+ userLong+"&"+param_device
				+ deviceId+"&"+ param_userid+ userId;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject searchDeals(int valueStartIndex,String userLat,String userLong,String deviceId,String tokenKey,SuggestionKeyword suggestionKeyword ) {
		webService = URLApi + service_getsearchdeals + param_start_index
				+ valueStartIndex + "&" + param_items_per_page
				+ valueItemsPerPage+"&"+param_user_lat
				+ userLat+"&"+param_user_lng
				+ userLong+"&"+param_device
				+ deviceId+"&"+param_tableid
				+ suggestionKeyword.getTableId()+"&"+param_id
				+ suggestionKeyword.getId()+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject login(String email, String passward, String type) {

		webService = URLApi + service_login + param_email + email + "&"
				+ param_passward + passward + "&" + param_type + type;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getPoints(String userId,String deviceId,String tokenKey) {

		webService = URLApi + service_getpoints + param_userid+ userId+"&"+param_tokenKey + tokenKey +"&"+param_device + deviceId ;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getMyDealDetail(String userId,String type,String tokenKey,String deviceId) {

		webService = URLApi + service_getmydeals + param_userid + userId+ "&"
				+ param_listtype+ type+"&"+param_tokenKey + tokenKey+"&"+param_device + deviceId;  ;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getOrderList(String userId,String tokenKey,String deviceId) {

		webService = URLApi + service_getorderlist + param_userid + userId+ "&" +param_tokenKey + tokenKey+"&"+param_device + deviceId;  ;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getReviewList(String cid,String deviceId,String tokenKey) {

		webService = URLApi + service_reviewlist + param_c_id + cid+"&"+param_tokenKey + tokenKey +"&"+param_device + deviceId ;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	public JSONObject getStoreList(String UserId,
			String deviceId,String tokenKey) {

		webService = URLApi + service_getstorelist + param_userid+ UserId+"&"+param_device + deviceId+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getsuggestionList(String suggestion,
			String deviceId,String tokenKey) {

		webService = URLApi + service_getsuggestionkeyword + param_keyword+ suggestion+"&"+param_device + deviceId+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getDealList(String storeId,String deviceId,String tokenKey) {

		webService = URLApi + service_getdeallist + param_storeId+ storeId +"&"+param_device + deviceId
				+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getCustomerList(String userId,String deviceId,String tokenKey) {

		webService = URLApi + service_getcustomerlist + param_merchant_userid+ userId +"&"+param_device + deviceId
				+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject getCustomerDetailList(String userId,String customerId,String deviceId,String tokenKey) {

		webService = URLApi + service_getcustomerdetaillist + param_merchant_userid+ userId +"&"+param_userid +customerId+"&"+param_device + deviceId
				+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}
	
	public JSONObject saveComments(String cid,String userId,String comments,String starRate,String tokenKey,String deviceId) {

		webService = URLApi + service_addcomments + param_c_id+ cid+ "&" + param_userid
				+ userId + "&" + param_comments
				+ comments + "&" + param_reviewcount
				+ starRate +"&"+param_tokenKey + tokenKey+"&"+param_device + deviceId; ;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;

	}

	public JSONObject saveStore(Store store,String deviceId,String tokenKey) {
		JSONObject json = null;
		try{
		String convertaddress = URLEncoder.encode(store.getStoreAddress(),
				"UTF-8");
		
		
			webService = URLApi + service_savestore + param_storename
					+ store.getStoreName() + "&" + param_map_address
					+ convertaddress + "&" + param_placeid + store.getPlaceId()
					+ "&" + param_userid + store.getUserId() + "&"
					+ param_categoryid + store.getCategoryId() + "&"
					+ param_address + store.getAddress() + "&" + param_locality
					+ store.getLocality() + "&" + param_device + deviceId + "&"
					+ param_tokenKey + tokenKey;
		
		
		
			
		//webService = URLApi + service_getstorelist + param_userid+ store.getUserId() ;
		 json = jsonParser.getJSONFromUrl(webService);
		}catch(Exception e){
			System.out.println();
			
			
			
		}


		return json;

	}
	public JSONObject saveAddress(Address address,String deviceId,String tokenKey) {
		JSONObject json = null;
		try{
			
			webService = URLApi + service_saveaddress + param_name
					+ address.getFullName() + "&" + param_address1
					+ address.getAddress1() + "&" + param_address2 + address.getAddress2()
					+ "&" + param_phone + address.getPhoneNumber() + "&" + param_userid + address.getUserId() + "&"
					+ param_pincode + address.getPinCode() + "&"
					+ param_city + address.getCity() + "&" + param_state
					+ address.getState() + "&" + param_device + deviceId + "&"
					+ param_tokenKey + tokenKey;
		
		 json = jsonParser.getJSONFromUrl(webService);
			
		}catch(Exception e){
			System.out.println();
			
			
			
		}


		return json;
		}
			
	public JSONObject getAddress(String userId,String deviceId,String tokenKey) {
		JSONObject json = null;
		try{
			
			webService = URLApi + service_getaddress + param_userid + userId + "&" + param_device + deviceId + "&"
					+ param_tokenKey + tokenKey;
		
		 json = jsonParser.getJSONFromUrl(webService);
			
		}catch(Exception e){
			System.out.println();
			
			
			
		}


		return json;
		}
	
	public JSONObject saveOrders(String cId, String addressId, String qty,
			String amount, String userId, String deviceId, String tokenKey) {
		JSONObject json = null;
		try {

			webService = URLApi + service_saveorder + param_c_id + cId + "&"
					+ param_addressid + addressId + "&" + param_qty + qty + "&"
					+ param_amount + amount + "&" + param_userid + userId + "&"
					+ param_device + deviceId + "&" + param_tokenKey + tokenKey;

			json = jsonParser.getJSONFromUrl(webService);

		} catch (Exception e) {
			System.out.println();

		}

		return json;
	}
	
	
	public JSONObject saveDeal(Deals deals,String deviceId,String tokenKey) {
		JSONObject json = null;
		try{
		
			/* SharedPreferences prefs = cns.getSharedPreferences("StoreID", Context.MODE_PRIVATE); 
			 String storeid = prefs.getString("sid", "");*/
			
			webService = URLApi + service_savedeal + param_dealname
					+ deals.getDealsName() + "&" + param_description
					+ deals.getDescription() + "&" + param_startdate
					+ deals.getStartDate() + "&" + param_enddate
					+ deals.getEndDate() + "&" + param_categoryid
					+ deals.getCategoryId() + "&" + param_offerdetails
					+ deals.getOfferDetail() + "&" + param_storeId
					+ deals.getStoreId() + "&" + param_userid
					+ deals.getUserId()+ "&" + param_dayrestrict
					+ deals.getDayRestrict()+"&" + param_dealtype
					+ deals.getType()+"&" + param_price
					+ deals.getPrice()+"&" + param_qty
					+ deals.getQuantity()+"&"+param_tokenKey + tokenKey+"&"+param_device + deviceId;
			
			Log.d("nowww",webService.toString());
		
		 json = jsonParser.getJSONFromUrl(webService);
		 
		 Log.d("nowww",json.toString());
		 
		 JSONObject storeObject;

			String status = json.getString("status");

			if (status.equals("200")) {
				
			String dealid=json.getString("deal_id");
		 	 
		 
			String result = null;
			InputStream is = null;
			HttpURLConnection conn = null;
			DataOutputStream dos = null;
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String imageBase64 = null;
			String boundary = "*****";
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;
			String response;
			String converttitle = null;
			File file = null;
			
			for(int i=1 ;i<=deals.getImageCount();i++){
		 
					if (i == 1) {
						file = new File(deals.getImagePath());
					} else if (i == 2) {
						file = new File(deals.getImagePath2());
					} else if (i == 3) {
						file = new File(deals.getImagePath3());
					} else if (i == 4) {
						file = new File(deals.getImagePath4());
					}

			if (file.exists()) {

				try {
					int serverResponseCode = 0;
					// open a URL connection to the Servlet
					FileInputStream fileInputStream = new FileInputStream(
							file);
					URL url = new URL(URLApi+"upload_deal_image?deal_id="+ dealid+"&indicator="+i+"&"+param_tokenKey + tokenKey+"&"+param_device + deviceId);

					// Open a HTTP connection to the URL
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoInput(true); // Allow InputsFF
					conn.setDoOutput(true); // Allow Outputs
					conn.setUseCaches(false); // Don't use a
												// Cached
												// Copy
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("ENCTYPE",
							"multipart/form-data");
					conn.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					conn.setRequestProperty("uploaded_file",
							file.toString());

					dos = new DataOutputStream(conn.getOutputStream());

					dos.writeBytes(twoHyphens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
							+ file + "\"" + lineEnd);

					dos.writeBytes(lineEnd);

					// create a buffer of maximum size
					bytesAvailable = fileInputStream.available();

					bufferSize = Math
							.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];

					// read file and write it into form...
					bytesRead = fileInputStream.read(buffer, 0,
							bufferSize);

					while (bytesRead > 0) {

						dos.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable,
								maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0,
								bufferSize);

					}

					// send multipart form data necesssary after
					// file data...
					dos.writeBytes(lineEnd);
					dos.writeBytes(twoHyphens + boundary + twoHyphens
							+ lineEnd);

					// Responses from the server (code and
					// message)
					serverResponseCode = conn.getResponseCode();
					String serverResponseMessage = conn
							.getResponseMessage();

					Log.i("uploadFile", "HTTP Response is : "
							+ serverResponseMessage + ": "
							+ serverResponseCode);

					BufferedReader in = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));

					Log.d("BuffrerReader", "" + in);

					if (in != null) {

						response = convertStreamToString(in);
						Log.e("FINAL_RESPONSE-LENGTH",
								"" + response.length());
						Log.e("FINAL_RESPONSE", response);
					}

					fileInputStream.close();
					dos.flush();
					dos.close();

				} catch (MalformedURLException ex) {

					ex.printStackTrace();

					Log.e("Upload file to server",
							"error: " + ex.getMessage(), ex);
				} catch (Exception e) {

					e.printStackTrace();

					Log.e("Upload file to server Exception",
							"Exception : " + e.getMessage(), e);
				}

			}
			}
			}
		}catch(Exception e){
			System.out.println(e);
			
		}


		return json;

	}
	
	
	public JSONObject saveSpecialDeal(Deals deals,String deviceId,String tokenKey) {
		JSONObject json = null;
		try{
		
			/* SharedPreferences prefs = cns.getSharedPreferences("StoreID", Context.MODE_PRIVATE); 
			 String storeid = prefs.getString("sid", "");*/
			
			webService = URLApi + service_savedeal + param_dealname
					+ deals.getDealsName() + "&" + param_description
					+ deals.getDescription() + "&" + param_startdate
					+ deals.getStartDate() + "&" + param_enddate
					+ deals.getEndDate() + "&" + param_categoryid
					+ deals.getCategoryId() + "&" + param_offerdetails
					+ deals.getOfferDetail() + "&" + param_storeId
					+ deals.getStoreId() + "&" + param_userid
					+ deals.getUserId()+ "&" + param_dayrestrict
					+ deals.getDayRestrict()+"&" + param_dealtype
					+ deals.getType()+"&" + param_price
					+ deals.getPrice()+"&" + param_qty
					+ deals.getPrice()+"&" + param_specialdeal
					+ deals.getSpecialDeal()+"&" + param_customerid
					+ deals.getCustomerId()+"&"+param_tokenKey + tokenKey+"&"+param_device + deviceId;
			
			Log.d("nowww",webService.toString());
		
		 json = jsonParser.getJSONFromUrl(webService);
		 
		 Log.d("nowww",json.toString());
		 
		 JSONObject storeObject;

			String status = json.getString("status");

			if (status.equals("200")) {
				
			String dealid=json.getString("deal_id");
		 	 
		 
			String result = null;
			InputStream is = null;
			HttpURLConnection conn = null;
			DataOutputStream dos = null;
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String imageBase64 = null;
			String boundary = "*****";
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;
			String response;
			String converttitle = null;
			File file = null;
			
			for(int i=1 ;i<=deals.getImageCount();i++){
		 
					if (i == 1) {
						file = new File(deals.getImagePath());
					} else if (i == 2) {
						file = new File(deals.getImagePath2());
					} else if (i == 3) {
						file = new File(deals.getImagePath3());
					} else if (i == 4) {
						file = new File(deals.getImagePath4());
					}

			if (file.exists()) {

				try {
					int serverResponseCode = 0;
					// open a URL connection to the Servlet
					FileInputStream fileInputStream = new FileInputStream(
							file);
					URL url = new URL(URLApi+"upload_deal_image?deal_id="+ dealid+"&indicator="+i+"&"+param_tokenKey + tokenKey+"&"+param_device + deviceId);

					// Open a HTTP connection to the URL
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoInput(true); // Allow InputsFF
					conn.setDoOutput(true); // Allow Outputs
					conn.setUseCaches(false); // Don't use a
												// Cached
												// Copy
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("ENCTYPE",
							"multipart/form-data");
					conn.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					conn.setRequestProperty("uploaded_file",
							file.toString());

					dos = new DataOutputStream(conn.getOutputStream());

					dos.writeBytes(twoHyphens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
							+ file + "\"" + lineEnd);

					dos.writeBytes(lineEnd);

					// create a buffer of maximum size
					bytesAvailable = fileInputStream.available();

					bufferSize = Math
							.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];

					// read file and write it into form...
					bytesRead = fileInputStream.read(buffer, 0,
							bufferSize);

					while (bytesRead > 0) {

						dos.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable,
								maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0,
								bufferSize);

					}

					// send multipart form data necesssary after
					// file data...
					dos.writeBytes(lineEnd);
					dos.writeBytes(twoHyphens + boundary + twoHyphens
							+ lineEnd);

					// Responses from the server (code and
					// message)
					serverResponseCode = conn.getResponseCode();
					String serverResponseMessage = conn
							.getResponseMessage();

					Log.i("uploadFile", "HTTP Response is : "
							+ serverResponseMessage + ": "
							+ serverResponseCode);

					BufferedReader in = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));

					Log.d("BuffrerReader", "" + in);

					if (in != null) {

						response = convertStreamToString(in);
						Log.e("FINAL_RESPONSE-LENGTH",
								"" + response.length());
						Log.e("FINAL_RESPONSE", response);
					}

					fileInputStream.close();
					dos.flush();
					dos.close();

				} catch (MalformedURLException ex) {

					ex.printStackTrace();

					Log.e("Upload file to server",
							"error: " + ex.getMessage(), ex);
				} catch (Exception e) {

					e.printStackTrace();

					Log.e("Upload file to server Exception",
							"Exception : " + e.getMessage(), e);
				}

			}
			}
			}
		}catch(Exception e){
			System.out.println(e);
			
		}


		return json;

	}

	public JSONObject register(String deviceId,String userId,String otp,String vid,String tokenKey) {
		webService = URLApi + service_register + param_userid + userId +  "&" + param_device + deviceId
				+ "&" + param_otpcode + otp +  "&" + param_vid + vid
				+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

	

		return json;
	}
	
	public JSONObject registerMerchant(String businessName,
			String contactPerson, String email, String password,
			String contactphone, String businessphone, String website,
			String address, String facebookurl, String category,
			String deviceId, String type, String otp,String vid,String tokenKey) {
		webService = URLApi + service_register + param_name + contactPerson
				+ "&" + param_bssaddress + address + "&" + param_bssname
				+ businessName + "&" + param_bssphone + businessphone + "&"
				+ param_bsswebsite + website + "&" + param_otpcode + otp + "&"
				+ param_vid + vid + "&" + param_bssfburl + facebookurl + "&"
				+ param_phone + contactphone + "&" + param_email + email + "&"
				+ param_passward + password + "&" + param_passward + password
				+ "&" + param_passward + password + "&" + param_passward
				+ password + "&" + param_device + deviceId + "&" + param_type
				+ type+"&"+param_tokenKey + tokenKey;
		
		Log.d("weburl",webService.toString());
		
		JSONObject json = jsonParser.getJSONFromUrl(webService);

		return json;
	}
	
	
	public JSONObject otpGenerate(String phoneNumber) {
		webService = URLApi + service_otpverification + param_phone + phoneNumber;
		JSONObject json = jsonParser.getJSONFromUrl(webService);



		return json;
	}
	
	public JSONObject rechargeCoins(String rechargeAmount,String rechargeType,String userId,String deviceId,String tokenKey) {
		
		webService = URLApi + service_getrecharege + param_recharge_amount
				+ rechargeAmount + "&" + param_recharge_type + rechargeType
				+ "&" + param_userid + userId + "&" + param_device + deviceId
				+ "&" + param_tokenKey + tokenKey;
		;
		JSONObject json = jsonParser.getJSONFromUrl(webService);



		return json;
	}
	
	
	public JSONObject otpGenerate_new(String name,String email,String phone,String deviceId,String typeValue,
			String tokenKey) {
		webService = URLApi + service_otpverification + param_phone + phone+ "&" + param_name + name+"&" + param_email + email+"&" + param_device + deviceId+"&" + param_type + typeValue+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

	

		return json;
	}
	
	
	public JSONObject otpGenerate_merchant(String businessName,String 
			contactPerson,String email,String password,String contactphone,
			String businessphone,String website,String address,String facebookurl,String category,
			String deviceId,String typeValue,String tokenKey) {
		webService = URLApi + service_otpverification + param_name + contactPerson
				+ "&" + param_bssaddress + address + "&" + param_bssname
				+ businessName + "&" + param_bssphone + businessphone + "&"
				+ param_bsswebsite + website + "&" + param_bssfburl + facebookurl + "&"
				+ param_phone + contactphone + "&" + param_email + email + "&"
				+ param_passward + password + "&" + param_passward + password
				+ "&" + param_passward + password + "&" + param_passward
				+ password + "&" + param_device + deviceId + "&" + param_type
				+ typeValue +"&bss_category="+category+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);

	

		return json;
	}
	
	
	


	public JSONObject aroundYou(double userLat, double userLng) {
		webService = URLApi + service_deal_around_you + param_user_lat
				+ userLat + "&" + param_user_lng + userLng;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject dealDetail(String dealId,String userId,String tokenKey,String deviceId) {
		webService = URLApi + service_deal_detail + param_c_id + dealId + "&"
				+ param_userid + userId + "&" + param_tokenKey + tokenKey + "&"
				+ param_device + deviceId;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject categoryList(String tokenKey,String deviceId,String userLat,String userLong) {
		webService = URLApi + service_deal_category_list+param_tokenKey + tokenKey+"&"+param_device + deviceId+"&"+param_user_lat
				+ userLat+"&"+param_user_lng
				+ userLong;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject spinnerCategoryList(String tokenKey,String deviceId) {
		webService = URLApi + service_category_list+param_tokenKey + tokenKey+"&"+param_device + deviceId;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject dealByCategory(String valueCategoryId,String userLat,String userLong, int valueStartIndex,String tokenKey,
			String deviceId) {

		webService = URLApi + service_deal_by_category + param_category_id
				+ valueCategoryId + "&" + param_start_index + valueStartIndex
				+ "&" + param_items_per_page + valueItemsPerPage+"&"+param_user_lat
				+ userLat+"&"+param_user_lng
				+ userLong + "&"
				+ param_tokenKey + tokenKey + "&" + param_device + deviceId;

		Log.d("nowurl",webService.toString());
		
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		
		Log.d("nowresult",json.toString());
		
		return json;
	}

	public JSONObject searchByName(String valueKeyName, int valueStartIndex,
			int valueItemsPerPage) {

		webService = URLApi + service_deal_by_search + param_keyword
				+ valueKeyName + "&" + param_start_index + valueStartIndex
				+ "&" + param_items_per_page + valueItemsPerPage;

		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject currency() {
		webService = URLApi + service_currency;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject loadURL(String dealId) {
		webService = Server + folderMain + service_desc + param_deal_id
				+ dealId;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	
	public JSONObject verifyPromoCode(String promoCode,String userId,String deviceId,String tokenKey) {
		webService = URLApi + service_verifypromo + param_promo_code
				+ promoCode+ "&" + param_merchant_userid
				+ userId+"&"+param_device + deviceId+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject getAnalyticDeal(String cid,String deviceId,String tokenKey) {
		webService = URLApi + service_analyticdeal + param_c_id+ cid+"&"+param_device + deviceId
				+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject getOrderDetail(String oid,String userId,String deviceId,String tokenKey) {
		webService = URLApi + service_getorderlist + param_oid+ oid+"&"+param_device + deviceId+"&"+param_userid + userId
				+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	public JSONObject acceptPromoCode(String pid,String cid,String userId,String amount,String deviceId,String tokenKey) {
		webService = URLApi + service_confirmpromo + param_pid
				+ pid+"&" + param_c_id+ cid+  "&" + param_userid+ userId+ "&" + param_amount+ amount+
				"&"+param_device + deviceId+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject saveStatusChange(String status,String cid,String deviceId,String tokenKey) {
		webService = URLApi + service_dealstatus + param_c_id
				+ cid+ "&" + param_status
				+ status+"&status_type=deal_disable_status&"+param_device + deviceId+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject deleteDeal(String status,String cid,String deviceId,String tokenKey) {
		webService = URLApi + service_dealstatus + param_c_id
				+ cid+ "&" + param_status
				+ status+"&status_type=deal_delete_status&"+param_device + deviceId+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	
	public JSONObject deleteStore(String storeId,String deviceId,String tokenKey) {
		webService = URLApi + service_storestatus + param_storeId
				+ storeId+ "&" + param_status
				+ "1&status_type=store_delete_status&"+param_device + deviceId+"&"+param_tokenKey + tokenKey;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject getPromoCode(String cid,String userId,String userCode,String validDate,String DealId,String merchantId,String type,String tokenKey,
			String deviceId) {
		
		JSONObject json=null;
		try {
		String encodeValidDate = URLEncoder.encode(validDate,
				"UTF-8");
		
		webService = URLApi + service_getpromocode + param_c_id 
				+ cid+ "&" + param_userid
				+ userId + "&" + param_userCode
				+ userCode + "&" + param_validDate
				+ encodeValidDate + "&" + param_deal_id
				+ DealId+ "&" + param_merchant_id
				+ merchantId+"&"+page
				+ type+"&"+param_tokenKey + tokenKey+"&"+param_device + deviceId;
		
		
		 json = jsonParser.getJSONFromUrl(webService);
		}catch(Exception e){
			
		}
		
		http://www.dealsforsure.in/api/add_promocode?cid=1&uid=2&user_code=U0001&valid_upto_date=2015-03-25&deal_id=D0002
		return json;
	}
	
	public static String convertStreamToString(BufferedReader is)
			throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {

				while ((line = is.readLine()) != null) {
					sb.append(line).append("");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

}