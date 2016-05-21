package app.com.example.android.bulletpoints.data;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.ui.DetailActivity;
import app.com.example.android.bulletpoints.ui.DetailActivityFragment;
import app.com.example.android.bulletpoints.ui.MainActivity;
import app.com.example.android.bulletpoints.ui.MainActivityFragment;

/**
 * An adaptor to link the data from the cursor loader to the recycler view
 */
public class ArticleAdaptor extends RecyclerView.Adapter<ArticleAdaptor.ViewHolder> {
    private final static String TAG = "ARTICLE_ADAPTOR";
    private final Cursor cursor;
    private final Context context;
    private final Tracker tracker;
    private Typeface robotoReg;
    private Typeface robotoThin;
    public boolean mIsTablet;

    public ArticleAdaptor(Context context, Cursor cursor, Tracker tracker) {
        robotoReg = Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Regular.ttf");
        robotoThin = Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Light.ttf");
        this.context = context;
        this.cursor = cursor;
        this.tracker = tracker;

        mIsTablet = context.getResources().getBoolean(R.bool.tablet);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArticleAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Google Analytics for article click
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(context.getString(R.string.ga_cat_article))
                        .setAction(context.getString(R.string.ga_act_click))
                        .setLabel(context.getString(R.string.ga_lbl_sky_world))
                        .build());

                final MainActivity activity = (MainActivity) context;

                if (mIsTablet) {
                    // If the app is running on a tablet then the adapter needs to load the fragment
                    // alongside the list
                    Bundle args = new Bundle();
                    args.putLong(MainActivityFragment.LAYOUT_POSITION_KEY,
                            getItemId(viewHolder.getAdapterPosition()));

                    DetailActivityFragment fragment = new DetailActivityFragment();
                    fragment.setArguments(args);

                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_detail, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(MainActivityFragment.LAYOUT_POSITION_KEY,
                            getItemId(viewHolder.getAdapterPosition()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity);
                        Bundle bundle = options.toBundle();

                        context.startActivity(intent, bundle);
                    } else {
                        context.startActivity(intent);
                    }
                }
            }
        });

        Log.v(TAG, "Adaptor view holder created");

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdaptor.ViewHolder holder, int position) {
        this.cursor.moveToPosition(position);
        String title = this.cursor.getString(MainActivityFragment.COL_ARTICLE_TITLE);
        String img_url = this.cursor.getString(MainActivityFragment.COL_ARTICLE_IMG_URL);

        Log.v(TAG, "Adaptor: " + title);

        holder.titleView.setText(title);
        holder.titleView.setTypeface(this.robotoThin);
        holder.descriptionView.setText(this.cursor.getString(MainActivityFragment.COL_ARTICLE_DESCRIPTION));
        holder.descriptionView.setTypeface(this.robotoReg);

        Glide.with(context).load(img_url).centerCrop().into(holder.thumbnailView);
    }

    @Override
    public long getItemId(int position) {
        this.cursor.moveToPosition(position);
        return this.cursor.getLong(MainActivityFragment.COL_ARTICLE_ID);
    }

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }

    /**
     * Cache of views that make up an article list item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView thumbnailView;
        public final TextView titleView;
        public final TextView descriptionView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            descriptionView = (TextView) view.findViewById(R.id.article_description);
        }
    }
}
