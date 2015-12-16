package com.youflik.youflik.proxy;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientSingalTon {

	private static Object yfLock = new Object();
	private static CookieStore yfCookie = null;

	public static HttpClient getHttpClienttest() {
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		synchronized (yfLock) {
			if (yfCookie == null) {
				yfCookie = httpClient.getCookieStore();
			} else {
				httpClient.setCookieStore(yfCookie);
			}
		}
		return httpClient;
	}

}
