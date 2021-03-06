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

package com.google.android.apps.picview.activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.picasa_tagger.R;
import com.example.picasa_tagger.TagDialog;
import com.google.android.apps.picview.adapter.AccountsAdapter;
import com.google.android.apps.picview.data.Album;
import com.google.android.apps.picview.data.FileSystemImageCache;
import com.google.android.apps.picview.data.Photo;
import com.google.android.apps.picview.request.CachedImageFetcher;
import com.google.android.apps.picview.request.ImageLoadingTask;

/**
 * An activity that shows a single photo.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PhotoViewActivity extends Activity {
	private static final String TAG = "PhotoViewActivity";
	private static final boolean dbg = false;
	private static final int MENU_ADD_ACCOUNT = 0;
	private static final int MENU_PREFERENCES = 1;
	private static final int MENU_ABOUT = 2;
	private AccountsAdapter adapter;

	private static final int CONTEXT_MENU_CREDITS = 0;
	private static final int CONTEXT_MENU_TAGS = 1;
	private static class SavedConfiguration {
		public int currentIndex;
		public CachedImageFetcher cachedImageFetcher;

		public SavedConfiguration(int currentIndex,
				CachedImageFetcher cachedImageFetcher) {
			this.currentIndex = currentIndex;
			this.cachedImageFetcher = cachedImageFetcher;
		}
	}

	private static final String KEY_INDEX = "index";
	private static final String KEY_PHOTOS = "photos";
	private static final String KEY_ALBUM_NAME = "albumName";
	private static final String KEY_ALBUM = "album";
	private static final String KEY_USERNAME = "userName";
	private static final String KEY_AUTHKEY = "authKey";

	private ImageView photoView;
	private TextView txtPhotoTitle;
	private TextView txtAlbumName;
	private View photoTouchAreaLeft;
	private View photoTouchAreaRight;

	private int currentIndex = 0;
	private List<Photo> photos;
	private String albumName = "";
	private CachedImageFetcher cachedImageFetcher;
	private int photoSizeLongSide = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.photo_view);
		photoView = (ImageView) findViewById(R.id.photo);
		txtPhotoTitle = (TextView) findViewById(R.id.photo_title);
		txtAlbumName = (TextView) findViewById(R.id.photo_album_name);
		photoTouchAreaLeft = findViewById(R.id.photo_touch_left);
		photoTouchAreaRight = findViewById(R.id.photo_touch_right);

		photoTouchAreaLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPreviousPhoto();
			}
		});

		photoTouchAreaRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextPhoto();
			}
		});

		cachedImageFetcher = new CachedImageFetcher(new FileSystemImageCache());
		initCurrentConfiguration();
		showPhoto();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return new SavedConfiguration(currentIndex, cachedImageFetcher);
	}

	private void initCurrentConfiguration() {
		SavedConfiguration savedConfig = (SavedConfiguration) getLastNonConfigurationInstance();
		if (savedConfig != null) {
			currentIndex = savedConfig.currentIndex;
			cachedImageFetcher = savedConfig.cachedImageFetcher;
		} else {
			currentIndex = getIntent().getExtras().getInt(KEY_INDEX);
		}
		photos = getIntent().getExtras().getParcelableArrayList(KEY_PHOTOS);
		albumName = getIntent().getExtras().getString(KEY_ALBUM_NAME);
	}

	private void showNextPhoto() {
		currentIndex++;
		if (currentIndex == photos.size()) {
			currentIndex--;
		} else {
			showPhoto();
		}
	}

	private void showPreviousPhoto() {
		currentIndex--;
		if (currentIndex < 0) {
			currentIndex = 0;
		} else {
			showPhoto();
		}
	}

	private void showPhoto() {
		if (photoSizeLongSide < 0) {
			// Determines the size for the photo shown full-screen (without zooming).
			DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
			photoSizeLongSide = Math.max(displayMetrics.heightPixels,
					displayMetrics.widthPixels);
		}

		try {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading photo");
			ImageLoadingTask imageLoadingTask = new ImageLoadingTask(
					photoView,
					new URL(photos.get(currentIndex).getMediumImageUrl(photoSizeLongSide)),
					cachedImageFetcher, progressDialog);
			imageLoadingTask.execute();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}

		txtPhotoTitle.setText(photos.get(currentIndex).getName());
		txtAlbumName.setText(albumName);

		if (photos.size() > (currentIndex + 1)) {
			try {
				Photo photo = photos.get(currentIndex + 1);
				if (photo != null) {
					cachedImageFetcher.maybePrefetchImageAsync(new URL(photo
							.getMediumImageUrl(photoSizeLongSide)));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, CONTEXT_MENU_CREDITS, 0, R.string.credist);
		menu.add(0, CONTEXT_MENU_TAGS, 1, R.string.tags);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXT_MENU_CREDITS:
			// TODO implement these 
			if(dbg)
				Log.v(TAG, "CONTEXT_MENU_CREDITS clicked");
			return true;
		case CONTEXT_MENU_TAGS:
			if(dbg)
				Log.v(TAG, "CONTEXT_MENU_TAGS clicked");
			Bundle bundle = getIntent().getExtras();
			TagDialog tg = new TagDialog((Album) bundle.getSerializable(KEY_ALBUM), bundle.getString(KEY_USERNAME),
					bundle.getString(KEY_AUTHKEY), this,photos.get(currentIndex));
			tg.show();
			Log.v(TAG, bundle.getString(KEY_ALBUM_NAME)+ bundle.getString(KEY_USERNAME)+
					bundle.getString(KEY_AUTHKEY));
			return true; 
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() != R.id.accounts_list) {
			return;
		}
	}
}
