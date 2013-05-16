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

import com.google.android.apps.picview.data.Album;

/**
 * A SAX handler for parsing Picasa Albums XML.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PicasaAlbumsSaxHandler extends DefaultHandler {
	private static final String TAG = "PicasaAlbumsSaxHandler";
	private static final boolean dbg = !true;
	private List<Album> albums = new ArrayList<Album>();
	private List<String> elementNames = Arrays.asList(new String[]{"id", "name"});
	private int currentElement = -1;
	private Album currentAlbum;
	private StringBuilder builderName = new StringBuilder();
	private StringBuilder builderID = new StringBuilder();

	public List<Album> getAlbums() {
		return albums;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if(dbg)
			Log.v(TAG, "characters " + String.copyValueOf(ch, start, length));
		if(currentElement == elementNames.indexOf("name")){
			builderName.append(ch, start, length);		
		}else if(currentElement == elementNames.indexOf("id")){
			builderID.append(ch, start, length);		
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("entry")) {
			albums.add(currentAlbum);
		} else if (localName.equals("title")) {
			if (currentAlbum != null) {

				if(dbg)
					Log.v(TAG, "builderName " + builderName.toString());
				if(dbg)
					Log.v(TAG, "builderID " + builderID.toString());
				currentAlbum.setName(builderName.toString());
				currentAlbum.setId(builderID.toString());
				builderID = new StringBuilder();
				builderName = new StringBuilder();
			}
		}
		builderName.setLength(0);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(dbg)
			Log.v(TAG, "startElement " + localName);
		currentElement = elementNames.indexOf(localName);
		if (localName.equals("entry")) {
			currentAlbum = new Album();
		} else { 
			if (currentAlbum != null) {
				if (localName.equals("thumbnail")) {
					String thumbnail = attributes.getValue("", "url");
					currentAlbum.setThumbnailUrl(thumbnail);
				} else if (localName.equals("link")) {
					if (attributes.getValue("", "rel").equals(
							"http://schemas.google.com/g/2005#feed")) {
						String gdataUrl = attributes.getValue("", "href");
						currentAlbum.setGdataUrl(gdataUrl);
					}
				}
			}
		}
	}
}