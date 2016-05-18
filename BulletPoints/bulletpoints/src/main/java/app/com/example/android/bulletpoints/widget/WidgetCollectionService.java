package app.com.example.android.bulletpoints.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Steven on 17/05/2016 using Tutorial by Dharmang Soni
 * http://dharmangsoni.blogspot.co.uk/2014/03/collection-widget-with-event-handling.html
 */
public class WidgetCollectionService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataFactory(getApplicationContext(), intent);
    }

}