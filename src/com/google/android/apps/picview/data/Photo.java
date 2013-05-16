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

package com.google.android.apps.picview.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import com.google.android.apps.picview.data.parser.PicasaPhotosSaxHandler;
import com.google.android.apps.picview.data.parser.PicasaTagSaxHandler;

/**
 * The Photo data object containing all information about a photo.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class Photo implements Serializable, Parcelable {
	private static final String TAG = "Photo";
	private static final boolean dbg = true;
  private static final long serialVersionUID = 12L;

  private String id;
  private String name;
  private String imageUrl; 
  private String title;
  private String credit;
  private String description;
  private String group;
  private List<String> oldkeywords;
  private List<String> keywords;
  private String thumbnailUrl; 
  
  public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
    public Photo createFromParcel(Parcel in) {
      try {
        ObjectInputStream inputStream = new ObjectInputStream(
            new ByteArrayInputStream(in.createByteArray()));
        return (Photo) inputStream.readObject();
      } catch (StreamCorruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      return null;
    }

    public Photo[] newArray(int size) {
      return new Photo[size];
    }
  };


  /**
   * Parses photos XML (a list of photo; the contents of an album).
   * 
   * @param xmlStr
   *          the photo XML
   * @return a list of {@link Photo}s
   */
  public static List<Photo> parseFromPicasaXml(String xmlStr) {
    PicasaPhotosSaxHandler handler = new PicasaPhotosSaxHandler();
    try {
      // The Parser somehow has some trouble with a plus sign in the
      // content. This is a hack to fix this.
      // TODO: Maybe we should replace all these special characters with
      // XML entities?
      xmlStr = xmlStr.replace("+", "&#43;");
      if(dbg)
    	  Log.v(TAG, xmlStr);
      Xml.parse(xmlStr, handler);
      return handler.getPhotos();
    } catch (SAXException e) {
      Log.e("Photo", e.getMessage(), e);
    }
    return new ArrayList<Photo>();
  }

  /**
   * Returns the photo name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the photo.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the thumbnail URL of the photo.
   */
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  /**
   * Sets the thumbnail URL of the photo.
   */
  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  /**
   * Returns the URL of a medium resolution version the photo that can be used
   * to be shown on the screen.
   * <p>
   * TODO(haeberling): This is Picasa specific, this should be made more
   * general.
   */
  public String getMediumImageUrl(int photoSizeLongSide) {
    int pos = imageUrl.lastIndexOf('/');
    return imageUrl.substring(0, pos + 1) + 's' + photoSizeLongSide
        + imageUrl.substring(pos);
  }

  /**
   * Returns the URL to the highest resolution version of the photo.
   */
  public String getFullImageUrl() {
    return imageUrl;
  }

  /**
   * Sets the URL to the highest resolution version of the photo.
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
   * Returns the serialized Photo object.
   */
  public byte[] convertToBytes() {
    try {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      ObjectOutputStream output = new ObjectOutputStream(result);
      output.writeObject(this);
      return result.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByteArray(convertToBytes());
  }

  public String getTitle() {
	  return title;
  }

  public void setTitle(String title) {
	  this.title = title;
  }

  public String getCredit() {
	  return credit;
  }

  public void setCredit(String credit) {
	  this.credit = credit;
  }

  public String getDescription() {
	  return description;
  }

  public void setDescription(String description) {
	  this.description = description;
  }

  public String getGroup() {
	  return group;
  }

  public void setGroup(String group) {
	  this.group = group;
  }

  public List<String> getKeywords() {
	  return keywords;
  }
  public String getKeywordsString() {
	  StringBuilder str = new StringBuilder();
	  for(String s:this.keywords){
		  str.append(s);
		  str.append(",");
	  }
	  str.deleteCharAt(str.length()-1);
	  return str.toString();
  }
  public void setKeywordsFromString(String keywords) {
	  this.keywords = new ArrayList<String>();
	  addKeywordsFromString(keywords);
  }
  public void addKeywordsFromString(String keywords) {
	  this.keywords = new ArrayList<String>();
	  this.oldkeywords= new ArrayList<String>();
	  if(dbg)
			Log.v(TAG, "keywords -> " + keywords);
	  for(String key: keywords.split(",")){
		  addKeyword(key);
		  oldkeywords.add(key);
	  }
  }
  public void addKeyword(String keywords) {
	  this.keywords.add(keywords);
  }
  public void setKeywords(List<String> keywords) {
	  this.keywords = keywords;
  }

  public String getImageUrl() {
	  return imageUrl;
  }

  public void saveOnServer(Album album, String username, String authkey, Context ctx) {

		if(dbg)
			Log.v(TAG, "saveOnServer new tag count :" + getNewKeywords().size());
	  TagManager tm = new TagManager(username, authkey, ctx);
	  tm.addTagStart(this, album, getNewKeywords());
	  TagManager.deleteTagStart(this, getDeletedKeywords());

  }
  public List<String> getNewKeywords(){
	  List<String> newKeywords = new ArrayList();
	  for(String key: keywords){
		  if(! oldkeywords.contains(key)){
			  newKeywords.add(key);
		  }
	  }
	  return newKeywords;
  }

  public List<String> getDeletedKeywords(){
	  List<String> deletedKeywords = new ArrayList();
	  for(String key: oldkeywords){
		  if(! keywords.contains(key)){
			  deletedKeywords.add(key);
		  }
	  }
	  return deletedKeywords;
  }

  public void setID(String string) {
	  this.id = string;

  } 
  public String getID() {
	  return this.id;

  }
  

  
  
}
