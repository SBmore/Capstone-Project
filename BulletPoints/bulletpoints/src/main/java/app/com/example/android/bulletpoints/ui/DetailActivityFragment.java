package app.com.example.android.bulletpoints.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.data.ArticleContract;
import app.com.example.android.bulletpoints.data.ArticleProvider;
import app.com.example.android.textbulletpointer.BulletPointWizard;

public class DetailActivityFragment extends Fragment {
    public final static String EXTRA_ID = "extra_id";
    private static final String[] ARTICLE_COLUMNS = {
            ArticleContract.ArticleColumns._ID,
            ArticleContract.ArticleColumns.TITLE,
            ArticleContract.ArticleColumns.DESCRIPTION,
            ArticleContract.ArticleColumns.LINK,
            ArticleContract.ArticleColumns.PUB_DATE,
            ArticleContract.ArticleColumns.IMG_URL
    };

    // These are the indices for the columns in ArticleContract
    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_TITLE = 1;
    public static final int COL_ARTICLE_DESCRIPTION = 2;
    public static final int COL_ARTICLE_LINK = 3;
    public static final int COL_ARTICLE_PUB_DATE = 4;
    public static final int COL_ARTICLE_IMG_URL = 5;

    public DetailActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_activity, container, false);

        long id = -1;
        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getLong(EXTRA_ID);
        }

        // TODO: temporary to test loading a picture
        if (id > -1) {
            Cursor cursor = getContext().getContentResolver().query(
                    ArticleProvider.Articles.CONTENT_URI,
                    ARTICLE_COLUMNS,
                    ArticleContract.ArticleColumns._ID + "= ?",
                    new String[]{Long.toString(id)},
                    null);

            if (cursor != null) {
                cursor.moveToFirst();
                String img_url = cursor.getString(COL_ARTICLE_IMG_URL);
                String title = cursor.getString(COL_ARTICLE_TITLE);
                String subtitle = new java.util.Date(cursor.getLong(COL_ARTICLE_PUB_DATE)).toString();
                TextView titleTextView = (TextView) root.findViewById(R.id.detail_title);
                TextView descriptionTextView = (TextView) root.findViewById(R.id.detail_subtitle);
                ImageView view = (ImageView) root.findViewById(R.id.article_photo);

                titleTextView.setText(title);
                descriptionTextView.setText(subtitle);
                Glide.with(getContext()).load(img_url).centerCrop().into(view);
                cursor.close();
            }
        }

        BulletPointWizard bulletPointWizard = new BulletPointWizard();
        String[][] bulletPoints = bulletPointWizard.getBulletPoints();

        // Banner Ad
        AdView mAdView = (AdView) root.findViewById(R.id.detailAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        return root;
    }
}