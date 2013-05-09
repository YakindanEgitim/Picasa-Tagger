package com.example.picasa_tagger;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TagDialog extends Dialog{
	private static final String TAG = "TagDialog";
	private static final boolean dbg = true;

	public TagDialog(Context context) {
		super(context);
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
		//TODO implement this
		this.dismiss();
	}

}
