package app.com.example.android.bulletpoints;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return root;
    }
}
