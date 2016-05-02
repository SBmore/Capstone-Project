package app.com.example.android.bulletpoints.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.ui.DetailActivity;
import app.com.example.android.bulletpoints.ui.MainActivityFragment;

/**
 * An adaptor to link the data from the cursor loader to the recycler view
 */
public class ArticleAdaptor extends RecyclerView.Adapter<ArticleAdaptor.ViewHolder> {
    private final static String TAG = "ARTICLE_ADAPTOR";
    private final Cursor cursor;
    private final Context context;
    private Typeface robotoReg;
    private Typeface robotoThin;

    public ArticleAdaptor(Context context, Cursor cursor) {
        robotoReg = Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Regular.ttf");
        robotoThin = Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Light.ttf");
        this.context = context;
        this.cursor = cursor;

        Log.v(TAG, "Adaptor constructor called");
    }

    @Override
    public ArticleAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        // TODO: Add on click listeners to the view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                context.startActivity(intent);
            }
        });

        Log.v(TAG, "Adaptor view holder created");

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdaptor.ViewHolder holder, int position) {
        this.cursor.moveToPosition(position);
        String img_url =  this.cursor.getString(MainActivityFragment.COL_ARTICLE_IMG_URL);
        Log.v(TAG, "Adaptor: " + this.cursor.getString(MainActivityFragment.COL_ARTICLE_TITLE));

        holder.titleView.setText(this.cursor.getString(MainActivityFragment.COL_ARTICLE_TITLE));
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
