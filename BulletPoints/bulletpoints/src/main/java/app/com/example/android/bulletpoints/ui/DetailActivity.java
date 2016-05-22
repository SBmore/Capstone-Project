package app.com.example.android.bulletpoints.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.com.example.android.bulletpoints.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(getIntent().getExtras());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                postponeEnterTransition();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_detail, fragment)
                    .commit();
        }
    }
}
