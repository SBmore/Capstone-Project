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

public class DetailActivityFragment extends Fragment {
    public final static String EXTRA_ID = "extra_id";
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

                setBulletPoints(root, cursor);
                titleTextView.setText(title);
                descriptionTextView.setText(subtitle);
                Glide.with(getContext()).load(img_url).centerCrop().into(view);
                cursor.close();
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

    private void setBulletPoints(View root, Cursor cursor) {
        String bulletPoint1 = cursor.getString(COL_BULLETPOINT_1);
        String bulletPoint2 = cursor.getString(COL_BULLETPOINT_2);
        String bulletPoint3 = cursor.getString(COL_BULLETPOINT_3);
        String bulletPoint4 = cursor.getString(COL_BULLETPOINT_4);
        String bulletPoint5 = cursor.getString(COL_BULLETPOINT_5);

        if (bulletPoint1 != null) {
            View bulletPointView1 = root.findViewById(R.id.bulletpoint_1);
            TextView bulletPointText1 = (TextView) bulletPointView1.findViewById(R.id.bulletpoint_text);
            bulletPointText1.setText(bulletPoint1);
        }

        if (bulletPoint2 != null) {
            View bulletPointView2 = root.findViewById(R.id.bulletpoint_2);
            TextView bulletPointText2 = (TextView) bulletPointView2.findViewById(R.id.bulletpoint_text);
            bulletPointText2.setText(bulletPoint2);
        }

        if (bulletPoint3 != null) {
            View bulletPointView3 = root.findViewById(R.id.bulletpoint_3);
            TextView bulletPointText3 = (TextView) bulletPointView3.findViewById(R.id.bulletpoint_text);
            bulletPointText3.setText(bulletPoint3);
        }

        if (bulletPoint4 != null) {
            View bulletPointView4 = root.findViewById(R.id.bulletpoint_4);
            TextView bulletPointText4 = (TextView) bulletPointView4.findViewById(R.id.bulletpoint_text);
            bulletPointText4.setText(bulletPoint4);
        }

        if (bulletPoint5 != null) {
            View bulletPointView5 = root.findViewById(R.id.bulletpoint_5);
            TextView bulletPointText5 = (TextView) bulletPointView5.findViewById(R.id.bulletpoint_text);
            bulletPointText5.setText(bulletPoint5);
        }
    }
}