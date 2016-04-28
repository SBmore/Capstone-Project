package app.com.example.android.bulletpoints.data;

import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by Steven on 23/04/2016.
 */
public class ArticleContract {
    public interface ArticleColumns {

        @DataType(DataType.Type.INTEGER) @PrimaryKey
        @AutoIncrement
        String _ID = "_id";

        @DataType(DataType.Type.TEXT) @NotNull @Unique
        String TITLE = "title";

        @DataType(DataType.Type.TEXT)
        String DESCRIPTION = "description";

        @DataType(DataType.Type.TEXT) @NotNull @Unique
        String LINK = "link";

        @DataType(DataType.Type.INTEGER) @NotNull
        String PUB_DATE = "pub_date";

        @DataType(DataType.Type.TEXT) @Nullable
        String IMG_URL = "img_url";
    }
}
