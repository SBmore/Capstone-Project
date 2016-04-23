package app.com.example.android.bulletpoints.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Steven on 23/04/2016.
 */
@Database(version = ArticleDatabase.VERSION)
public final class ArticleDatabase {
    public static final int VERSION = 1;

    @Table(ArticleContract.ArticleColumns.class) public static final String ARTICLES = "lists";
}

