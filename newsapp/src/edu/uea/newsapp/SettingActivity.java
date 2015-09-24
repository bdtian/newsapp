package edu.uea.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import edu.uea.newsapp.service.NewsDetectionService;

public class SettingActivity extends Activity implements View.OnClickListener {
	private Button btnBack = null;
	private Button btnChangeSetting = null;
	private CheckBox soundSetting = null;
	private CheckBox vibrationSetting = null;
	private TimePicker  timePick = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		timePick=(TimePicker)findViewById(R.id.interval);
		timePick.setIs24HourView(true);
		
		btnBack = (Button) findViewById(R.id.setting_btn_back);
		btnBack.setOnClickListener(this);

		btnChangeSetting = (Button)findViewById(R.id.submit);
		btnChangeSetting.setOnClickListener(this);
		
		soundSetting	= (CheckBox)findViewById(R.id.sound);
		vibrationSetting = (CheckBox)findViewById(R.id.vibration);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setting_btn_back) {
			finish();
		} else if(v.getId() == R.id.submit) {
			changeSettings();
			finish();
		}
	}
	
	void changeSettings() {
		SharedPreferences settings = getSharedPreferences("app_settings", 0);
		int newsRefreshInterval = settings.getInt("news_refresh_interval", 1);
		boolean vibration = settings.getBoolean("enable_virbration", true);
		boolean sound = settings.getBoolean("enable_sound", true);
		
		boolean soundNow = soundSetting.isChecked();
		boolean vibrationNow = vibrationSetting.isChecked();
		
		if (vibration != vibrationNow || sound != soundNow) {
			  SharedPreferences.Editor editor = settings.edit();
			  editor.putBoolean("enable_virbration", vibration);
			  editor.putBoolean("enable_sound", sound);
			  editor.commit();			
		}
		
	    int h=timePick.getCurrentHour();  
        int m=timePick.getCurrentMinute(); 
        int newsRefreshIntervalNew = h * 60 * 60 + m * 60;
		if (newsRefreshIntervalNew != newsRefreshInterval) {
			  SharedPreferences.Editor editor = settings.edit();
			  editor.putInt("news_refresh_interval", newsRefreshIntervalNew);
			  editor.commit();	
			  NewsDetectionService.getInstance().changeTimer();
		}
	}
}
