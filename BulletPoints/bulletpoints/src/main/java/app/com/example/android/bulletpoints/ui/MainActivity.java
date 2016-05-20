package app.com.example.android.bulletpoints.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import app.com.example.android.bulletpoints.App;
import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.Utilities;

public class MainActivity extends AppCompatActivity implements DetailActivityFragment.ShareCallback,
        MainActivityFragment.widgetLaunchCallback {
    public boolean mIsTablet;
    private static Tracker mTracker;
    private ShareActionProvider mShareActionProvider;
    private static Long mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();

        mIsTablet = getResources().getBoolean(R.bool.tablet);
        setContentView(R.layout.activity_main);

        if (mIsTablet) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // If the app was launched by the widget then this will provide the ID of the article
            mId = getIntent().getLongExtra(MainActivityFragment.LAYOUT_POSITION_KEY, 0);

            // Banner Ad
            AdView mAdView = (AdView) findViewById(R.id.listAdView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            if (mAdView != null) {
                mAdView.loadAd(adRequest);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // If the app is running on a tablet then the MainActivity handles creating the share icon
        // as it also creates the toolbar
        if (mIsTablet) {
            menu.clear();
            getMenuInflater().inflate(R.menu.detail_main, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
                @Override
                public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.ga_cat_article))
                            .setAction(getString(R.string.ga_act_share))
                            .setLabel(getString(R.string.ga_lbl_sky_world))
                            .build());

                    return false;
                }
            });
        }
        return true;
    }

    /**
     * If the app is running on a tablet then the share functionality is handled by the MainActivity
     * so this callback passes back the share text once it has been prepared by the detail fragment
     * @param shareText     the text that will be shared
     */
    @Override
    public void onSharePrepared(String shareText) {
        if (mIsTablet) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(Utilities.createShareIntent(shareText));
            }
        }
    }

    /**
     * If there is a mId then the app has been launched via the widget. If this is the case then
     * the detail fragment for that ID should also be loaded
     */
    @Override
    public void onWidgetLaunch() {
        if (mId != null && mId != 0) {
            Bundle args = new Bundle();
            args.putLong(MainActivityFragment.LAYOUT_POSITION_KEY, mId);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_detail, fragment)
                    .commit();
        }
    }
}
