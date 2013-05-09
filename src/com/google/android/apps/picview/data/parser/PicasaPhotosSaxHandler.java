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

package com.google.android.apps.picview.data.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.google.android.apps.picview.data.Photo;

/**
 * A SAX handler for parsing Picasa Photos XML.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PicasaPhotosSaxHandler extends DefaultHandler {
	private static final String TAG = "PicasaPhotosSaxHandler";
	private static final boolean dbg = true;
	private List<Photo> albums = new ArrayList<Photo>();
	private Photo currentPhoto;
	private List<String> elementNames = Arrays.asList(new String[]{"content", "title", "credit", "description", "group", "keyword", "thumbnail","name"});
	private int currentElement = -1;
	private StringBuilder nameBuilder = new StringBuilder();
	private StringBuilder titleBuilder = new StringBuilder();
	private StringBuilder creditBuilder = new StringBuilder();
	private StringBuilder descriptionBuilder = new StringBuilder();
	private StringBuilder keywordsBuilder = new StringBuilder();


	public List<Photo> getPhotos() {
		return albums;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(currentElement != -1)
			Log.v(TAG, "Current index -> " + currentElement + "  " + elementNames.get(currentElement));
		Log.v(TAG, "Current index -> " + String.copyValueOf(ch, start, length) );
		if(currentElement == elementNames.indexOf("content")){

		}else if(currentElement == elementNames.indexOf("title")){
			String title = String.copyValueOf(ch, start, length);
			if(dbg)
				Log.v(TAG, "title -> " + title);
			titleBuilder.append(title);	
		}else if(currentElement == elementNames.indexOf("credit")){
			String credit = String.copyValueOf(ch, start, length);
			if(dbg)
				Log.v(TAG, "credit -> " + credit);
			titleBuilder.append(credit);	

		}else if(currentElement == elementNames.indexOf("description")){
			String description = String.copyValueOf(ch, start, length);
			if(dbg)
				Log.v(TAG, "description -> " + description);

			descriptionBuilder.append(description);	

		}else if(currentElement == elementNames.indexOf("group")){

		}else if(currentElement == elementNames.indexOf("keyword")){
			String keywords = String.copyValueOf(ch, start, length);
			if(dbg)
				Log.v(TAG, "keywords -> " + keywords);
			keywordsBuilder.append(keywords);	

		}else if(currentElement == elementNames.indexOf("thumbnail")){

		}else{
			nameBuilder.append(ch, start, length);		
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("entry")) {
			albums.add(currentPhoto);
		} else if (localName.equals("title")) {
			if (currentPhoto != null) {
				currentPhoto.setName(nameBuilder.toString());
				currentPhoto.setTitle(titleBuilder.toString());
				currentPhoto.setCredit(creditBuilder.toString());
				currentPhoto.setDescription(descriptionBuilder.toString());
				currentPhoto.setKeywordsFromString(keywordsBuilder.toString());
			}
		}
		nameBuilder.setLength(0);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		Log.v(TAG, "Current index -> " + currentElement);
		if (localName.equals("entry")) {
			currentPhoto = new Photo();
		} else {
			if (currentPhoto != null) {
				// We can do better by selecting the best size. Right now we
				// always use the last one, which should be the best one.
				if (localName.equals("thumbnail")) {
					currentElement = elementNames.indexOf("thumbnail");
					String thumbnail = attributes.getValue("", "url");
					if(dbg)
						Log.v(TAG, "thumbnail -> " + thumbnail);
					currentPhoto.setThumbnailUrl(thumbnail);
				} else if (localName.equals("title")) {
					currentElement = elementNames.indexOf("title");
					
				} else if (localName.equals("credit")) {
					currentElement = elementNames.indexOf("credit");
					
				}else if (localName.equals("description")) {
					currentElement = elementNames.indexOf("description");
					
				}else if (localName.equals("group")) {
					currentElement = elementNames.indexOf("group");
					
				}else if (localName.equals("keywords")) {
					currentElement = elementNames.indexOf("keywords");
				}
			}
		}
	}
}