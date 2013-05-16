package com.google.android.apps.picview.data.parser;

public class PicasaXmlBuilder {
	public static String addTagXmlString(String tag){
		return String.format(
				"<entry xmlns='http://www.w3.org/2005/Atom'>"+
				"<title>%s</title>" +
				"<category scheme=\"http://schemas.google.com/g/2005#kind\"" +
				" term=\"http://schemas.google.com/photos/2007#tag\"/>" +
				"</entry>", tag);
	}
}
