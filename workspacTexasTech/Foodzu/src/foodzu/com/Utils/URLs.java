package foodzu.com.Utils;

public class URLs {
	public static String MAIN = "http://foodzu.com/";
	public static String DOMAIN = "app/";
	public static String BASE = MAIN + DOMAIN;

	public static String MENU_URL		 		= BASE + "category.php";
	public static String SUB_MENU_URL	 		= BASE + "menucat.php?category_id=";
	public static String CATEGORY_URL			= BASE + "home.php";
	public static String MAIN_HOME_PROD_URL 	= BASE + "homeproducts.php";
	public static String FILTER_PROD_URL		= BASE + "products.php?category_id=";
	public static String SEARCH_URL				= BASE + "search.php?keywords=%s&userid=%s";
	public static String CHK_PINCODE_URL 		= BASE + "checkzipcode.php?pincode=";
	public static String SHIPPING_ADDRESS_URL 	= BASE + "shippingaddress.php?";
	public static String GETCOUNTRYS_URL 		= BASE + "countrylist.php";
	public static String CART_COST_URL 			= BASE + "prdcalcaultion.php?";
	public static String ONLINE_PAY_URL 		= BASE + "onlinepayment.php?";
	public static String COD_URL 				= BASE + "cod.php?";
	public static String APP_LOGIN_URL			= BASE + "applogin.php?";
	public static String CHK_USER_URL 			= BASE + "checkuser.php?email=";
	public static String GET_PREV_ORDERS_URL 	= BASE + "transaction.php?userid=";
	public static String SIGNUP_URL 			= BASE + "registration.php?First_Name=";
	public static String FETCH_ADDRESS_URL 		= BASE + "fetchaddress.php?emailid=";
	public static String SEARCH_KEYWORD_URL 	= BASE + "prdtitle.php";
	public static String ABOUT_US_URL 			= BASE + "aboutus.php";
	public static String FAV_ITEM_URL 			= BASE + "user_fav_items_list.php?userid=";
	public static String SET_FAV_URL 			= BASE + "user_fav_items.php?userid=%s&product_id=%s&favourite_value=1";
	public static String UNSET_FAV_URL 			= BASE + "user_fav_items.php?userid=%s&product_id=%s&favourite_value=0";
	public static String FILTER_BRAND			= BASE + "brand_catid.php?category_id=";
	public static String FILTER_PRICE 			= BASE + "pricerange_catid.php?category_id=";
	public static String FILTER_URL		    = BASE + "products.php?category_id=%s&brandid=%s&pricerange=%s";
	
	
}
