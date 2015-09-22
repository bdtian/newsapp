package edu.uea.newsapp;

import java.util.Date;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import edu.uea.newsapp.adapter.RssListAdapter;
import edu.uea.newsapp.model.RssNews;
import edu.uea.newsapp.service.HttpUtils;
import edu.uea.newsapp.utils.XMLTools;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private PullToRefreshListView mPullToRefreshListView;
	private TextView mLoadingTextView;
	private Button mSettingButton;
	private RssListAdapter mRssListAdapter;
	private static String sRssUrl = "https://news.google.co.uk/news/feeds?pz=1&cf=all&ned=uk&hl=en&output=rss";
	private List<RssNews> mNewsList = null;
	private long TIME_DIFF = 2 * 1000;
	private long mLastBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
//				WindowManager.LayoutParams.TYPE_STATUS_BAR);

        setContentView(R.layout.activity_main);
        mRssListAdapter = new RssListAdapter(this);
        initView();
    }
    
    protected boolean initView() {
		mSettingButton = (Button) findViewById(R.id.setting);
		mSettingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == mSettingButton) {
					Toast.makeText(MainActivity.this, "setting clicked", Toast.LENGTH_LONG).show();
				}
			}
		});

    	mLoadingTextView = (TextView)findViewById(R.id.rss_list_load_text);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.rss_refreshview);
		new GetDataTask().execute();
		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new GetDataTask().execute();
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
				RssNews rssNews = mNewsList.get(arg2 - 1);
				Intent intent = new Intent(MainActivity.this,
						NewsDetailActivity.class);
				intent.putExtra("list", rssNews.toJSONString());
				startActivity(intent);
			}
		});
		
		return true;
    }
        
	/**
	 * 获取rss新闻数据
	 */
	private class GetDataTask extends AsyncTask<Integer, Void, List<RssNews>> {

		@Override
		protected void onPreExecute() {
			mLoadingTextView.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected List<RssNews> doInBackground(Integer... params) {
			String xmlStr = HttpUtils.httpGet(sRssUrl,
					"utf-8");
			List<RssNews> newsList = XMLTools.parseXML(xmlStr);
			mNewsList = newsList;
			return newsList;
		}

		@Override
		protected void onPostExecute(List<RssNews> result) {
			super.onPostExecute(result);
			mRssListAdapter.setData(result);
			mPullToRefreshListView.onRefreshComplete();
			mPullToRefreshListView.setAdapter(mRssListAdapter);
			mRssListAdapter.notifyDataSetChanged();
			mLoadingTextView.setVisibility(View.GONE);
		}
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

}
