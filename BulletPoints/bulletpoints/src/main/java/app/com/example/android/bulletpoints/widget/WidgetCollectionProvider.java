package app.com.example.android.bulletpoints.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.ui.DetailActivity;

/**
 * Created by Steven on 17/05/2016 using Tutorial by Dharmang Soni
 * http://dharmangsoni.blogspot.co.uk/2014/03/collection-widget-with-event-handling.html
 */
public class WidgetCollectionProvider extends AppWidgetProvider {
    public static final String EXTRA_ID = "extra_id";
    public static final String ACTION_LAUNCH = "action_launch";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews view = initViews(context, appWidgetManager, widgetId);

            Intent intent = new Intent(context, DetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            view.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, view);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews initViews(Context context, AppWidgetManager widgetManager, int widgetId) {
        RemoteViews view = new RemoteViews(context.getPackageName(),
                R.layout.widget_collection_layout);

        Intent intent = new Intent(context, WidgetCollectionService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        view.setRemoteAdapter(widgetId, R.id.widget_data_list, intent);

        return view;
    }
}