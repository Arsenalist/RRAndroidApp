package com.raptorsrepublic.myrrapp.rrapp1;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import at.theengine.android.simple_rss2_android.RSSItem;
import at.theengine.android.simple_rss2_android.SimpleRss2Parser;
import at.theengine.android.simple_rss2_android.SimpleRss2ParserCallback;

/**
 * Created by Zarar_Siddiqi on 5/26/2014.
 */
public class WebArticlesFragment extends Fragment {

    class WebArticle {
        public String url, title, domain;
        public WebArticle(String url, String title) {
            this.url = url;
            this.title = title;
            this.domain = getDomainName(url);
        }

        private String getDomainName(String url) {
            URI uri = null;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                return null;
            }
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }
    }

    static class Holder {
        public TextView domain;
        public TextView textView;
    }

    class WebArticlesListAdapter extends ArrayAdapter<WebArticle> {
        private final List<WebArticle> items;
        private final int resourceId;
        private final Context context;

        WebArticlesListAdapter(Context context, int resourceId, List<WebArticle> items) {
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
                holder.textView = (TextView)row.findViewById(R.id.web_articles_title);
                holder.domain = (TextView)row.findViewById(R.id.web_articles_domain);
                row.setTag(holder);
            }
            else {
                holder = (Holder) row.getTag();
            }
            WebArticle webArticle = this.items.get(position);
            holder.textView.setText(webArticle.title);
            holder.domain.setText(webArticle.domain);
            return row;
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_web_articles, container, false);

        String url = "http://feeds.delicious.com/v2/json/therealrrlinks?count=25";
        new RequestTask(rootView).execute(url);


        return rootView;
    }

    class RequestTask extends AsyncTask<String, String, String>{

        private final ViewGroup context;

        public RequestTask(ViewGroup context) {
            this.context = context;

        }

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                List<WebArticle> webArticles = new ArrayList<WebArticle>();

/*
                JSONObject json = new JSONObject(result);
                JSONArray posts = json.getJSONObject("data").getJSONArray("children");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject postData = posts.getJSONObject(i).getJSONObject("data");
                    webArticles.add(new WebArticle(postData.getString("url"), postData.getString("title"), postData.getString("domain")));
                }
*/
                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject postData = json.getJSONObject(i);
                    webArticles.add(new WebArticle(postData.getString("u"), postData.getString("d")));
                }

                WebArticlesListAdapter webArticlesListAdapter =
                        new WebArticlesListAdapter(getActivity(), R.layout.list_item_web_articles, webArticles);
                ListView view = (ListView) context.findViewById(R.id.list_view_web_articles);
                view.setAdapter(webArticlesListAdapter );
                view.setOnItemClickListener(new WebArticlesLoader(webArticles));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private class WebArticlesLoader implements AdapterView.OnItemClickListener {
        private List<WebArticle> webArticles;
        public WebArticlesLoader(List<WebArticle> webArticles) {
            this.webArticles = webArticles;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent intent = new Intent(adapterView.getContext(), WebArticleActivity.class);
            intent.putExtra("article_url", this.webArticles.get(position).url);
            intent.putExtra("article_title", this.webArticles.get(position).title);
            startActivity(intent);
        }
    }

}
