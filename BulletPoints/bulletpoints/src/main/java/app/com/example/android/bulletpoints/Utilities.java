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

    /**
     * Take in the three elements that make up the share text and format them to ensure that they
     * stay 140 characters or under to ensure the share text works with twitter
     *
     * @param text    main text that explains what is being shared
     * @param link    a url as a string that should have been shortened
     * @param hashtag the hashtag to add onto the end
     * @return the formatted share text (140 characters or less)
     */
    public static String formatShareText(String text, String link, String hashtag) {
        int lengthLimit = 140;
        String shareText = link + " " + hashtag;

        int characters = (text + " " + shareText).length();

        if (characters > lengthLimit) {
            int otherLen = shareText.length();

            text = text.substring(0, lengthLimit - otherLen - 4) + "...";
        }

        shareText = text + " " + shareText;
        return shareText;
    }
}