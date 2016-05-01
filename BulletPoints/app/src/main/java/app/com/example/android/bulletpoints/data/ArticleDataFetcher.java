package app.com.example.android.bulletpoints.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import app.com.example.android.bulletpoints.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Steven on 19/04/2016.
 */
public class ArticleDataFetcher extends AsyncTask<String, Void, RSSFeed> {
    private final static String TAG = ArticleDataFetcher.class.getSimpleName();
    private Context mContext;
    private Exception exception;

    public ArticleDataFetcher(Context context) {
        mContext = context;
    }

    protected org.mcsoxford.rss.RSSFeed doInBackground(String... urls) {
        String title;
        String description;
        URL link;
        long pubDate;
        String imageUrl;

        try {
            URL url = new URL(urls[0]);
            RSSReader theRSSReader = new RSSReader();
            org.mcsoxford.rss.RSSFeed feed = theRSSReader.load(url.toString());

            List list = feed.getItems();
            RSSItem[] items = new RSSItem[list.size()];

            ContentValues contentValues = new ContentValues(list.size());

            // get all the articles from the RSS feed
            for (int x = 0; x < list.size(); x += 1) {
                items[x] = (RSSItem) list.get(x);
                title = ((RSSItem) list.get(x)).getTitle();
                description = ((RSSItem) list.get(x)).getDescription();
                pubDate = ((RSSItem) list.get(x)).getPubDate().getTime();
                link = new URL(((RSSItem) list.get(x)).getLink().toString());

                // temporary to test writing to the database
                contentValues.put(ArticleContract.ArticleColumns.TITLE, title);
                contentValues.put(ArticleContract.ArticleColumns.DESCRIPTION, description);
                contentValues.put(ArticleContract.ArticleColumns.PUB_DATE, pubDate);
                contentValues.put(ArticleContract.ArticleColumns.IMG_URL, link.toString());
                contentValues.put(ArticleContract.ArticleColumns.LINK, link.toString());

                Cursor cursor = mContext.getContentResolver().query(
                        ArticleProvider.Articles.CONTENT_URI,
                        new String[]{ArticleContract.ArticleColumns._ID},
                        ArticleContract.ArticleColumns.TITLE + "= ?",
                        new String[]{title},
                        null);

                boolean exists = false;
                try {
                    cursor.moveToFirst();
                    exists = !cursor.isAfterLast();
                    cursor.close();
                } catch (NullPointerException e) {
                    Log.e(TAG, e.toString());
                }

                if (exists) {
                    int num =  mContext.getContentResolver().update(
                            ArticleProvider.Articles.CONTENT_URI,
                            contentValues,
                            ArticleContract.ArticleColumns.TITLE + "= ?",
                            new String[]{title}
                    );
                    Log.v(TAG, "Updated main: " + num);
                } else {
                    mContext.getContentResolver().insert(
                            ArticleProvider.Articles.CONTENT_URI,
                            contentValues
                    );
                }


                // get the link to the full article
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(link)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String elementName = mContext.getResources().getString(R.string.html_body_name);
                            Document doc = Jsoup.parse(response.body().string());
                            Element element = doc.select(elementName).first();
                            String json = element.html();
                            Gson gson = new Gson();
                            gson.toJson(json);

                            JsonParser jsonParser = new JsonParser();
                            JsonObject jo = (JsonObject) jsonParser.parse(json);

                            // variable are to be put in db when it's created. Store here for now
                            String title = jo.get("headline").getAsString();
                            String imgUrl = jo.get("image").getAsString();
                            String articleBody = jo.get("articleBody").getAsString();

                            ContentValues values = new ContentValues();
                            values.put(ArticleContract.ArticleColumns.IMG_URL, imgUrl);
                            values.put(ArticleContract.ArticleColumns.BODY, articleBody);

                           int num =  mContext.getContentResolver().update(
                                    ArticleProvider.Articles.CONTENT_URI,
                                    values,
                                    ArticleContract.ArticleColumns.TITLE + "= ?",
                                    new String[]{title}
                            );

                            Log.v(TAG, "Updated with body: " + num);

                        } else {
                            Log.e(TAG, mContext.getResources().getString(R.string.err_html_call_fail));
                        }
                    }
                });

            }

            return feed;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(org.mcsoxford.rss.RSSFeed feed) {
        // TODO: check this.exception
        // TODO: write the feed to the database
    }
}
