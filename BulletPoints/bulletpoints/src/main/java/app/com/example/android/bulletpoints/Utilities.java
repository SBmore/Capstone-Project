package app.com.example.android.bulletpoints;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;

import java.io.IOException;

/**
 * Utility class that contains useful parsing and formatting methods
 */
public class Utilities {

    public static String shortenUrl(Context context, String longUrl) {
        // http://stackoverflow.com/questions/18372672/how-do-i-use-the-google-url-shortener-api-on-android
        Urlshortener.Builder builder = new Urlshortener.Builder(AndroidHttp.newCompatibleTransport(),
                GsonFactory.getDefaultInstance(), null);
        Urlshortener urlshortener = builder.build();

        com.google.api.services.urlshortener.model.Url url = new Url();
        url.setLongUrl(longUrl);
        try {
            Urlshortener.Url.Insert insert = urlshortener.url().insert(url);
            insert.setKey(context.getString(R.string.url_shortener_api_key));
            url = insert.execute();
            return url.getId();
        } catch (IOException e) {
            return null;
        }
    }
}
