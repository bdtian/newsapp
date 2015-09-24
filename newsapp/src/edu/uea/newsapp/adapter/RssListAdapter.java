package edu.uea.newsapp.adapter;

import java.util.Date;
import java.util.List;

import edu.uea.newsapp.R;
import edu.uea.newsapp.model.RssNews;
import edu.uea.newsapp.network.DownloadImg;
import edu.uea.newsapp.network.DownloadImg.ImageCalback;
import edu.uea.newsapp.utils.FileUtils;
import edu.uea.newsapp.utils.BitmapCompressTools;
import edu.uea.newsapp.utils.StringUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class RssListAdapter extends BaseAdapter {

	private Context context;

	private List<edu.uea.newsapp.model.RssNews> mList;

	public RssListAdapter(Context c) {
		this.context = c;
	}
	
	public void removeAll(){
		mList = null;
		notifyDataSetChanged();
	}

	public void setData(List<RssNews> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int getCount() {
		try {
			return mList.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.news_list_item, null);
		}
		final RssNews news = mList.get(position);
		TextView title = (TextView) convertView.findViewById(R.id.item_title);
		TextView time = (TextView) convertView.findViewById(R.id.item_time);
		final ImageView image = (ImageView) convertView
				.findViewById(R.id.item_image);
		title.setText(news.title);
		time.setText(StringUtils.formatDate(new Date(news.pubDate)));
		
		View readedIcon = convertView.findViewById(R.id.read_icon);
		if (news.readed) {
			readedIcon.setVisibility(View.VISIBLE);
		}
		
		if (FileUtils.readImgFromSdcard(news.imgName, Context.MODE_PRIVATE,
				"rssCache") != null) {
			image.setImageBitmap(BitmapCompressTools
					.decodeSampledBitmapFromByte(FileUtils.readImgFromSdcard(
							news.imgName, Context.MODE_PRIVATE, "rssCache"),
							100, 100));
		} else {
			// download image
			if (news.imgUrl.indexOf("http") != 0) {
				news.imgUrl = "http:"+news.imgUrl;
			}
			DownloadImg download = new DownloadImg(news.imgUrl);
			download.DownloadImage(new ImageCalback() {

				@Override
				public void getImage(byte[] data) {
					// save image to sdcard
					FileUtils.savaImgToSdcard(news.imgName,
							Context.MODE_PRIVATE, data, "rssCache");
					image.setImageBitmap(BitmapCompressTools
							.decodeSampledBitmapFromByte(data, 100, 100));
				}
			});
		}
		return convertView;
	}
}
