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

    @TableEndpoint(table = ArticleDatabase.ARTICLES) public static class Articles {

        @ContentUri(
                path = "articles",
                type = "vnd.android.cursor.dir/list",
                defaultSort = ArticleContract.ArticleColumns.TITLE + " ASC")
        public static final Uri ARTICLES = Uri.parse("content://" + AUTHORITY + "/articles");
    }
}
