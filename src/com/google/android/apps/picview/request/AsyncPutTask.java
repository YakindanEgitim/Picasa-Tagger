/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.picview.request;

import java.net.URL;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * A task that executes an HTTP request asynchronously, without blocking the UI
 * thread.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AsyncPutTask extends AsyncTask<Void, Integer, String> {

	public static interface RequestCallback {
		public void success(String data);

		public void error(String message);
	}

	private static final String TAG = AsyncPutTask.class.getSimpleName();

	private CachedWebPutRequestFetcher fetcher;
	private final String url;
	private final com.google.android.apps.picview.request.AsyncRequestTask.RequestCallback callback;
	private final boolean forceFetchFromWeb;
	private final Context context;
	private ProgressDialog progressDialog = null;
	private String errorMessage;
	private String data;

	public AsyncPutTask(CachedWebPutRequestFetcher cachedPutRequestFetcher, String url, String content,
			boolean forceFetchFromWeb, String loadingMessage, Context context,
			com.google.android.apps.picview.request.AsyncRequestTask.RequestCallback requestCallback) {
		this.fetcher = cachedPutRequestFetcher;
		this.url = url;
		this.forceFetchFromWeb = forceFetchFromWeb;
		this.context = context;
		this.callback = requestCallback;
		this.data = content;

		if (loadingMessage != null) {
			this.progressDialog = new ProgressDialog(context);
			this.progressDialog.setMessage(loadingMessage);
		}
	}

	@Override
	protected void onPreExecute() {
		if (progressDialog != null) {
			progressDialog.show();
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			fetcher.cachedFetch(new URL(url), data, forceFetchFromWeb);

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (result != null) {
			callback.success(result);

		} else {
			callback.error(errorMessage);
		}
	}


}
