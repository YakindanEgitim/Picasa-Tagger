 package com.example.picasa_tagger;

public class AppManager {
	PicasaAccount activeAcccount;
	static AppManager inst;
	private AppManager(){
		
	}
	public static AppManager getAppManager(){
		if(inst == null){
			inst = new AppManager();
		}
		return inst;
	}
	public PicasaAccount geActiveAcccount() {
		return activeAcccount;
	}
	public void setActiveAcccount(PicasaAccount activeAcccount) {
		this.activeAcccount = activeAcccount;
	}
}
