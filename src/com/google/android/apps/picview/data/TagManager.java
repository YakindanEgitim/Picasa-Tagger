package com.google.android.apps.picview.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
	private static Queue<DeleteTagRequest>deleteQeue = new LinkedList<DeleteTagRequest>();
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
	

	

	public void addTagStart(Photo photo, Album album, List<String> newKeywords) {
		for(String tag: newKeywords){
			doAddTagRequest(tag, album, photo);
		}
		
	}
	public  void deleteTagStart(Photo photo, Album album, List<String> deletedKeywords) {
		for(String key: deletedKeywords){
			deleteQeue.add(new DeleteTagRequest(album ,photo ,key));
		}
		startDeleteTags();
	}
	private void startDeleteTags() {
		if(tags == null){
			doAllTagRequest();
		}else{
			for(DeleteTagRequest req :deleteQeue){
				doDeleteTagRequest(req.getTag(), req.getAlbum(), req.getPhoto());
			}
		} 
		
	}
	private void doAddTagRequest(String tag, Album album, Photo photo) {
		// Use text field value.
		PicasaTagsUrl url = new PicasaTagsUrl(userName);

	    String urlString = url.getAddTagUrl(album, photo, authKey);
		if(dbg)
			Log.v(TAG, "tags url" + urlString);
	    String content = PicasaXmlBuilder.addTagXmlString(tag);
	    AsyncPutTask request = new AsyncPutTask(cachedWebPutRequestFetcher,
	    		urlString,  content, "POST", false, "Loading albums...",  ctx,
	        new RequestCallback() {
	          @Override
	          public void success(String data) {
	        	  Log.v(TAG,"Success doAddTagRequest");
	          }

	          @Override
	          public void error(String message) { 
	          }
	        });
	    request.execute();
	  }
	private void doDeleteTagRequest(String tag, Album album, Photo photo) {
		PicasaTagsUrl url = new PicasaTagsUrl(userName);

	    String urlString = url.getDeleteUrl(tags.get(tag),album, photo, authKey);
		if(dbg)
			Log.v(TAG, "tags url " + urlString);
	    String content = PicasaXmlBuilder.addTagXmlString(tag);
	    AsyncPutTask request = new AsyncPutTask(cachedWebPutRequestFetcher,
	    		urlString,  content, "DELETE", false, "Loading albums...",  ctx,
	        new RequestCallback() {
	          @Override
	          public void success(String data) {
	        	  Log.v(TAG,"Success doDeleteTagRequest");
	          } 

	          @Override
	          public void error(String message) {
	          }
	        });
	    request.execute();
	}
	private void doAllTagRequest() {
		// Use text field value.
		PicasaTagsUrl url = new PicasaTagsUrl(userName);

	     String urlString = url.getAuthUrl(authKey);
		if(dbg)
			Log.v(TAG, "tags url" + urlString);
	    AsyncRequestTask request = new AsyncRequestTask(cachedWebRequestFetcher,
	    		urlString,   true,  null, ctx,
	        new RequestCallback() {
	          @Override
	          public void success(String data) {
	        	  tags = parseTagFromPicasaXml(data);
	        	  startDeleteTags();
	        	  Log.v(TAG,"Success doAllTagRequest");
	          }
 
	          @Override
	          public void error(String message) {

	        	  Log.v(TAG,"doAllTagRequest error");
	          }
	        });
	    request.execute();
	  }

}
