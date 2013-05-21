package com.example.picasa_tagger;

import java.io.IOException;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.apps.picview.activities.AlbumListActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AppInfo extends Activity {
	private static final String TAG = "AppInfo";
	DefaultHttpClient http_client = new DefaultHttpClient();
	String AUTH_TOKEN_TYPE = "oauth2:https://picasaweb.google.com/data/";
	String AUTH_TOKEN_TYPE2 = "lh2";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_info);
	}

	@Override 
	protected void onResume() {  
		super.onResume();
		Intent intent = getIntent();
		AccountManager accountManager = AccountManager.get(getApplicationContext());
		Account account = (Account)intent.getExtras().get("account");
		accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, true, new GetAuthTokenCallback(), null);
	}
	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

		private static final String TAG = AppInfo.TAG + "/GetAuthTokenCallback";

		public void run(AccountManagerFuture result) {
			Bundle bundle;
			try {
				bundle = (Bundle) result.getResult();
				for(String key:bundle.keySet()){
					Log.v(TAG, String.format("%s = %s", key, bundle.get(key)));
				}
				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
				if(intent != null) {  
					// User input required
					startActivity(intent);
				} else { 
					onGetAuthToken(bundle);
				}
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} 
		protected void onGetAuthToken(Bundle bundle) {
			String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			String name = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
		 	AppManager.getAppManager().setActiveAcccount(new PicasaAccount(name, auth_token));
			Intent intent = new Intent(AppInfo.this, AlbumListActivity.class);
	        intent.putExtra("accountId", name);
 	        intent.putExtra("authKey", auth_token);
	        AppInfo.this.startActivity(intent);
			//new GetCookieTask().execute(auth_token);
		}
	}

}