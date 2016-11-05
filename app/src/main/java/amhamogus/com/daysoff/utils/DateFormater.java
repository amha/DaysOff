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

    private static final String TAG = "DATE_FORMATTER_LOG";

    /**
     * Helper method that adds a user selected event time
     * to a {@link DateTime} object.
     *
     * @param selectedDateTime Time value returned from time picker
     * @param date             Current date
     * @return RFC3339 formatted objected that represents the start or end of an event
     */
    public static DateTime getDateTime(String selectedDateTime, Date date, String amOrPm) {

        // object we're going to pass to the server
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(date);

        // formatting for the string selectedDateTime
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        // get user selected date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        try {
            Date temp = timeFormat.parse(selectedDateTime);
            timeCalendar.setTime(timeFormat.parse(selectedDateTime));

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

        } catch (ParseException e) {
            Log.d(TAG, "Parse: " + e.getMessage());
        }

        if (amOrPm == "AM") {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new DateTime(format.format(calendar.getTime()));
    }
}
