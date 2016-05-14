package app.com.example.android.bulletpoints.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.Utilities;
import app.com.example.android.bulletpoints.data.ArticleContract;
import app.com.example.android.bulletpoints.data.BulletPointDisplayData;
import app.com.example.android.bulletpoints.data.QueryHelper;

public class DetailActivityFragment extends Fragment {
    public final static String EXTRA_ID = "extra_id";
    private final static String SHARE_HASHTAG = "#BulletPoints";
    public static final int MAX_BULLETPOINTS = 5;
    private ShareActionProvider mShareActionProvider;
    private String mArticleShareText;
    private static final String[] ARTICLE_COLUMNS = {
            ArticleContract.ArticleColumns._ID,
            ArticleContract.ArticleColumns.TITLE,
            ArticleContract.ArticleColumns.DESCRIPTION,
            ArticleContract.ArticleColumns.LINK,
            ArticleContract.ArticleColumns.PUB_DATE,
            ArticleContract.ArticleColumns.IMG_URL,
            ArticleContract.ArticleColumns.BULLETPOINT_1,
            ArticleContract.ArticleColumns.BULLETPOINT_2,
            ArticleContract.ArticleColumns.BULLETPOINT_3,
            ArticleContract.ArticleColumns.BULLETPOINT_4,
            ArticleContract.ArticleColumns.BULLETPOINT_5,
            ArticleContract.ArticleColumns.PARAGRAPH_1,
            ArticleContract.ArticleColumns.PARAGRAPH_2,
            ArticleContract.ArticleColumns.PARAGRAPH_3,
            ArticleContract.ArticleColumns.PARAGRAPH_4,
            ArticleContract.ArticleColumns.PARAGRAPH_5,
    };

    // These are the indices for the columns in ArticleContract
    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_TITLE = 1;
    public static final int COL_ARTICLE_DESCRIPTION = 2;
    public static final int COL_ARTICLE_LINK = 3;
    public static final int COL_ARTICLE_PUB_DATE = 4;
    public static final int COL_ARTICLE_IMG_URL = 5;
    public static final int COL_BULLETPOINT_1 = 6;
    public static final int COL_BULLETPOINT_2 = 7;
    public static final int COL_BULLETPOINT_3 = 8;
    public static final int COL_BULLETPOINT_4 = 9;
    public static final int COL_BULLETPOINT_5 = 10;
    public static final int COL_PARAGRAPH_1 = 11;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.detail_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mArticleShareText != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        Typeface robotoReg = Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Regular.ttf");

        long id = -1;
        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getLong(EXTRA_ID);
        }

        // TODO: temporary to test loading a picture
        if (id > -1) {
            Cursor cursor = QueryHelper.getById(getContext(), ARTICLE_COLUMNS, Long.toString(id));

            if (cursor != null) {
                cursor.moveToFirst();

                final String articleLink = cursor.getString(COL_ARTICLE_LINK);
                String img_url = cursor.getString(COL_ARTICLE_IMG_URL);
                String title = cursor.getString(COL_ARTICLE_TITLE);
                String subtitle = Utilities.formatDate(cursor.getLong(COL_ARTICLE_PUB_DATE));

                TextView titleTextView = (TextView) root.findViewById(R.id.detail_title);
                titleTextView.setTypeface(robotoReg);
                titleTextView.setText(title);

                TextView descriptionTextView = (TextView) root.findViewById(R.id.detail_subtitle);
                descriptionTextView.setTypeface(robotoReg);
                descriptionTextView.setText(subtitle);

                ImageView view = (ImageView) root.findViewById(R.id.article_photo);
                Glide.with(getContext()).load(img_url).centerCrop().into(view);

                setBulletpoints(root, cursor);
                cursor.close();

                mArticleShareText = Utilities.formatShareText(title, articleLink, SHARE_HASHTAG);
                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareIntent());
                }

                root.findViewById(R.id.goto_doc_fab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(articleLink));
                        startActivity(intent);
                    }
                });
            }
        }

        // Banner Ad
        AdView mAdView = (AdView) root.findViewById(R.id.detailAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        return root;
    }

    private void setBulletpoints(View root, Cursor cursor) {
        BulletPointDisplayData[] bulletpoints = bulletpointMapper(cursor, root);

        for (int i = 0; i < bulletpoints.length; i += 1) {
            final BulletPointDisplayData bulletpoint = bulletpoints[i];
            TextView bpText = (TextView) bulletpoint.getTextView().findViewById(R.id.bulletpoint_text);
            bpText.setText(bulletpoint.getBulletpoint());

            bpText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showParagraph(bulletpoint.getParagraph());
                }
            });
        }
    }

    private BulletPointDisplayData[] bulletpointMapper(Cursor cursor, View root) {
        String bulletIdBase = "bulletpoint_";
        BulletPointDisplayData[] bulletpoints = new BulletPointDisplayData[MAX_BULLETPOINTS];

        for (int i = 0; i <= MAX_BULLETPOINTS - 1; i += 1) {
            String bulletId = bulletIdBase + (i + 1);
            int resourceId = getResources().getIdentifier(bulletId, "id", getContext().getPackageName());
            String bpoint = cursor.getString(i + COL_BULLETPOINT_1);
            String pgraph = cursor.getString(i + COL_PARAGRAPH_1);
            View view = root.findViewById(resourceId);
            bulletpoints[i] = new BulletPointDisplayData(bpoint, pgraph, view);
        }

        return bulletpoints;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mArticleShareText);
        return shareIntent;
    }

    private void showParagraph(String text) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DetailParagraphFragment paragraph = DetailParagraphFragment.newInstance(text);
        paragraph.show(fragmentManager, "paragraph");
    }
}