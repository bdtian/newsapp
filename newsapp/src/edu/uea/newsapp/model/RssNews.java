package edu.uea.newsapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uea.newsapp.utils.*;

public class RssNews {
	public String id;
	public String title;
	public String link;
	public String comments;
	public String description;
	public String source;
	public String pubDate;
	public String imgUrl;
	public String imgName;
	public String content;
	public boolean readed = false;

	public RssNews() {
	}

	@Override
	public String toString() {
		return "RssNews [id=" +id+", title=" + title + ", link=" + link + ", comments="
				+ comments + ", description=" + description + ", source="
				+ source + ", pubDate=" + pubDate + ", imgUrl=" + imgUrl
				+ ", imgName=" + imgName + ", content=" + content + "]";
	}

	public String toJSONString() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", id);
			jsonObject.put("title", title);
			jsonObject.put("link", link);
			jsonObject.put("comments", comments);
			jsonObject.put("description", description);
			jsonObject.put("source", source);
			jsonObject.put("pubDate", pubDate);
			jsonObject.put("imgUrl", imgUrl);
			jsonObject.put("imgName", imgName);
			jsonObject.put("content", content);
			jsonObject.put("readed", readed);
			return jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static RssNews parse(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			return RssNews.parse(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static RssNews parse(JSONObject jsonObject) {
		if (null == jsonObject) {
			return null;
		}

		RssNews news = new RssNews();
		news.title = jsonObject.optString("title");
		news.link = jsonObject.optString("link");
		news.comments = jsonObject.optString("comments");
		news.description = jsonObject.optString("description");
		news.source = jsonObject.optString("source");
		news.pubDate = jsonObject.optString("pubDate");
		news.imgUrl = jsonObject.optString("imgUrl");
		news.imgName = jsonObject.optString("imgName");
		news.id = jsonObject.optString("id");
		news.readed = jsonObject.optBoolean("readed");
		return news;
	}
}
