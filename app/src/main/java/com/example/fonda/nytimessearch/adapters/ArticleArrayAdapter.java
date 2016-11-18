package com.example.fonda.nytimessearch.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fonda.nytimessearch.R;
import com.example.fonda.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fonda on 11/15/16.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    // For caching
    private static class ViewHolder {
        public ImageView ivImage;
        public TextView tvTitle;
    }

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get data item for current position
        Article article = this.getItem(position);

        // Check to see if existing view is being re-used
        // If not using recycled view -> inflate the layout
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            // inflate view, parentGroup, attach instant
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            // Clear out the recycled image
            viewHolder.ivImage.setImageResource(0);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate data into the template view using the data object
        viewHolder.tvTitle.setText(article.getHeadLine());
        String thumbnail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(Uri.parse(thumbnail)).into(viewHolder.ivImage);
        }

        /*
        // Find the image view
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        // Clear out the recycled image
        imageView.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadLine());

        // Populate the thumbnail image
        // Remote download the image in the background
        String thumbnail = article.getThumbNail();
         if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(Uri.parse(thumbnail)).into(imageView);
         }
            */
        return convertView;
    }
}
