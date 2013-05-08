package com.example.picasa_tagger;

import android.app.Activity;
import android.os.Bundle;
 
import com.google.gdata.client.*;
import com.google.gdata.client.photos.*;
import com.google.gdata.data.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.photos.*;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.util.AuthenticationException;

public class MainActivity extends Activity{

	private static final String TAG = "PicasaAndroidSample";



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
		PicasawebService myService = new PicasawebService("exampleCo-exampleApp-1");
		
			myService.setUserCredentials("liz@gmail.com", "mypassword");
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
}