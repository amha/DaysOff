package amhamogus.com.daysoff.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CalendarDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "daysOffCalendar.db";
    private static final int DATABASE_VERSION = 1;

    public CalendarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CALENDAR_LIST_TABLE = "CREATE TABLE " + DaysOffContract.DaysOffCalendarsEntry.TABLE_NAME + "(" +
                DaysOffContract.DaysOffCalendarsEntry._ID + " INTEGER PRIMARY KEY," +
                DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_SUMMARY + " TEXT NOT NULL, " +
                DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_KIND + " TEXT NOT NULL, " +
                DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_IDENTIFIER + " INTEGER, " +
                DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_KEY + " INTEGER " +
                ")";
        sqLiteDatabase.execSQL(SQL_CREATE_CALENDAR_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DaysOffContract.DaysOffCalendarsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
