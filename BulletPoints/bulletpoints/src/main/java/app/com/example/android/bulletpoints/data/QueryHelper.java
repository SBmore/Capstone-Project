package app.com.example.android.bulletpoints.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Steven on 14/05/2016.
 */
public class QueryHelper {
    public static Cursor getById(Context context, String[] cols, String id) {
        return context.getContentResolver().query(
                ArticleProvider.Articles.CONTENT_URI,
                cols,
                ArticleContract.ArticleColumns._ID + "= ?",
                new String[]{id},
                null);
    }

    public static Cursor getByTitle(Context context, String[] cols, String title) {
        return context.getContentResolver().query(
                ArticleProvider.Articles.CONTENT_URI,
                cols,
                ArticleContract.ArticleColumns.TITLE + "= ?",
                new String[]{title},
                null);
    }

    public static Cursor getLatest(Context context, String[] cols, int num) {
        String sortOrder = ArticleContract.ArticleColumns.PUB_DATE + " DESC";
        String limit = " LIMIT " + num;
        return context.getContentResolver().query(
                ArticleProvider.Articles.CONTENT_URI,
                cols,
                ArticleContract.ArticleColumns.BODY + " IS NOT NULL",
                null,
                sortOrder + limit);
    }

    public static int updateByTitle(Context context, ContentValues values, String title) {
        return context.getContentResolver().update(
                ArticleProvider.Articles.CONTENT_URI,
                values,
                ArticleContract.ArticleColumns.TITLE + "= ?",
                new String[]{title}
        );
    }

    public static Uri insert(Context context, ContentValues value) {
        return context.getContentResolver().insert(
                ArticleProvider.Articles.CONTENT_URI,
                value
        );
    }

    public static int bulkInsert(Context context, ContentValues[] values) {
        return context.getContentResolver().bulkInsert(
                ArticleProvider.Articles.CONTENT_URI,
                values
        );
    }
}
