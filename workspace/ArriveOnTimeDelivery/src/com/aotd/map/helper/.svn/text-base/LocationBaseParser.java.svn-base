package com.aotd.map.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class LocationBaseParser {
	
	protected final String 	GEOCODE_RESPONSE	=	"GeocodeResponse";
	protected final String 	STATUS				=	"status";
	protected final String 	RESULT				=	"result";
	protected final String 	FORMATTED_ADDRESS	=	"formatted_address";
	protected final String 	ADDRESS_COMPONENT	=	"address_component";
	protected final String 	LONG_NAME			=	"long_name";
	protected final String  SHORT_NAME			= 	"short_name";
	protected final String  TYPE				=	"type";
	
	private static final String TAG	= LocationBaseParser.class.getSimpleName();

    
    final URL feedUrl;

    protected LocationBaseParser(String feedUrl){
        try {
            this.feedUrl = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected InputStream getInputStream() {
        try {
            return feedUrl.openConnection().getInputStream();
        } catch (IOException e) {
        	Log.e(TAG, "internet connection lose");
        	throw new RuntimeException(e);
        }
    }
}

