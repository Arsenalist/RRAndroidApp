package com.raptorsrepublic.myrrapp.rrapp1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.theengine.android.simple_rss2_android.RSSItem;
import at.theengine.android.simple_rss2_android.SimpleRss2Parser;
import at.theengine.android.simple_rss2_android.SimpleRss2ParserCallback;

/**
 * Created by Zarar_Siddiqi on 5/26/2014.
 */
public class BlogPostsFragment extends Fragment {

    static class Holder {
        TextView textView;
        ImageView imageVIew;
    }

    class ItemBackgroundListAdapter extends ArrayAdapter<RSSItem>{
        private final List<RSSItem> items;
        private final int resourceId;
        private final Context context;

        ItemBackgroundListAdapter(Context context, int resourceId, List<RSSItem> items) {
            super(context, resourceId, items);
            this.items = items;
            this.resourceId = resourceId;
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final Holder holder;
            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(resourceId, parent, false);

                holder = new Holder();
                holder.textView = (TextView) row.findViewById(R.id.txtTitle);
                holder.imageVIew = (ImageView) row.findViewById(R.id.imgBack);
                row.setTag(holder);
            }
            else {
                holder = (Holder) row.getTag();
            }
            RSSItem rssItem = this.items.get(position);
            holder.textView.setText(rssItem.getTitle());
            holder.textView.setTextColor(Color.WHITE);
            Document document = Jsoup.parse(rssItem.getContent());
            Elements images = document.select("img");
            if (!images.isEmpty()) {
                String url = images.get(0).attr("src");
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
                ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.imageVIew.setImageDrawable(new BitmapDrawable(loadedImage));
                    }
                });
            }

            return row;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_blog_posts, container, false);

        final List<RSSItem> posts = new ArrayList<RSSItem>();

        SimpleRss2Parser parser = new SimpleRss2Parser("http://www.raptorsrepublic.com/feed",
                new SimpleRss2ParserCallback() {
                    @Override
                    public void onFeedParsed(List<RSSItem> feedItems) {
                        for (RSSItem item : feedItems) {
                            posts.add(item);
                        }
                        ItemBackgroundListAdapter itemsAdapter =
                                new ItemBackgroundListAdapter(getActivity(), R.layout.blog_post_item, posts);
                        ListView view = (ListView) rootView.findViewById(R.id.list_view_blog_posts);
                        view.setAdapter(itemsAdapter);
                        view.setOnItemClickListener(new ArticleLoader(feedItems));
                    }

                    @Override
                    public void onError(Exception ex) {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        parser.parseAsync();
        return rootView;
    }

    private class ArticleLoader implements AdapterView.OnItemClickListener {
        private List<RSSItem> rssItems;
        public ArticleLoader(List<RSSItem> rssItems) {
            this.rssItems = rssItems;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent intent = new Intent(adapterView.getContext(), BlogPostActivity.class);
            intent.putExtra("article_title", this.rssItems.get(position).getTitle());
            intent.putExtra("article_content", this.rssItems.get(position).getContent());
            startActivity(intent);
        }
    }

}
