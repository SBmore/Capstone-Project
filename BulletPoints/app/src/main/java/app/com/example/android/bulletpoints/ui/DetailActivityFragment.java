package app.com.example.android.bulletpoints.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.data.ArticleContract;
import app.com.example.android.bulletpoints.data.ArticleProvider;

public class DetailActivityFragment extends Fragment {
    public final static String EXTRA_ID = "extra_id";

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
                    new String[]{ArticleContract.ArticleColumns.IMG_URL},
                    ArticleContract.ArticleColumns._ID + "= ?",
                    new String[]{Long.toString(id)},
                    null);

            if (cursor != null) {
                cursor.moveToFirst();
                String img_url = cursor.getString(0);
                ImageView view = (ImageView) getActivity().findViewById(R.id.article_photo);
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
}