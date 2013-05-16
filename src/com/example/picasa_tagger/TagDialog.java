package com.example.picasa_tagger;

import java.util.List;

import com.google.android.apps.picview.data.Album;
import com.google.android.apps.picview.data.Photo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TagDialog extends Dialog{
	private static final String TAG = "TagDialog";
	private static final boolean dbg = true;
	Photo photo;
	String userName;
	String authkey;
	String album;
	public TagDialog(String albumName,String userName,String authkey,Context context, Photo photo) {
		super(context);
		this.userName =  userName;
		this.authkey =  authkey;
		this.album =  albumName;
		setContentView(R.layout.tag_dialog);
		((Button) findViewById(R.id.button_save)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				saveAndExit();
			}
		});
		((Button) findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		((Button) findViewById(R.id.button_new_tag)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Editable editAble =((EditText) findViewById(R.id.tag_name)).getText();
				((TagAdapter) ((ListView) findViewById(R.id.tags_list)).getAdapter()).addTag(editAble.toString());
			}
		});
		((ListView) findViewById(R.id.tags_list)).setAdapter(new TagAdapter(photo.getKeywords()));

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(this.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = (int) (metrics.heightPixels * 0.7);
		this.getWindow().setAttributes(lp);
		this.photo = photo;
		setTitle("Tags");
	}

	public void cancel(){
		if(dbg)
			Log.v(TAG, "cancel");
		this.dismiss();
	}
	public void saveAndExit(){
		if(dbg)
			Log.v(TAG, "saveAndExit");
		photo.saveOnServer(album, userName, authkey, getContext());
		this.dismiss();
	}

}
