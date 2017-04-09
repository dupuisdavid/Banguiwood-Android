package com.afrikawood.banguiwood.tools;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpRestClient {
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static int HttpDefaultTimeoutDuration = 10000;

	public static void get(String URL, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(HttpDefaultTimeoutDuration);
		client.get(URL, params, responseHandler);
	}

	public static void post(String URL, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(HttpDefaultTimeoutDuration);
		client.post(URL, params, responseHandler);
	}

}
