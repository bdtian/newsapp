package edu.uea.newsapp.service;

import android.app.Service;  
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;
import android.content.Intent; 
import android.content.SharedPreferences;
import android.content.*;
import android.os.IBinder;  
import android.util.Log;
import android.provider.*;
import android.net.*;
import android.media.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.uea.newsapp.logic.*;
import edu.uea.newsapp.model.RssNews;
import edu.uea.newsapp.*;

public class NewsDetectionService extends Service implements NewsGetTask.NewsGetTaskListener{
	 private static final String TAG = "NewsDetectionService";   
	 private Timer mTimer = null;
	 private TimerTask mTimerTask = null;
	 private static NewsDetectionService sInstance = null;
	 public void changeTimer() {
		 mTimer.cancel();
		 startTimer();
	 }
	 
	 public static NewsDetectionService getInstance() {
		 return sInstance;
	 }
	 public void startTimer() {
	        mTimer = new Timer();
	        mTimerTask = new TimerTask() {
	            @Override
	            public void run() {
	                Log.d("AndroidTimerDemo", "timer");
	                new NewsGetTask(NewsDetectionService.this, NewsDetectionService.this).execute();
	            }
	        };

			SharedPreferences settings = getSharedPreferences("app_settings", 0);
			long newsRefreshInterval = settings.getInt("news_refresh_interval", 1);
	        mTimer.schedule(mTimerTask, 1000, newsRefreshInterval*60*1000);
	 }
	 
	 	@Override  
	    public void onCreate() {  
	        super.onCreate();  
	        sInstance = this;
	    }  
	  
	    @Override  
	    public void onStart(Intent intent, int startId) {  
	        super.onStart(intent, startId);  
	        startTimer();
	    }  
	  
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {  
	        return super.onStartCommand(intent, flags, startId);  
	    }  
	  
	    @Override  
	    public IBinder onBind(Intent intent) {  
	        return null;
	    }  
	      
	    @Override  
	    public void onDestroy() {
	        super.onDestroy();
	        mTimer.cancel();
	    }  
	    
		@Override
		public void onLoadFinished(List<RssNews> result) {
	    	for(RssNews newsItem: result) {
	    		if (!newsItem.readed) {
	    			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
	    			Notification notification = new Notification(R.drawable.university_of_east_anglia_logo,
	    					"uea open day news updated, click to view in app",
	    					System.currentTimeMillis());
	    			SharedPreferences settings = getSharedPreferences("app_settings", 0);
	    			boolean enable_sound = settings.getBoolean("enable_sound", true);
	    			boolean enable_vibrate = settings.getBoolean("enable_virbration", true);
	    			if (enable_sound) {
		    			notification.defaults |= Notification.DEFAULT_SOUND;
	    				notification.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6");
	    			} else {
		    			notification.defaults &= ~Notification.DEFAULT_SOUND;	    				
	    			}
	    			
	    			notification.flags = Notification.FLAG_AUTO_CANCEL;
	    			
	    			if (enable_vibrate) {
	    				notification.defaults |= Notification.DEFAULT_VIBRATE;
	    				long[] vibrate = {200};
	    				notification.vibrate = vibrate;
	    			} else {
		    			notification.defaults &= ~Notification.DEFAULT_VIBRATE;	    					    				
	    			}
	    			
	    			notification.flags = Notification.FLAG_AUTO_CANCEL;                
	    			Intent i = new Intent(this, MainActivity.class);
	    			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
	    			//PendingIntent
	    			PendingIntent contentIntent = PendingIntent.getActivity(
	    			        this, 
	    			        R.string.app_name, 
	    			        i, 
	    			        PendingIntent.FLAG_UPDATE_CURRENT);
	    			                 
	    			notification.setLatestEventInfo(
	    			        this,
	    			        "UEA Open Day News", 
	    			        "refrsh news received, click to view in app", 
	    			        contentIntent);
	    			
	    			nm.notify(R.string.app_name, notification);
	    			break;
	    		}
	    	}
	    }
}
