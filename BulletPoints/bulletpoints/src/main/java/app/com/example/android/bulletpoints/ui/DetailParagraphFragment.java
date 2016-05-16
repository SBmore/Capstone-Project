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
}