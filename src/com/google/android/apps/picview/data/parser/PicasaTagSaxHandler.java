package com.google.android.apps.picview.data.parser;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PicasaTagSaxHandler extends DefaultHandler{
	private Map<String,String> tags = new HashMap<String,String>();
	private String currentTag;
	private StringBuilder builderTag = new StringBuilder();
	private StringBuilder builderID = new StringBuilder();
	private boolean inID = false;
	private boolean inTitle  = false;

	public Map<String, String> getTags() {
		return tags;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(inID)
			builderID.append(ch, start, length);
		if(inTitle)
			builderTag.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("entry")) {
			tags.put(builderTag.toString(), builderID.toString());
		}
		inID = false;
		inTitle  = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("entry")) {
			currentTag = new String();
		} else {
			if (currentTag != null) {
				if (localName.equals("id")) {
					inID = true;
				}else{
					inID = false;
				} 
				if (localName.equals("title")) {
					inTitle  =  true;
				}else{
					inTitle = false;
				}
			}
		}
	}
}