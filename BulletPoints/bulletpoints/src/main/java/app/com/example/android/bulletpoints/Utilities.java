package app.com.example.android.bulletpoints;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * Take in a unix timestamp and return it as a formatted string.
     * @param unixTimestamp the timestamp to convert to a formatted string
     * @return              the formatted string
     */
    public static String formatDate(long unixTimestamp) {
        Date date = new Date(unixTimestamp);
        String format = "dd-MMMM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.UK);
        return dateFormat.format(date);
    }

    /**
     * Source:          http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     * User:            Alexandre Jasmin
     * Changes:         .getApplicationContext() to access getSystemService
     * @param context   activity context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void toastNoNetword(Context context) {
        String text = context.getString(R.string.err_no_network);
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Creates a share intent that will share the text that is passed in
     * @param shareText     the text to share
     * @return              the share intent
     */
    public static Intent createShareIntent(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }
}
