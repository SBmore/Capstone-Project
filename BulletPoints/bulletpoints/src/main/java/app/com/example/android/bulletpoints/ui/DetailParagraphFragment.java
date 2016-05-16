package app.com.example.android.bulletpoints.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import app.com.example.android.bulletpoints.App;
import app.com.example.android.bulletpoints.R;

/**
 * Created by Steven on 14/05/2016.
 */
public class DetailParagraphFragment extends DialogFragment {
    private final static String TAG = DetailParagraphFragment.class.getSimpleName();
    private static String mParagraphText;
    private static Tracker mTracker;

    static DetailParagraphFragment newInstance(String text) {
        DetailParagraphFragment fragment = new DetailParagraphFragment();

        mParagraphText = text;

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the shared Tracker instance.
        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_paragraph, container);
        TextView textView = (TextView) view.findViewById(R.id.paragraph_text);
        textView.setText(mParagraphText);
        textView.setContentDescription(mParagraphText + getString(R.string.paragraph_close_help));

        // Dismiss dialog if the user clicks anywhere
        getDialog().setCanceledOnTouchOutside(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailParagraphFragment.this.dismiss();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + TAG);
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}