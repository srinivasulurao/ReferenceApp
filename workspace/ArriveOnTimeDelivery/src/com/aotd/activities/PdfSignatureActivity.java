package com.aotd.activities;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.andpdf.nio.ByteBuffer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.aotd.utils.Utils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Header;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;

public class PdfSignatureActivity extends Activity {

	private static final String TAG = "PdfSignatureActivity";
	ImageView imageView, signImage;
	Button ok, previos, next;

	RelativeLayout add_parent;

	//	signature mSignature;
	Paint paint;
	LinearLayout mContent;
	Button mClear, mGetSign, mCancel;
	View mView;

	Bitmap pdfPage;

	byte[] bs;

	int windowwidth;
	int windowheight;
	int pdfwidth;
	int pdfheight;

	int pageNumber = 1;

	PDFFile pdf_file;

	private android.widget.RelativeLayout.LayoutParams params;

	String original;
	PdfReader reader;
	private int delayTime;
	private boolean isFromOncreate;

	private TextView txtTitleHeader;
	private ImageView imgOnline;
	private String fileNameDlOrRT;
	String comp="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.aotd_pdf_signature);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		comp = getIntent().getStringExtra("company");

		try {
			//	copyingFileToSDcard(getIntent().getStringExtra("orderNumber"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		imageView = (ImageView) findViewById(R.id.imageView1);
		ok = (Button) findViewById(R.id.ok);
		next = (Button) findViewById(R.id.next);
		previos = (Button) findViewById(R.id.previos);
		add_parent = (RelativeLayout) findViewById(R.id.add_parent);

		txtTitleHeader = (TextView)  findViewById(R.id.signature_layout_title);
		txtTitleHeader.setText(getIntent().getStringExtra("orderNumber").trim());

		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);

		if(Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);


		if(getIntent().getStringExtra("condition").equalsIgnoreCase("updateSingDelivered"))
		{
			fileNameDlOrRT = "dl";
		}else{
			fileNameDlOrRT = "rt";
		}

		
		String newFolder = "/AOTD";
		String extStorageDirectory = Environment
				.getExternalStorageDirectory().toString();
		File myNewFolder = new File(extStorageDirectory + newFolder);
		File file = new File(myNewFolder, fileNameDlOrRT+getIntent()
				.getStringExtra("orderNumber")+"_sign.pdf");

		if(file.exists()){
			original = Environment.getExternalStorageDirectory()+ "/AOTD/"+fileNameDlOrRT+getIntent()
					.getStringExtra("orderNumber")+"_sign.pdf";

		}else{
			original = Environment.getExternalStorageDirectory()+ "/AOTD/"+getIntent()
					.getStringExtra("orderNumber")+".pdf";

		}

		Log.i(TAG, "original pdf path: "+original);
		byte[] bytes;
		//File originalFile = new File(Environment.getExternalStorageDirectory(),	"/AOTD/1234.pdf");
		File originalFile = new File(original);

		if (originalFile.exists()) {

			try {

				FileInputStream is = new FileInputStream(originalFile);
				long length = originalFile.length();
				bytes = new byte[(int) length];
				int offset = 0;
				int numRead = 0;

				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}

				ByteBuffer buffer = ByteBuffer.NEW(bytes);
				String data = Base64.encodeToString(bytes, Base64.DEFAULT);

				pdf_file = new PDFFile(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}


		} else {

			Toast.makeText(getApplicationContext(), "File is not found",Toast.LENGTH_SHORT).show();
		}



		try {
			reader = new PdfReader(original);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PDFImage.sShowImages = true; // show images
		PDFPaint.s_doAntiAlias = true; // make text smooth

		toViewPage(pageNumber,true);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.e("action UP", "action UP");

				//				add_parent.setVisibility(View.VISIBLE);
				//				addTheLayout();

				Intent sign_intent = new Intent(PdfSignatureActivity.this,SignatureActivity.class);
				sign_intent.putExtra("orderNumber", getIntent().getStringExtra("orderNumber"));
				sign_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
				sign_intent.putExtra("condition", getIntent().getStringExtra("condition"));
				sign_intent.putExtra("deliveryType", getIntent().getStringExtra("deliveryType"));
				sign_intent.putExtra("from", "aotd");
				sign_intent.putExtra("company", comp);
				startActivity(sign_intent);
				finish();

			}
		});

		previos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (pageNumber > 0) {

					pageNumber--;
					toViewPage(pageNumber,false);
				}

			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (pageNumber < reader.getNumberOfPages()) {

					pageNumber++;
					toViewPage(pageNumber,false);
				}
			}
		});

		//		signImage.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//
		//				Log.e("action UP", "action UP");
		//				Toast.makeText(getApplicationContext(), "clciked",
		//						Toast.LENGTH_SHORT).show();
		//
		//				add_parent.setVisibility(View.VISIBLE);
		//				addTheLayout();
		//
		//			}
		//		});

	}

	private void copyingFileToSDcard(String orderNumber) throws Exception {

		AssetManager assetManager = getAssets();
		InputStream inputStream = assetManager.open("samplesign.pdf");

		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath()+"/AOTD");
		dir.mkdirs();
		File file = new File(dir, orderNumber+".pdf");
		FileOutputStream f = new FileOutputStream(file);

		byte[] buffer = new byte[1024];
		int read;
		while ((read = inputStream.read(buffer)) != -1) {
			f.write(buffer, 0, read);
		}

	}

	private void toViewPage(int pageNumber, boolean isFromOncreate) {

		try {

			PDFPage page = pdf_file.getPage(pageNumber, true);

			final ProgressDialog dialog = new ProgressDialog(PdfSignatureActivity.this);
			dialog.setMessage("Please wait while loading...");
			dialog.setCancelable(false);
			dialog.show();
			RectF rect = new RectF(0, 0, (int) page.getBBox().width(),(int) page.getBBox().height());
			pdfPage = page.getImage((int) rect.width(),	(int) rect.height(), rect);

			if (isFromOncreate) {
				delayTime = 10;

			}else{
				delayTime = 1000;

			}
			// create thread
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {

					Drawable drawable = new BitmapDrawable(getResources(), pdfPage);
					imageView.setImageDrawable(drawable);
					dialog.dismiss();
				}
				//sleep thread for 1000 mili second
			}, delayTime);



			/*	runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Drawable drawable = new BitmapDrawable(getResources(), pdfPage);
						imageView = (ImageView) findViewById(R.id.imageView1);
						//imageView.refreshDrawableState();
						imageView.setImageDrawable(drawable);
						imageView.postInvalidateDelayed(3000);
						imageView.notify();

						//imageView.setImageBitmap(null);
						//imageView.setImageBitmap(pdfPage);
						//imageView.postInvalidate();
						//imageView.refreshDrawableState();
						//imageView.notify();
					}
				});*/



		} catch (Exception e) {

			System.out.println("Error in main -->>" + e.toString());
		}

	}

	
	public static void toSaveThePDF(byte[] bs2, String orderNumber, String condition) {

		String original = Environment.getExternalStorageDirectory()+ "/AOTD/"+orderNumber+".pdf"; // 4 pages
		String dir = Environment.getExternalStorageDirectory() + "/AOTD/"; // 1page
		String dlOrRT;
		if(condition.equalsIgnoreCase("updateSingDelivered"))
		{
			dlOrRT = "dl";
		}else{
			dlOrRT = "rt";
		}
		
		String signFile = dir +dlOrRT+orderNumber+ "_sign.pdf";
		Document document = new Document();

		try {
		
			PdfReader reader = new PdfReader(original);
			
			PdfStamper pdfStamper = new PdfStamper(reader,	new FileOutputStream(signFile));
			Image image = Image.getInstance(bs2);
			
			

			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			Date date = new Date();
			String sDate = dateFormat.format(date).toString();
			
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {

				PdfContentByte content = pdfStamper.getUnderContent(i);
				content = pdfStamper.getOverContent(i);
				image.setAbsolutePosition(0,0); //0,10
				content.addImage(image);
				}
			
			/*
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {

				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(signFile));
				 Font FONT = new Font(Font.FontFamily.TIMES_ROMAN, 06, Font.BOLD, new BaseColor(0, 0, 0));
				  PdfContentByte canvas = pdfStamper.getOverContent(1);
				  ColumnText.showTextAligned(
				    canvas,
				    Element.ALIGN_LEFT, 
				    new Phrase(sDate, FONT), 
				    05, 160, 0
				  );
				document.open();
				writer.setPageEmpty(false);
				document.newPage();

			}*/
			
			
			
			
			document.close();
			pdfStamper.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		//moveFile(dir, "samplesign.pdf", original);

	}



	private static void addTitlePage(Document document)
			throws DocumentException {
		Paragraph preface = new Paragraph();
		// We add one empty line
		// Lets write a big header
		preface.add(new Paragraph("Details Of the Form"));

		document.add(preface);
		// Start a new page
		document.newPage();
	}

	private void moveFile(String inputPath, String inputFile, String outputPath) {

		InputStream in = null;
		OutputStream out = null;

		try {

			// create output directory if it doesn't exist
			File dir = new File(outputPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			new File(outputPath).delete();

			in = new FileInputStream(inputPath + inputFile);
			out = new FileOutputStream(outputPath);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

			in.close();
			in = null;

			// write the output file
			out.flush();
			out.close();
			out = null;

		}

		catch (FileNotFoundException fnfe1) {
			Log.e("tag", fnfe1.getMessage());
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}
	}

	
}