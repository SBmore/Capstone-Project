package app.com.example.android.bulletpoints.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.com.example.android.bulletpoints.R;

/**
 * Created by Steven on 14/05/2016.
 */
public class DetailParagraphFragment extends DialogFragment {

    private static String mParagraphText;
    static DetailParagraphFragment newInstance(String text) {
        DetailParagraphFragment fragment = new DetailParagraphFragment();

        mParagraphText = text;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_paragraph, container);
        TextView textView = (TextView) view.findViewById(R.id.paragraph_text);
        textView.setText(mParagraphText);

        return view;
    }
}