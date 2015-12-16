package com.texastech.httputil;


 
public interface HttpTaskListener{
	/**
	 * @param ac
	 * @param param
	 */
	public void sendHttpRequest(Action ac, String... param);
	/**
	 * 
	 * @param ac
	 * @param response
	 */
	public void onSuccess(Action ac, String response);
	
	public void onFaliure(Action ac, String error);
}
