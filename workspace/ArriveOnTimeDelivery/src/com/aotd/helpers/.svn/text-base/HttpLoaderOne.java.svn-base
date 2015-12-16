package com.aotd.helpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;

public class HttpLoaderOne extends Thread {
	
	private String		 feedUrl;
	private StringBuffer ERROR_MESSAGE;
	public  StringBuffer text=new StringBuffer();
	public  boolean 	 isPost = false;
	public  String 	 	 postData = "";
	public  boolean 	 isUpload = false;
	public  byte[] 		 responseBytes = null;
	public  byte[] 		 imgBytes = null;
	public String 		 mFileName = "";
	
	public  boolean      isMultipart = false;
	public Hashtable params = null;

	
	
	public HttpLoaderOne(String feedUrl) 
	{
		this.feedUrl=feedUrl;	
		
//		Log.e("", " *** feedUrl *** "+feedUrl);
	}


	@Override
	public void run()
	{		
		super.run();
	
		ERROR_MESSAGE = new StringBuffer();			
		int BUFFER_SIZE = 2000;
		
		InputStreamReader isr = null;
		
		HttpURLConnection httpConn = null;
		try {
				httpConn = openHttpConnection(feedUrl, ERROR_MESSAGE);
				System.out.println("got the message");
				if (ERROR_MESSAGE.length() == 0)
				{				
					isr = new InputStreamReader(httpConn.getInputStream());
					
					int charRead;
					char[] inputBuffer = new char[BUFFER_SIZE];
					while ((charRead = isr.read(inputBuffer)) > 0)
					{
						// ---convert the chars to a String---
						String readString = String.copyValueOf(inputBuffer,0, charRead);
						text.append(readString);
						inputBuffer = new char[BUFFER_SIZE];
					}
	
					
					isr.close();
					httpConn.disconnect();
					responseBytes = null;
			   }
			
		  }
		  catch (IOException e) 
		  {
			ERROR_MESSAGE.append("Network timed out or the server is not responding, please contact support...");
			e.printStackTrace();
		  }
		 catch (Exception e)
		 {
			ERROR_MESSAGE.append("Network timed out or the server is not responding, please contact support...");
			e.printStackTrace();
		 }
		finally{
			try{
				isr.close();
				responseBytes = null;
			}catch(Exception ex){}
		}
//		Log.e("Serivce Data",text.toString());
		parse(text.toString(),ERROR_MESSAGE.toString());
		
		
	}

	protected void parse(String text2,StringBuffer errMsg) 
	{		
	}
	
	protected void parse(String text2,String errMsg) 
	{		
	}
	


	
	String getBoundaryString()
	{
	    String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";
	    return BOUNDARY;
	}
	
	String getBoundaryMessage(String boundary)
	{
	    
	    String fileField = "filename";
	    String fileName = mFileName;
	    String fileType = "image/jpg";
 	    
	    
        StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");

        Enumeration keys = params.keys();

        while(keys.hasMoreElements())
        {
                String key = (String)keys.nextElement();
                String value = (String)params.get(key);

                res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n")    
                        .append("\r\n").append(value).append("\r\n")
                        .append("--").append(boundary).append("\r\n");
        }
        if(imgBytes !=null){
        res.append("Content-Disposition: form-data; name=\"").append(fileField).append("\"; filename=\"").append(fileName).append("\"\r\n") 
                .append("Content-Type: ").append(fileType).append("\r\n\r\n");
        }    
        return res.toString();
	}
	
	private HttpURLConnection openHttpConnection(String Url,	StringBuffer errMsg) 
	{
		int resCode=-1;
		
		HttpURLConnection httpConn = null;
		OutputStream ost = null;
		try
		{ 
			//Log.v("Service Url " , Url);
			URL url= new URL(Url);
			URLConnection urlconnection = url.openConnection();
			if(!(urlconnection instanceof HttpURLConnection))
				ERROR_MESSAGE.append("URL is not an Http URL");
			
			else
			{
				httpConn = (HttpURLConnection)urlconnection;
				httpConn.setConnectTimeout(30*1000);// 1 min
				httpConn.setReadTimeout(20000);
				
				 if(isMultipart)
				 {
				   try{
					 httpConn.setDoOutput(true);
					 httpConn.setDoInput(true);
					 httpConn.setRequestMethod("POST");
					 httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + getBoundaryString());
					 ost = httpConn.getOutputStream();
					 
					 String boundary = getBoundaryString(); //"----------V2ymHFg03ehbqgZCaKO6jy"
				     String boundaryMessage = getBoundaryMessage(boundary); 
				     System.out.println("The boundry message is " + boundaryMessage);
				      
				     ost.write(boundaryMessage.getBytes());
				      
				      // wrtie the image bytes
				      byte[] imageBytes = imgBytes;
				      
				      if(imgBytes != null )
				      {
				        System.out.println("The Image to be uploaded length is " + imageBytes.length);

				          int index = 0;
				          int size = 1024;
				          do{
				            if((index+size)>imageBytes.length)
				            {
				                size = imageBytes.length - index;
				            }
				            ost.write(imageBytes, index, size);
				            index+=size;
				            //progress(imgData.length, index); // update the progress bar.
				         }while(index<imageBytes.length);

				      } 
				      String endBoundary = "\r\n--" + boundary + "--\r\n";
				      ost.write(endBoundary.getBytes());

				  		ost.flush();
				  		ost.close();   
	            	}catch(Exception ex){
	            		ERROR_MESSAGE.append("Error uploading data" + ex.getMessage());
	            		ex.printStackTrace();
	            	}finally{
	            		try{
	            			ost.flush();
	            			ost.close();
	            		}catch(Exception ex){
	            			
	            		}
	            	}
				      
				    
				      
				}else if(!isPost){
	            	httpConn.setAllowUserInteraction(false);
	            	httpConn.setRequestMethod("GET");
	            	httpConn.setInstanceFollowRedirects(true);
	            	httpConn.connect(); 
	            	
	            }else{
	            	//Log.v("Service Post","post method");
	            	try{
		            	if(!isUpload)
		            	{	
		            		//httpConn.setRequestMethod("POST");
		            		//httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		            		//httpConn.setRequestProperty("Content-Language", "en-US"); 										
		            	}
		            	httpConn.setDoOutput(true);  
                    
                        if(!isUpload)
                        	responseBytes  = postData.getBytes("UTF-8");
                        //Log.v("HttpLoader",""+ responseBytes.length);
  	            	
		            	ost = httpConn.getOutputStream();
		            	if(!isUpload)
		            	{
			            	for (int iCtr = 0; iCtr < responseBytes.length; iCtr++) {
						        ost.write(responseBytes[iCtr]);
						      }
		            	}else{
		            		ost.write(responseBytes);
		            	}
						ost.flush();
						ost.close();   
	            	}catch(Exception ex){
	            		ERROR_MESSAGE.append("Error uploading data, Network timed out or the server is not responding, please contact support...");
	            		ex.printStackTrace();
	            	}finally{
	            		try{
	            		ost.flush();
						ost.close();
	            		}catch(Exception ex){}
	            	}
	            	
	            }
				if(ERROR_MESSAGE.length()==0)
				{
//		            resCode = httpConn.getResponseCode();    
//		            //Log.v("resCode",""+resCode);
//		            if (resCode == HttpURLConnection.HTTP_OK)
//		            {	            	
//		                                                 
//		            }
//				
//		            else
//		            {
//		               	ERROR_MESSAGE.append("Invalid or Bad URL request to the Server, HTTP error code " + resCode);	           
//		            }
				}
			}    
			
		}
		 catch (MalformedURLException e)
		 {
			 e.printStackTrace();
			ERROR_MESSAGE.append("Network timed out or the server is not responding, please contact support...");
		 }
		 catch (IOException e)
		 {
			 e.printStackTrace();
			 ERROR_MESSAGE.append("Network timed out or the server is not responding, please contact support...");
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 ERROR_MESSAGE.append("Network timed out or the server is not responding, please contact support...");
		 }
		//Log.v("Service",ERROR_MESSAGE.toString());
		
		return httpConn;
	}
	
	
}
