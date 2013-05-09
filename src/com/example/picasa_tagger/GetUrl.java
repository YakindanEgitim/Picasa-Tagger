package com.example.picasa_tagger;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class GetUrl {
	private static final boolean dbg = true;
	private static final String TAG = "GETURL";
	Map<String,String> args = new HashMap<String,String>();
	String base ;
	public GetUrl(String baseUrl){
		base = baseUrl;		
	}
	public void setArg(String key, String value){
		args.put(key, value);
	}
	public void delArg(String key){
		args.remove(key);
	}
	public String getUrl(){
		StringBuilder str = new StringBuilder();
		str.append(base);
		str.append("?");
		for(String key : args.keySet()){
			str.append(String.format("%s=%s", key,args.get(key)));
			str.append("&");
		}

		str.deleteCharAt(str.length() - 1);
		if(dbg)
			Log.v(TAG, str.toString());
		return str.toString();
	}
	
}
