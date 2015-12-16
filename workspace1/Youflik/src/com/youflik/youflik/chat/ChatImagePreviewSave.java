package com.youflik.youflik.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.polites.android.GestureImageView;
import com.youflik.youflik.R;

public class ChatImagePreviewSave extends ActionBarActivity{
	private String imagePath;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Bitmap showedImgae;
	public Menu menuInstance;
	private GestureImageView imageView;
	private MediaScannerConnection msConn = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chat_image_viewer);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		imagePath = bundle.getString("ImagePath");
		imageView = (GestureImageView)findViewById(R.id.image);
		final ProgressBar spinner = (ProgressBar)findViewById(R.id.loading);

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.placeholder)
		.showImageOnFail(R.drawable.placeholder)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();

		imageLoader.getInstance().displayImage(imagePath, imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				spinner.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "Input/Output error";
					break;
				case DECODING_ERROR:
					message = "Image can't be decoded";
					break;
				case NETWORK_DENIED:
					message = "Downloads are denied";
					break;
				case OUT_OF_MEMORY:
					message = "Out Of Memory error";
					break;
				case UNKNOWN:
					message = "Unknown error";
					break;
				}

				spinner.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

				showedImgae = loadedImage;


				spinner.setVisibility(View.GONE);
			}
		});
	}

	public void downloadImage(){

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/DCIM/Youflik Chat");    
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "YF-Chat-"+ n +".jpg";
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete (); 
		try {
			FileOutputStream out = new FileOutputStream(file);
			showedImgae.compress(Bitmap.CompressFormat.JPEG, 100, out);
			Toast.makeText(ChatImagePreviewSave.this, "Image Saved", Toast.LENGTH_SHORT).show();
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		getApplicationContext().sendBroadcast(mediaScanIntent);
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		menuInstance = menu;
		getMenuInflater().inflate(R.menu.save_image, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.saveImage: 
			downloadImage();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
