package app.com.example.android.bulletpoints.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.data.ArticleContract;
import app.com.example.android.bulletpoints.data.QueryHelper;
import app.com.example.android.bulletpoints.ui.MainActivityFragment;

/**
 * Created by Steven on 17/05/2016 using Tutorial by Dharmang Soni
 * http://dharmangsoni.blogspot.co.uk/2014/03/collection-widget-with-event-handling.html
 */
public class WidgetDataFactory implements RemoteViewsFactory {
    private static final String TAG = WidgetDataFactory.class.getSimpleName();
    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;

    private final static int numToReturn = 5;
    private int[] mIdData;
    private String[] mStringData;
    private Context mContext;

    public WidgetDataFactory(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mIdData.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        float fontSize = 10;

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);

        if (mStringData == null) {
            view.setTextViewText(android.R.id.text1, mContext.getString(R.string.widget_no_data));
        } else {
            view.setTextViewText(android.R.id.text1, mStringData[position]);
            view.setTextColor(android.R.id.text1, Color.BLACK);
            view.setTextViewTextSize(android.R.id.text1, TypedValue.COMPLEX_UNIT_SP, fontSize);

            final Intent intent = new Intent();
            final Bundle bundle = new Bundle();

            bundle.putLong(MainActivityFragment.LAYOUT_POSITION_KEY, mIdData[position]);
            intent.putExtras(bundle);

            view.setOnClickFillInIntent(android.R.id.text1, intent);
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        final String[] projection = {
                ArticleContract.ArticleColumns._ID,
                ArticleContract.ArticleColumns.TITLE
        };

        Thread thread = new Thread() {
            public void run() {

                Cursor cursor = QueryHelper.getLatest(mContext, projection, numToReturn);
                if (cursor.moveToFirst()) {
                    int count = cursor.getCount();
                    mIdData = new int[count];
                    mStringData = new String[count];

                    int index = 0;
                    while (!cursor.isAfterLast()) {
                        mIdData[index] = cursor.getInt(COL_ID);
                        mStringData[index] = cursor.getString(COL_TITLE);

                        index++;
                        cursor.moveToNext();
                    }
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onDestroy() {

    }
}