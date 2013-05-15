package com.example.picasa_tagger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AccountList extends ListActivity {
	protected AccountManager accountManager;
	protected Intent intent;
	Account[] accounts;
	/** Called when the activity  is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(getApplicationContext());
        accounts = accountManager.getAccountsByType("com.google");
        String[] names = new String[accounts.length];
        for(int i = 0; i<accounts.length; i++){
        	names[i] = accounts[i].name;
        }
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,R.id.selection, names));        
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
       /* Intent intent = new Intent(this, AlbumListActivity.class);
        intent.putExtra("accountId", accounts[position].name);
        this.startActivity(intent);*/
    	Account account = accounts[position];
    	Intent intent = new Intent(this, AppInfo.class);
    	intent.putExtra("account", account);
    	startActivity(intent);
    }
}