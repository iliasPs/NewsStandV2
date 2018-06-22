package com.example.android.newsstandv2;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsListAdapter extends ArrayAdapter<News> {

    public static final String LOG_TAG = NewsListAdapter.class.getSimpleName();

    public NewsListAdapter(Activity context, ArrayList<News> news) {

        super(context,0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) ;
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item_list_view, parent, false);
        }

        News currentNew = getItem(position);
        ImageView newsImage = listItemView.findViewById(R.id.newsImage);

        if (currentNew.getNewsImage() != null) {
            Log.i(LOG_TAG, "test: image url =" + currentNew.getNewsImage());
            Picasso.with(getContext()).load(currentNew.getNewsImage()).resize(300, 250).error(R.drawable.ic_launcher_background).into(newsImage);
        } else {
            Log.i(LOG_TAG, "test: image url is null");
            newsImage.setVisibility(View.GONE);
        }

        TextView newsTitle = listItemView.findViewById(R.id.newsTitle);
        newsTitle.setText(currentNew.getNewsHeader());

        TextView newsAuthor = listItemView.findViewById(R.id.newsAuthor);
        newsAuthor.setText(currentNew.getNewsAuthor());

        TextView newsDate = listItemView.findViewById(R.id.newsDate);
        newsDate.setText(currentNew.getNewsDate());

        TextView newsCategory = listItemView.findViewById(R.id.newsCategory);
        newsCategory.setText(currentNew.getNewsCategory());

        return listItemView;
    }

}
