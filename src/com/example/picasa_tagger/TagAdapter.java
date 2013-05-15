package com.example.picasa_tagger;

import java.util.List;

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

	private List<String> tags;
	TagAdapter(List<String> tags){
		this.tags = tags;

	}
	@Override
	public int getCount() {
		return tags.size();
	}

	@Override
	public Object getItem(int position) {
		return tags.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.tag_list_item, parent, false);
		}
		TextView textView = (TextView) view.findViewById(R.id.tag_text);
		textView.setText(tags.get(position));
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.check_box);
		checkbox.setChecked(true);
		checkbox.setOnCheckedChangeListener(new TagCheckedChangeListener(position));
		return view;
	}
	public void addTag(String str){
		tags.add(str);
		this.notifyDataSetChanged();
	}
	public void removeTag(int pos){
		tags.remove(pos);
		this.notifyDataSetChanged();
	}

	public class TagCheckedChangeListener implements OnCheckedChangeListener{
		int pos;
		public TagCheckedChangeListener(int position){
			pos = position;
		}
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean check) {
			if(check);
			else
				removeTag(pos);
		}

	}
}
