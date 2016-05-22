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
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.Utilities;
import app.com.example.android.textbulletpointer.BulletPointWizard;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Fetches data from the supplied RSS feed, extracts the data and then writes it to the
 * database
 */
public class ArticleDataFetcher extends AsyncTask<String, Void, Boolean> {
    private final static String TAG = ArticleDataFetcher.class.getSimpleName();
    private Context mContext;
    private Exception exception;

    public ArticleDataFetcher(Context context) {
        mContext = context;
    }

    /**
     * Fetches data from the supplied RSS feed, extracts the data and then writes it to the
     * database
     *
     * @param urls the RSS feed links to grab data from
     * @return <code>true</code> if the data was successfully gathered and written to the
     * database. <code>false</code> otherwise.
     */
    protected Boolean doInBackground(String... urls) {
        String title;
        String description;
        String thumbnail;
        URL link;
        long pubDate;

        try {
            if (Utilities.isNetworkAvailable(mContext)) {
                URL url = new URL(urls[0]);
                RSSReader theRSSReader = new RSSReader();
                org.mcsoxford.rss.RSSFeed feed = theRSSReader.load(url.toString());

                List list = feed.getItems();

                ContentValues[] contentValues = new ContentValues[list.size()];
                int next = 0;

                // get all the articles from the RSS feed
                for (int x = 0; x < list.size(); x += 1) {
                    ContentValues values = new ContentValues();

                    title = ((RSSItem) list.get(x)).getTitle();
                    description = ((RSSItem) list.get(x)).getDescription();
                    pubDate = ((RSSItem) list.get(x)).getPubDate().getTime();
                    link = new URL(((RSSItem) list.get(x)).getLink().toString());
                    thumbnail = ((RSSItem) list.get(x)).getThumbnails().get(0).toString();

                    values.put(ArticleContract.ArticleColumns.DESCRIPTION, description);
                    values.put(ArticleContract.ArticleColumns.PUB_DATE, pubDate);
                    values.put(ArticleContract.ArticleColumns.LINK,
                            Utilities.shortenUrl(mContext, link.toString()));
                    // add default low res thumbnail in case the full size image doesn't load
                    values.put(ArticleContract.ArticleColumns.IMG_URL, thumbnail);

                    // get the link to the full article
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(link)
                            .build();

                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else if (response.isSuccessful()) {
                        String[][] bulletpointCols = {
                                {ArticleContract.ArticleColumns.BULLETPOINT_1,
                                        ArticleContract.ArticleColumns.PARAGRAPH_1},
                                {ArticleContract.ArticleColumns.BULLETPOINT_2,
                                        ArticleContract.ArticleColumns.PARAGRAPH_2},
                                {ArticleContract.ArticleColumns.BULLETPOINT_3,
                                        ArticleContract.ArticleColumns.PARAGRAPH_3},
                                {ArticleContract.ArticleColumns.BULLETPOINT_4,
                                        ArticleContract.ArticleColumns.PARAGRAPH_4},
                                {ArticleContract.ArticleColumns.BULLETPOINT_5,
                                        ArticleContract.ArticleColumns.PARAGRAPH_5}};

                        String elementName = mContext.getResources().getString(R.string.html_body_name);
                        Document doc = Jsoup.parse(response.body().string());
                        Element element = doc.select(elementName).first();
                        String json = element.html();
                        Gson gson = new Gson();
                        gson.toJson(json);

                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject) jsonParser.parse(json);

                        // variable are to be put in db when it's created. Store here for now
                        String imgUrl = jo.get("image").getAsString();
                        String articleBody = jo.get("articleBody").getAsString();

                        Log.v(TAG, "Updating img: " + imgUrl);

                        BulletPointWizard bulletPointWizard = new BulletPointWizard();
                        String[][] bulletPoints = bulletPointWizard.getBulletPoints(articleBody);

                        values.put(ArticleContract.ArticleColumns.IMG_URL, imgUrl);
                        values.put(ArticleContract.ArticleColumns.BODY, articleBody);

                        for (int i = 0; i < bulletpointCols.length; i += 1) {
                            values.put(bulletpointCols[i][0], bulletPoints[i][0]);
                            values.put(bulletpointCols[i][1], bulletPoints[i][1]);
                        }


                        String[] checkCols = new String[]{ArticleContract.ArticleColumns._ID};
                        Cursor cursor = QueryHelper.getByTitle(mContext, checkCols, title);
                        boolean exists = false;

                        // Figure out if the article is already in the database to know whether to create
                        // it or update it
                        if (cursor != null) {
                            exists = cursor.moveToFirst();
                            cursor.close();
                        }

                        if (exists) {
                            int num = QueryHelper.updateByTitle(mContext, values, title);
                            Log.v(TAG, "Updated main: " + num);
                        } else {
                            values.put(ArticleContract.ArticleColumns.TITLE, title);
                            contentValues[next] = values;
                            next++;
                        }
                    } else {
                        Log.e(TAG, mContext.getResources().getString(R.string.err_html_call_fail));
                    }
                }

                if (next > 0) {
                    QueryHelper.bulkInsert(mContext, contentValues);
                }
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            this.exception = e;
            return null;
        }
    }

    /**
     * Checks if the data fetcher had any issues and displays a relevant toast.
     *
     * @param success a boolean where <code>true</code> shows data was collected successfully
     */
    protected void onPostExecute(Boolean success) {
        if (success != null && !success) {
            Utilities.toastNoNetword(mContext);
        }
    }
}
