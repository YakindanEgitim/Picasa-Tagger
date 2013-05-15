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

package com.google.android.apps.picview.request;

/**
 * A simple class that generates a Picasa albums feed URL for a given user.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PicasaAlbumsUrl implements UrlProvider {
	//http://picasaweb.google.com/data/entry/api/user/{my username}/albumid/ {the albumid}?authkey={the authkey}&kind=photo 
  private static final String BASE_URL = "picasaweb.google.com/data/feed/api/user/";
  private String user;

  public PicasaAlbumsUrl(String user) {
    this.user = user; 
  }
 
  @Override
  public String getUrl() {
    return "http://" + BASE_URL + user;
  }

@Override
public String getAuthUrl(String authKey) {
	return String.format("https://%s?access_token=%s&access=all", BASE_URL + user,authKey);
}
}
