package app.com.example.android.bulletpoints.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.data.ArticleDataFetcher;
import app.com.example.android.bulletpoints.data.ArticleProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final static String TAG = "MAIN_ACTIVITY_FRAGMENT";
    private Typeface mRobotoReg;
    private Typeface mRobotoThin;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mRobotoReg = Typeface.createFromAsset(getResources().getAssets(), "Roboto-Regular.ttf");
        mRobotoThin = Typeface.createFromAsset(getResources().getAssets(), "Roboto-Light.ttf");

        // Banner Ad
        AdView mAdView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        ArticleDataFetcher articleDataFetcher = new ArticleDataFetcher(getContext());
        articleDataFetcher.execute("http://feeds.skynews.com/feeds/rss/world.xml");

        // temporary to check if data is being written to db
        ContentResolver resolver = getContext().getContentResolver();

        Cursor cursor = resolver.query(
                ArticleProvider.Articles.CONTENT_URI,
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.v(TAG, cursor.getString(0));
            Log.v(TAG, cursor.getString(1));
            cursor.moveToNext();
        }

        return root;
    }
}
