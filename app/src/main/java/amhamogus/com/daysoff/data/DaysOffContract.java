package amhamogus.com.daysoff.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class DaysOffContract {

    public static final String CONTENT_AUTHORITY = "amhamogus.com.daysoff.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CALENDARS = "calendars";

    static final int CALENDARS = 100;

    public static final class DaysOffCalendarsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALENDARS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALENDARS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALENDARS;

        public static final String TABLE_NAME = "calendars";

        public static final String COLUMN_CAL_KEY = "calendars_id";
        public static final String COLUMN_CAL_KIND = "calendars_kind";
        public static final String COLUMN_CAL_SUMMARY = "calendars_summary";
        public static final String COLUMN_CAL_IDENTIFIER = "calendars_identifier";


        public static Uri buildCalendarsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
