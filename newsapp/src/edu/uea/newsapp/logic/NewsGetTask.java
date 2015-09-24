package edu.uea.newsapp.logic;

import java.util.List;

import android.os.AsyncTask;
import edu.uea.newsapp.model.RssNews;
import edu.uea.newsapp.utils.HttpUtils;
import edu.uea.newsapp.utils.XMLTools;
import android.content.Context;
import android.content.SharedPreferences;

public class NewsGetTask  extends AsyncTask<Integer, Void, List<RssNews>> {
	private NewsGetTaskListener mListener = null;
	private  String mUrl = "https://news.google.co.uk/news/feeds?pz=1&cf=all&ned=uk&hl=en&output=rss";
	private Context mContext;
	
	public interface NewsGetTaskListener {
		public void onLoadFinished(List<RssNews> result);
	}
	
	public NewsGetTask(Context context, NewsGetTaskListener listener) {
		mListener = listener;
		mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected List<RssNews> doInBackground(Integer... params) {
		String xmlStr = HttpUtils.httpGet(mUrl,
				"utf-8");
		List<RssNews> newsList = XMLTools.parseXML(xmlStr);
		initNewsReadedStatus(newsList);
		return newsList;
	}

	@Override
	protected void onPostExecute(List<RssNews> result) {
		super.onPostExecute(result);
		if (mListener != null) {
			mListener.onLoadFinished(result);
		}
	}
	
    public void initNewsReadedStatus(List<RssNews> newsList) {
		 SharedPreferences settings = mContext.getSharedPreferences("news_read_status", 0);
		 for(RssNews newsItem: newsList) {
			 newsItem.readed = settings.getBoolean(newsItem.id, false);		 
		 }
    }
}
