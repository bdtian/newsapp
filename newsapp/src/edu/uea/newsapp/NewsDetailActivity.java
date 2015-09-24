package edu.uea.newsapp;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uea.newsapp.model.RssNews;
import edu.uea.newsapp.utils.FileUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

public class NewsDetailActivity extends Activity implements View.OnClickListener {
	private WebView webView;
	private ProgressBar bar;
	private Button btnBack;
	/** 新闻数据 */
	private String list;
	/** 新闻数据 */
	private RssNews news = new RssNews();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		initView();
		initData();
		webView.loadUrl(news.link);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		bar = (ProgressBar) findViewById(R.id.deatail_pb);
		bar.setMax(100);
		btnBack = (Button) findViewById(R.id.rss_btn_back);
		btnBack.setOnClickListener(this);
		
		webView = (WebView) findViewById(R.id.rss_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.setWebViewClient(new WebClient());
		webView.setWebChromeClient(new WebChrome());
	}

	private void initData() {
		list = getIntent().getExtras().getString("list");
		try {
			JSONObject jsonObject = new JSONObject(list);
			news = RssNews.parse(jsonObject);
			System.out.println("jsonObject===>" + news.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (FileUtils.readImgFromSdcard(news.imgName + ".txt",
				Context.MODE_PRIVATE, "rssCollect") != null) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class WebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	private class WebChrome extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			bar.setProgress(newProgress);
			if (newProgress == 100) {
				bar.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.rss_btn_back) {
			finish();
		}
	}
}
