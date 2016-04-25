package app.com.example.android.bulletpoints.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Steven on 23/04/2016.
 */
@ContentProvider(authority = ArticleProvider.AUTHORITY, database = ArticleDatabase.class)
public class ArticleProvider {
    public static final String AUTHORITY = "app.com.example.android.bulletpoints.data.ArticleProvider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String ARTICLES = "articles";
    }

    private static Uri buildUri(String... params) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (int i = 0; i < params.length; i += 1) {
            builder.appendPath(params[i]);
        }
        return builder.build();
    }

    @TableEndpoint(table = ArticleDatabase.ARTICLES)
    public static class Articles {
        @ContentUri(
                path = Path.ARTICLES,
                type = "vnd.android.cursor.dir/list",
                defaultSort = ArticleContract.ArticleColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.ARTICLES);
    }
}
