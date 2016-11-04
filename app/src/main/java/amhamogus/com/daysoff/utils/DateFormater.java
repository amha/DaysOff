package amhamogus.com.daysoff.utils;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Collection of static methods that helps converts between
 * different date formats.
 */

public class DateFormater {

    public static final String TAG = "DATE_FORMATTER_LOG";

    /**
     * Helper method that adds a user selected event time
     * to a {@link DateTime} object.
     *
     * @param selectedDateTime Time value returned from time picker
     * @param date Current date
     * @return RFC3339 formatted objected that represents the start or end of an event
     */
    public static DateTime getDateTime(String selectedDateTime, Date date, String amOrPm) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar timeCalendar = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(amOrPm == "AM") {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }
        else {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }

        try {
            timeCalendar.setTime(timeFormat.parse(selectedDateTime));
        } catch (ParseException e) {
            Log.d(TAG, "Parse: " + e.getMessage());
        }

        calendar.set(Calendar.HOUR, timeCalendar.get(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        return new DateTime(format.format(calendar.getTime()));
    }
}
