package com.google.android.apps.picview.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;

import android.util.Log;

import com.google.android.apps.picview.data.FileSystemWebResponseCache;
import com.google.android.apps.picview.data.WebResponseCursor.CachedWebResponse;

public class CachedWebPutRequestFetcher {
	  private static final String TAG = CachedWebPutRequestFetcher.class
	      .getSimpleName();

	  private HashMap<URL, String> cache = new HashMap<URL, String>();

	  /** Used to synchronize access based on URLs. */
	  private HashMap<String, URL> urls = new HashMap<String, URL>();


	  /**
	   * Instantiated the {@link CachedImageFetcher}.
	   */
	  public CachedWebPutRequestFetcher() {
	  }

	  /**
	   * Performs a cached fetch. If the response is in one of the caches
	   * (file-system or in-memory), this version is returned. If the response could
	   * not be found in cache, it's fetched and automatically put into both caches.
	   * 
	   * @param url
	   *          the URL to fetch
	   * @param forceFetchFromWeb
	   *          whether the content should be fetched from the web, regardless of
	   *          whether it is present in any of the caches
	   */
	  public void cachedFetch(URL url, String data, boolean forceFetchFromWeb) {

	    // Make sure we have a URL object that we can synchronize on.
	    url = getSynchronizableInstance(url);

	    // Synchronize per URL.
	    synchronized (url) {
	      boolean response = false;

	      // If it is also not found in the file system cache, or fetching
	      // from cache was intentionally skipped, try to fetch it
	      // from the network.
	    	  response = putToWeb(url, data);
	      
	    }
	  }

	  /**
	   * Returns whether a response from a request with the given URL exists in the
	   * in-memory cache.
	   */
	  public boolean isCached(URL url) {
	    return cache.containsKey(url);
	  }

	  /** 
	   * Fetches the given URL from the web.
	   */
	  public boolean putToWeb(URL url, String data) {
		  
	    Log.d(TAG, "Fetching from web: " + url.toString());
	    HttpsURLConnection conn = null;
	    try {
	    	conn = (HttpsURLConnection) url.openConnection();
	    	conn.setRequestMethod("POST");
	    	conn.setUseCaches(false);
	    	conn.setReadTimeout(30000); // 30 seconds. 
	    	conn.addRequestProperty("GData-Version", "2");
	    	conn.addRequestProperty("Content-Length", String.valueOf(data.length()));
	    	conn.addRequestProperty("MIME-version", "1.0");
	    	conn.addRequestProperty("Content-Type", "application/atom+xml");
	    	conn.addRequestProperty("Encoding", "UTF-8l");

	    	conn.setDoInput(true);
	    	conn.setDoOutput(true);
	    	conn.connect();
	    	OutputStream out = conn.getOutputStream();
	    	boolean val = writeStringFromStream(out,data.getBytes("UTF-8"));
	    	Log.v(TAG, readStringFromStream(conn.getInputStream()));
	    	return val;
	    } catch (Exception e) {
	    Log.v(TAG,readStringFromStream(conn.getErrorStream()));
	      e.printStackTrace();
	    }
	    return false;
	  }

	  private URL getSynchronizableInstance(URL url) {
	    if (urls.containsKey(url.toString())) {
	      url = urls.get(url.toString());
	    } else {
	      urls.put(url.toString(), url);
	    }
	    return url;
	  }

	  /**
	   * Read the content of an {@link InputStream} as String.
	   * 
	   * @param stream
	   *          the stream to read from
	   * @return the content of the stream or an empty string, if an error occurs.
	   */
	  private static String readStringFromStream(InputStream stream) {
	    final int READ_BUFFER = 4096;
	    StringBuilder builder = new StringBuilder();
	    byte b[] = new byte[READ_BUFFER];
	    int l = 0;
	    try {
	      if (stream == null) {
	        return "";
	      } else {
	        while ((l = stream.read(b)) > 0) {
	          builder.append(new String(b, 0, l));
	        }
	      }
	    } catch (IOException ex) {
	      ex.printStackTrace();
	      return "";
	    }
	    return builder.toString();
	  }
	  
	  private static boolean writeStringFromStream(OutputStream stream, byte[] data) {
		    try {
		      if (stream == null) {
		        return false;
		      } else {
		        for(int i=0; i < data.length; i++) {
		        	stream.write(data[i]);
		        }
		      }
		    } catch (IOException ex) {
		      ex.printStackTrace();
		      return false;
		    }
		    return true;
		  }
	}