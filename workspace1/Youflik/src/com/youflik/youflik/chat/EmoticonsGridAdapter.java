package com.youflik.youflik.chat;

import java.io.InputStream;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EmoticonsGridAdapter extends BaseAdapter{
	
	private ArrayList<StickersModel> paths;
	private int pageNumber;
	Context mContext;
	private ImageLoader imageLoader;
	private DisplayImageOptions profile_options;
   
	
	KeyClickListener mListener;
	
	public EmoticonsGridAdapter(Context context, ArrayList<StickersModel> paths, int pageNumber, KeyClickListener listener) {
		this.mContext = context;
		this.paths = paths;
		this.pageNumber = pageNumber;
		this.mListener = listener;
	}
	public View getView(int position, View convertView, ViewGroup parent){
        StickersModel model = (StickersModel)paths.get(position);
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.emoticons_item, null);			
		}
		
		final String path = model.getStickerUrl();
		
	
		ImageView image = (ImageView) v.findViewById(R.id.item);
		imageLoader = ImageLoader.getInstance();
		profile_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		imageLoader.displayImage(path, image, profile_options, new SimpleImageLoadingListener() {
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
		});	
        
		/*image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				//mListener.keyClickedIndex(path);
			}
		});
*/
		return v;
	}
	
	@Override
	public int getCount() {		
		return paths.size();
	}
	
	@Override
	public StickersModel getItem(int position) {		
		return paths.get(position);
	}
	
	@Override
	public long getItemId(int position) {		
		return position;
	}
	
	public int getPageNumber () {
		return pageNumber;
	}
	
	private Bitmap getImage (String path) {
		AssetManager mngr = mContext.getAssets();
		InputStream in = null;
		
		 try {
				in = mngr.open("emoticons/" + path);
		 } catch (Exception e){
					e.printStackTrace();
		 }
		 
		 //BitmapFactory.Options options = new BitmapFactory.Options();
		 //options.inSampleSize = chunks;
		 
		 Bitmap temp = BitmapFactory.decodeStream(in ,null ,null);
		 return temp;
	}
	
	public interface KeyClickListener {
		
		public void keyClickedIndex(String index);
	}
}
