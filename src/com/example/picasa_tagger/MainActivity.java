package com.example.picasa_tagger;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends Activity{
	private static final boolean dbg = true;
	private static final String TAG = "MainActivity";
    protected AccountManager accountManager; 
 
	
	@Override   
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GetUrl url = new GetUrl("https://gdata.youtube.com/feeds/api/videos");
		url.setArg("q", "skateboarding+dog");
		url.setArg("start-index", "21");
		url.setArg("max-results", "10");
		if(dbg)
			Log.v(TAG, url.getUrl());
        accountManager = AccountManager.get(getApplicationContext());
		

	}

}