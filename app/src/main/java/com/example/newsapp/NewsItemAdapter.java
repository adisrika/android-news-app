package com.example.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    private Context mContext;
    public NewsItemAdapter(@NonNull Context context, List<NewsItem> newsItems) {
        super(context, 0, newsItems);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the news item at the given position in the list of earthquakes
        NewsItem currentNewsItem = getItem(position);

        // Find the TextView with view ID section
        TextView sectionView = listItemView.findViewById(R.id.section);
        // Display the location of the current earthquake in that TextView
        sectionView.setText(currentNewsItem.getmSectionName());

        // Find the TextView with view ID title
        TextView titleView = listItemView.findViewById(R.id.title);
        // Display the location of the current earthquake in that TextView
        titleView.setText(currentNewsItem.getmTitle());

        // Find the TextView with view ID byline
        TextView bylineView = listItemView.findViewById(R.id.byline);
        bylineView.setText(getByLine(currentNewsItem.getmContributor(), currentNewsItem.getmDate()));

        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.date_format));
        return dateFormat.format(dateObject);
    }

    private String getByLine(String contributor, Date dateObject) {
        String date = (dateObject != null) ? formatDate(dateObject) : "";
        String separator = (date.length() != 0 && contributor.length() != 0) ? " - " : "";
        return contributor + separator + date;
    }
}
