package dealsforsure.in;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import dealsforsure.in.adapters.AdapterCart;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Cart;
import dealsforsure.in.utils.Utils;

public class ActivityCart extends ActionBarActivity {
	
	private ActionBar actionbar;
	private Utils utils;
	SharedPreferences sharedPreferences;
	UserFunctions userFunction;
	List<Cart> cartItemList = new ArrayList<Cart>();
	Cart cart;
	AdapterCart adapterCart;
	ListView lvCart;
	Integer orderTotal;
	TextView tvOrderTotal;

	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		userFunction = new UserFunctions();
		utils = new Utils(ActivityCart.this);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityCart.this);
		
		Set<String> cartItemSet = new HashSet<String>();
		cartItemSet = sharedPreferences.getStringSet("cartItem", new HashSet<String>());
		tvOrderTotal=(TextView)findViewById(R.id.tvordertotal);
		lvCart = (ListView) findViewById(R.id.lvcart);
		lvCart.setItemsCanFocus(true);
		
		if (cartItemSet.size() > 0) {
			Iterator itr = cartItemSet.iterator();
			orderTotal=0;
			while (itr.hasNext()) {

				Gson gson = new Gson();
				String cartItems = (String) itr.next();
				cart = gson.fromJson(cartItems, Cart.class);
				orderTotal=orderTotal+ new Integer(cart.getPrice());
				cartItemList.add(cart);

			}
			
			tvOrderTotal.setText(orderTotal.toString()+Utils.mCurrency);

		
		
			adapterCart = new AdapterCart(this, R.layout.adapter_cart,
					cartItemList);

			lvCart.setAdapter(adapterCart);
		}
		
		
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
