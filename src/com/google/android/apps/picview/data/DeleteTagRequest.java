package com.google.android.apps.picview.data;


public class DeleteTagRequest {
	Album album;
	Photo photo;
	String tag;
	public DeleteTagRequest(Album album, Photo photo, String tag) {
		super();
		this.album = album;
		this.photo = photo;
		this.tag = tag;
	}
	public Album getAlbum() {
		return album;
	}
	public void setAlbum(Album album) {
		this.album = album;
	}
	public Photo getPhoto() {
		return photo;
	}
	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
