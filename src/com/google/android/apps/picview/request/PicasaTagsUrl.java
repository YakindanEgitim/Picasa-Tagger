package com.google.android.apps.picview.request;

import com.google.android.apps.picview.data.Album;
import com.google.android.apps.picview.data.Photo;

public class PicasaTagsUrl implements UrlProvider {
	//http://picasaweb.google.com/data/entry/api/user/{my username}/albumid/ {the albumid}?authkey={the authkey}&kind=photo 
	private static final String BASE_URL = "picasaweb.google.com/data/feed/api/user/";
	private static final String BASE_ADDTAG_URL = "https://picasaweb.google.com/data/feed/api/user/%s/albumid/%s/photoid/%s";
	private String user;

	public PicasaTagsUrl(String user) {
		this.user = user; 
	}
 
	@Override
	public String getUrl() {
		return "http://" + BASE_URL + user + "?kind=tag";
	}
 
	@Override
	public String getAuthUrl(String authKey) {
		return String.format("https://%s?kind=tag&access_token=%s&access=all", BASE_URL + user,authKey);
	}
	public String getAddTagUrl(Album album, Photo photo, String authKey) {
		return String.format(BASE_ADDTAG_URL + "?access_token=%s",  user, album.getId(), photo.getID(), authKey);
	}
	
	public String getDeleteUrl(String tagId, Album album, Photo photo, String authKey) {
		return String.format("https://"+BASE_ADDTAG_URL+"/tag/%s?kind=tag&access_token=%s",  user, album.getId(), photo.getID(), tagId, authKey);
	}
}

