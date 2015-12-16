package foodzu.com;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends Activity {

	private ImageView imgThumbnail, plus, minus;
	private TextView prod_name, prod_cost, prod_desc, count;
	Intent i;
	int value;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_product_details);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		prod_name = (TextView) findViewById(R.id.prod_name);
		prod_cost = (TextView) findViewById(R.id.prod_cost);
		prod_desc = (TextView) findViewById(R.id.desc_details);

		imgThumbnail = (ImageView) findViewById(R.id.imgThumbnail);
		plus = (ImageView) findViewById(R.id.plus);
		minus = (ImageView) findViewById(R.id.minus);
		count = (TextView) findViewById(R.id.count);
		
		i = getIntent();
		value = i.getIntExtra("count", 0);
			
		plus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				value = Integer.parseInt(count.getText().toString());
				if (value >= 0) 
					count.setText(Integer.toString(value + 1));
			}
		});

		minus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				value = Integer.parseInt(count.getText().toString());
				if (value > 0) 
					count.setText(Integer.toString(value - 1));
			}
		});
		
		prod_name.setText(i.getStringExtra("item_name"));
		prod_cost.setText("\u20B9  " + i.getStringExtra("actual_price"));
		prod_desc.setText(i.getStringExtra("actual_price"));
		count.setText(Integer.toString(value));
		Picasso.with(ProductDetailsActivity.this)
				.load("http://cdn.zunu.in/product_images/medium.jpg").fit()
				.centerInside().tag(ProductDetailsActivity.this).skipMemoryCache().into(imgThumbnail);
		
		
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
}
