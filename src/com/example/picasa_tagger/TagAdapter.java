package com.example.picasa_tagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.apps.picview.data.Photo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class TagAdapter extends BaseAdapter {

	private Map<String, Boolean> dirtyList;
	private Photo photo;
	private boolean init = true;
	TagAdapter(Photo photo){
		this.photo = photo;
		
		dirtyList = new HashMap<String,Boolean>();
		for(String key : photo.getKeywords())
			dirtyList.put(key, true);

	}
	@Override
	public int getCount() {
		return dirtyList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return dirtyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		String[] keyset = dirtyList.keySet().toArray(new String[dirtyList.keySet().size()]);
		if(view == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.tag_list_item, parent, false);
			CheckBox checkbox = (CheckBox) view.findViewById(R.id.check_box);
			checkbox.setChecked(dirtyList.get(keyset[position]));
		}  
		TextView textView = (TextView) view.findViewById(R.id.tag_text);
		textView.setText(keyset[position]);
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.check_box);
		checkbox.setOnCheckedChangeListener(new TagCheckedChangeListener(keyset[position]));
		return view;
	}
	public void addTag(String str){
		if(!dirtyList.containsKey(str))
			dirtyList.put(str, true);
		notifyDataSetChanged();
	}
	@Override
	public void notifyDataSetChanged(){
		List<String> list = new ArrayList<String>();
		for(String key : dirtyList.keySet()){
			if(dirtyList.get(key)){
				list.add(key); 
			}
		}
		photo.setKeywords(list);
		super.notifyDataSetChanged();
	}

	public class TagCheckedChangeListener implements OnCheckedChangeListener{
		String pos;
		public TagCheckedChangeListener(String position){
			pos = position;
		}
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean check) {
			dirtyList.put(pos, check);
			notifyDataSetChanged();	
		}

	}
}
