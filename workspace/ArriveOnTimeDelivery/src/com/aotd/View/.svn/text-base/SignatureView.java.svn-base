package com.aotd.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SignatureView extends View {
	
	private static final float 	MINP = 0.25f;
	private static final float 	MAXP = 0.75f;
	private Paint       		mPaint;
	private Canvas      		mCanvas;
	private Path        		mPath;
	private Paint       		mBitmapPaint;
	public Bitmap    	mBitmap = null;
	public boolean 		movedone = false;
	Display display; 
	int width;
	int height;
	
	public SignatureView(Context c) 
	{
		super(c);
		display = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		width = display.getWidth(); 
		height = display.getHeight();
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(10f);
		
		mPath 	= new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.v("on size changed","on size changed");
		if(mBitmap == null){
			
			mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mCanvas.drawColor(Color.WHITE);
			
		}else{

			Log.e("", " this.mBitmap "+ mBitmap.getRowBytes());
			mCanvas = new Canvas(mBitmap);

		}
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//  canvas.drawColor(0x00000000);
		Log.v("on draw canvas","on draw canvas");  
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		
		canvas.drawPath(mPath, mPaint);
	}
	
	
	public void setBitmap(Bitmap mBitmap) {
		
		this.mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

		Log.e("", " this.mBitmap "+this.mBitmap.getRowBytes());
	}
	
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;
	
	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y); 
		mX = x;
		mY = y;
	}
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
			mX = x;
			mY = y;
		}
	}
	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			movedone = true;
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}
	
	
}


