package com.google.android.apps.picview.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import com.google.android.apps.picview.data.parser.PicasaTagSaxHandler;
import com.google.android.apps.picview.data.parser.PicasaXmlBuilder;
import com.google.android.apps.picview.request.AsyncPutTask;
import com.google.android.apps.picview.request.AsyncRequestTask;
import com.google.android.apps.picview.request.CachedWebPutRequestFetcher;
import com.google.android.apps.picview.request.CachedWebRequestFetcher;
import com.google.android.apps.picview.request.AsyncRequestTask.RequestCallback;
import com.google.android.apps.picview.request.PicasaTagsUrl;

public class TagManager {
	private static final String TAG = "TagManager";
	private static final boolean dbg = true;
	private String userName;
	private String authKey;
	private Context ctx;

	private static Map<String, String> tags = null;
	private static Map<String, Photo> deleteQeue = new HashMap<String, Photo>();
	private static CachedWebRequestFetcher cachedWebRequestFetcher = new CachedWebRequestFetcher(new FileSystemWebResponseCache());
	private static CachedWebPutRequestFetcher cachedWebPutRequestFetcher = new CachedWebPutRequestFetcher();
	public static Map<String, String> parseTagFromPicasaXml(String xmlStr) {
		PicasaTagSaxHandler handler = new PicasaTagSaxHandler();
		try {
			// The Parser somehow has some trouble with a plus sign in the
			// content. This is a hack to fix this.
			// TODO: Maybe we should replace all these special characters with
			// XML entities?
			xmlStr = xmlStr.replace("+", "&#43;");
			Xml.parse(xmlStr, handler);
			return handler.getTags();
		} catch (SAXException e) {
			Log.e("Photo", e.getMessage(), e);
		}
		return new HashMap<String,String>();
	}
	
	public TagManager(String userName, String authKey, Context ctx){
		this.userName = userName;
		this.authKey = authKey;
		this.ctx = ctx;
	}
	private void doTagsRequest() {
		// Use text field value.
		PicasaTagsUrl url = new PicasaTagsUrl(userName);

		String urlString;
		if(authKey != null)
			urlString = url.getAuthUrl(authKey);
		else
			urlString = url.getUrl();
		AsyncRequestTask request = new AsyncRequestTask(cachedWebRequestFetcher,
				urlString, false, "Loading albums...",  ctx,
				new RequestCallback() {
			@Override
			public void success(String data) {
				tags = TagManager.parseTagFromPicasaXml(data);
				startDeleteTags();
			}

			@Override
			public void error(String message) {
			}
		});
		request.execute();
	}

	private void doAddTagRequest(String tag, Album album, Photo photo) {
		// Use text field value.
		PicasaTagsUrl url = new PicasaTagsUrl(userName);

	    String urlString = url.getAddTagUrl(album, photo, authKey);
		if(dbg)
			Log.v(TAG, "tags url" + urlString);
	    String content = PicasaXmlBuilder.addTagXmlString(tag);
	    AsyncPutTask request = new AsyncPutTask(cachedWebPutRequestFetcher,
	    		urlString,  content,false, "Loading albums...",  ctx,
	        new RequestCallback() {
	          @Override
	          public void success(String data) {
	        	  Log.v(TAG,"Success");
	          }

	          @Override
	          public void error(String message) {
	          }
	        });
	    request.execute();
	  }

	public void addTagStart(Photo photo, Album album, List<String> newKeywords) {
		for(String tag: newKeywords){
			doAddTagRequest(tag, album, photo);
		}
		
	}
	public static void deleteTagStart(Photo photo, List<String> deletedKeywords) {
		for(String key: deletedKeywords){
			deleteQeue.put(key, photo);
		}
		startDeleteTags();
	}
	private static void startDeleteTags() {
		if(tags == null){
		}
		
	}

}
