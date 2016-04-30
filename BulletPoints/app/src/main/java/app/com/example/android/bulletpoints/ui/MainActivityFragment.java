package app.com.example.android.bulletpoints.ui;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.data.ArticleContract;
import app.com.example.android.bulletpoints.data.ArticleDataFetcher;
import app.com.example.android.bulletpoints.data.ArticleProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

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

        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ArticleContract.ArticleColumns.PUB_DATE + " ASC";
        String[] columns = {ArticleContract.ArticleColumns.TITLE,
                ArticleContract.ArticleColumns.DESCRIPTION,
                ArticleContract.ArticleColumns.LINK,
                ArticleContract.ArticleColumns.IMG_URL,
                ArticleContract.ArticleColumns.PUB_DATE};

        return new CursorLoader(getActivity(),
                ArticleProvider.Articles.CONTENT_URI,
                columns,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // TODO: Make adaptor then swap cursor with data
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO: Make adaptor then swap cursor with null
    }
}
