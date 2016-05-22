package app.com.example.android.bulletpoints.data;

import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Contract for the article Database
 */
public class ArticleContract {
    public interface ArticleColumns {

        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
        String _ID = "_id";

        @DataType(DataType.Type.TEXT) @NotNull @Unique
        String TITLE = "title";

        @DataType(DataType.Type.TEXT)
        String DESCRIPTION = "description";

        @DataType(DataType.Type.TEXT) @NotNull
        String LINK = "link";

        @DataType(DataType.Type.INTEGER) @NotNull
        String PUB_DATE = "pub_date";

        @DataType(DataType.Type.TEXT) @Nullable
        String IMG_URL = "img_url";

        @DataType(DataType.Type.TEXT)
        String BODY = "body";

        @DataType(DataType.Type.TEXT)
        String BULLETPOINT_1 = "bulletpoint_1";

        @DataType(DataType.Type.TEXT)
        String BULLETPOINT_2 = "bulletpoint_2";

        @DataType(DataType.Type.TEXT)
        String BULLETPOINT_3 = "bulletpoint_3";

        @DataType(DataType.Type.TEXT)
        String BULLETPOINT_4 = "bulletpoint_4";

        @DataType(DataType.Type.TEXT)
        String BULLETPOINT_5 = "bulletpoint_5";

        @DataType(DataType.Type.TEXT)
        String PARAGRAPH_1 = "paragraph_1";

        @DataType(DataType.Type.TEXT)
        String PARAGRAPH_2 = "paragraph_2";

        @DataType(DataType.Type.TEXT)
        String PARAGRAPH_3 = "paragraph_3";

        @DataType(DataType.Type.TEXT)
        String PARAGRAPH_4 = "paragraph_4";

        @DataType(DataType.Type.TEXT)
        String PARAGRAPH_5 = "paragraph_5";


    }
}
