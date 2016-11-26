package com.example.fonda.nytimessearch.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fonda on 11/15/16.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

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
            LayoutInflater inflater = LayoutInflater.from(getContext());
            // inflate view, parentGroup, attach instant
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder(convertView);

            // Clear out the recycled image
            viewHolder.ivImage.setImageResource(0);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate data into the template view using the data object
        viewHolder.tvTitle.setText(article.getHeadLine());
        String thumbnail = article.getThumbNail();
        // if (!TextUtils.isEmpty(thumbnail)) {
            // Picasso.with(getContext()).load(Uri.parse(thumbnail)).into(viewHolder.ivImage);
        // } else {
            // viewHolder.ivImage = null;
        // }
        Picasso.with(getContext())
                .load(Uri.parse(thumbnail))
                .placeholder(R.drawable.nytimes_558x360)
                .into(viewHolder.ivImage);
        return convertView;
    }


    // For caching
    static class ViewHolder {
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvTitle) TextView tvTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
