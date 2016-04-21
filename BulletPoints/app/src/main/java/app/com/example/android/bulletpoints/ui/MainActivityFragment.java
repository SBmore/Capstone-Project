package app.com.example.android.bulletpoints.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.data.ArticleDataFetcher;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

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
}
