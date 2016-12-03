package amhamogus.com.daysoff.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;


public class DaysOffProvider extends ContentProvider {

    static final int CALENDARS = 100;

    private static final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    private CalendarDbHelper dbHelper;
    private UriMatcher uriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DaysOffContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DaysOffContract.PATH_CALENDARS, CALENDARS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new CalendarDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor mCursor;
        switch (uriMatcher.match(uri)) {
            case CALENDARS:
                mCursor = dbHelper.getReadableDatabase().query(
                        DaysOffContract.DaysOffCalendarsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return mCursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case CALENDARS:
                return DaysOffContract.DaysOffCalendarsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int returnCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long _id = db.insert(DaysOffContract.DaysOffCalendarsEntry.TABLE_NAME, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
