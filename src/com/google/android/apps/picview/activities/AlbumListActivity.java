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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.picasa_tagger.R;
import com.google.android.apps.picview.adapter.AlbumsAdapter;
import com.google.android.apps.picview.adapter.MultiColumnImageAdapter.ThumbnailClickListener;
import com.google.android.apps.picview.data.Album;
import com.google.android.apps.picview.data.FileSystemImageCache;
import com.google.android.apps.picview.data.FileSystemWebResponseCache;
import com.google.android.apps.picview.data.Photo;
import com.google.android.apps.picview.request.AsyncRequestTask;
import com.google.android.apps.picview.request.AsyncRequestTask.RequestCallback;
import com.google.android.apps.picview.request.CachedImageFetcher;
import com.google.android.apps.picview.request.CachedWebRequestFetcher;
import com.google.android.apps.picview.request.PicasaAlbumsUrl;
import com.google.android.apps.picview.ui.ThumbnailItem;

/**
 * Allows the user to enter a Picasa username for which this activity shows all
 * the available albums.
 * 
 * TODO(haeberling): Try to merge this with the {@link PhotoListActivity}.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AlbumListActivity extends Activity {
	private static final String TAG = "AlbumListActivity";
	private static final boolean dbg = false;

  private static class SavedConfiguration {
    public final List<Album> albums;
    public final CachedImageFetcher cachedImageFetcher;

    public SavedConfiguration(List<Album> albums,
        CachedImageFetcher cachedImageFetcher) {
      this.albums = albums;
      this.cachedImageFetcher = cachedImageFetcher;
    }
  }

  private ListView mainList;
  private LayoutInflater inflater;
  private List<Album> albums = new ArrayList<Album>();
  private CachedImageFetcher cachedImageFetcher;
  private CachedWebRequestFetcher cachedWebRequestFetcher;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.album_list);
    mainList = (ListView) findViewById(R.id.albumlist);
    inflater = LayoutInflater.from(this);

    cachedImageFetcher = new CachedImageFetcher(new FileSystemImageCache());
    cachedWebRequestFetcher = new CachedWebRequestFetcher(
        new FileSystemWebResponseCache());

    initCurrentConfiguration();

    // TODO: This is picasa specific.
    final String accountId = getIntent().getExtras().getString("accountId");
    final String authKey = getIntent().getExtras().getString("authKey");
    if (accountId != null) {
      doAlbumsRequest(accountId, authKey);
    } else {
      showAlbums();
    }
  }

  private void initCurrentConfiguration() {
    SavedConfiguration savedConfig = (SavedConfiguration) getLastNonConfigurationInstance();

    if (savedConfig != null) {
      albums = savedConfig.albums;
      cachedImageFetcher = savedConfig.cachedImageFetcher;
    }
  }

  /**
   * Loads the albums for the given user.
   * 
   * TODO: This is Picasa specific.
   */
  private void doAlbumsRequest(String userName, String authKey) {
    // Use text field value.
    PicasaAlbumsUrl url = new PicasaAlbumsUrl(userName);

    String urlString;
    if(authKey != null)
    	urlString = url.getAuthUrl(authKey);
    else
    	urlString = url.getUrl();
    AsyncRequestTask request = new AsyncRequestTask(cachedWebRequestFetcher,
    		urlString, false, "Loading albums...", this,
        new RequestCallback() {
          @Override
          public void success(String data) {
            AlbumListActivity.this.albums = Album.parseFromPicasaXml(data);
            Log.d(TAG, "Albums loaded: " + AlbumListActivity.this.albums.size());
            showAlbums();
          }

          @Override
          public void error(String message) {
            Log.e(TAG, "Could not load albums: " + message);
            showError("Error while fetching albums");
          }
        });
    request.execute();
  }

  private void doPhotosRequest(final Album album, String gdataUrl) {
    AsyncRequestTask request = new AsyncRequestTask(cachedWebRequestFetcher,
        gdataUrl, false, "Loading photos...", this,
        new RequestCallback() {

          @Override
          public void success(String data) {
            showPhotos(album, Photo.parseFromPicasaXml(data));
          }

          @Override
          public void error(String message) {
            Log.e(TAG, "Could not load photos: " + message);
            showError("Error while fetching photos");
          }
        });
    request.execute();
  }

  /**
   * Show a visual error message to the user.
   * 
   * @param message
   *          the message to show
   */
  private void showError(String message) {
    final Builder builder = new AlertDialog.Builder(AlbumListActivity.this);
    builder.setTitle(message);
    builder.setIcon(android.R.drawable.ic_dialog_alert);
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setMessage(message);
    builder.show();

  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    return new SavedConfiguration(albums, cachedImageFetcher);
  }

  private void showAlbums() {
    if (albums == null) {
      return;
    }

    ThumbnailClickListener<Album> foo = new ThumbnailClickListener<Album>() {
      @Override
      public void thumbnailClicked(Album album) {
    	  if(dbg)
    	  Log.v(TAG, album.getGdataUrl());
        doPhotosRequest(album, album.getGdataUrl());
      }
    };
    mainList.setAdapter(new AlbumsAdapter(wrap(albums), inflater, foo,
        cachedImageFetcher, getResources().getDisplayMetrics()));
    BaseAdapter adapter = (BaseAdapter) mainList.getAdapter();
    adapter.notifyDataSetChanged();
    adapter.notifyDataSetInvalidated();
    mainList.invalidateViews();
  }

  private void showPhotos(Album album, List<Photo> photos) {
    Log.d(TAG, "SHOW PHOTOS()");
	Bundle bundle = getIntent().getExtras();
    Intent intent = new Intent(this, PhotoListActivity.class);
    intent.putParcelableArrayListExtra("photos", (ArrayList<Photo>) photos);
    intent.putExtra("album", ((Parcelable)album));
    intent.putExtra("layout", R.layout.photo_list);
    intent.putExtra("userName", bundle.getString("accountId"));
    intent.putExtra("authKey", bundle.getString("authKey"));
    startActivity(intent);
  }

  /**
   * Wraps a list of {@link Album}s into a list of {@link ThumbnailItem}s, so
   * they can be displayed in the list.
   */
  private static List<ThumbnailItem<Album>> wrap(List<Album> albums) {
    List<ThumbnailItem<Album>> result = new ArrayList<ThumbnailItem<Album>>();
    for (Album album : albums) {
      result.add(new ThumbnailItem<Album>(album.getName(), album
          .getThumbnailUrl(), album));
    }
    return result;
  }
}