package edu.uea.newsapp;

import java.util.Date;
import java.util.List;
import java.util.*;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import edu.uea.newsapp.adapter.RssListAdapter;
import edu.uea.newsapp.model.RssNews;
import edu.uea.newsapp.service.*;
import edu.uea.newsapp.logic.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.*;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements NewsGetTask.NewsGetTaskListener{
	private PullToRefreshListView mPullToRefreshListView;
	private TextView mLoadingTextView;
	private Button mSettingButton;
	private RssListAdapter mRssListAdapter;
	private List<RssNews> mNewsList = null;
	private long TIME_DIFF = 2 * 1000;
	private long mLastBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRssListAdapter = new RssListAdapter(this);
        initView();
        Intent intent = new Intent(MainActivity.this,NewsDetectionService.class);
        if (!hasServiceStarted()) {
            startService(intent);        	
        }
    }
    
    protected boolean initView() {
		mSettingButton = (Button) findViewById(R.id.setting);
		mSettingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == mSettingButton) {
					Intent intent = new Intent(MainActivity.this, SettingActivity.class);
					startActivity(intent);
//					Toast.makeText(MainActivity.this, "setting clicked", Toast.LENGTH_LONG).show();
				}
			}
		});

    	mLoadingTextView = (TextView)findViewById(R.id.rss_list_load_text);
        mLoadingTextView.setVisibility(View.VISIBLE);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.rss_refreshview);
		new NewsGetTask(this, this).execute();
		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new NewsGetTask(MainActivity.this, MainActivity.this).execute();
					}

				});
		// Add an end-of-list listener
		mPullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						Toast.makeText(MainActivity.this, "Load finishd!",
								Toast.LENGTH_SHORT).show();
					}
				});
		
		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RssNews newsItem = mNewsList.get(arg2 - 1);
				if (!newsItem.readed) {
					 SharedPreferences settings = getSharedPreferences("news_read_status", 0);
					  SharedPreferences.Editor editor = settings.edit();
					  editor.putBoolean(newsItem.id, true);
					  editor.commit();
					  View readedIcon = arg1.findViewById(R.id.read_icon);
					  readedIcon.setVisibility(View.VISIBLE);
				}
				Intent intent = new Intent(MainActivity.this,
						NewsDetailActivity.class);
				intent.putExtra("list", newsItem.toJSONString());
				startActivity(intent);
			}
		});
		
		return true;
    }

	@Override
	public void onLoadFinished(List<RssNews> result) {
		mNewsList = result;
		mRssListAdapter.setData(result);
		mPullToRefreshListView.onRefreshComplete();
		mPullToRefreshListView.setAdapter(mRssListAdapter);
		mRssListAdapter.notifyDataSetChanged();
		mLoadingTextView.setVisibility(View.GONE);
	}
	
	@SuppressLint("ShowToast")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long now = new Date().getTime();
			if (now - mLastBackTime < TIME_DIFF) {
				return super.onKeyDown(keyCode, event);
			} else {
				mLastBackTime = now;
				Toast.makeText(this, R.string.repeat_to_exit, 2000).show();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean hasServiceStarted()
	 {
	  ActivityManager myManager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
	  ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
	  for(int i = 0 ; i<runningService.size();i++)
	  {
	   if(runningService.get(i).service.getClassName().toString().equals("edu.uea.newsapp.service.NewsDetectionService"))
	   {
	    return true;
	   }
	  }
	  return false;
	 }
}
