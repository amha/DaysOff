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

        Date timeOfEvent;
        Calendar calendarForTimeOfEvent;
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        calendarForTimeOfEvent = Calendar.getInstance();

        try {
            timeOfEvent = format.parse(selectedDateTime);
            calendarForTimeOfEvent.setTime(timeOfEvent);

            Log.d(TAG, "calendarForTimeOfEvent: "
                    + "HOUR: " + calendarForTimeOfEvent.get(Calendar.HOUR_OF_DAY)
                    + "MINUTE: " + calendarForTimeOfEvent.get(Calendar.MINUTE)
                    + "YEAR: " + calendarForTimeOfEvent.get(Calendar.YEAR)
                    + "DAY OF MONTH: " + calendarForTimeOfEvent.get(Calendar.DAY_OF_MONTH));

        } catch (ParseException e) {

        }

        Calendar calendarForDate = Calendar.getInstance();
        calendarForDate.setTime(date);
        calendarForDate.set(Calendar.HOUR_OF_DAY, calendarForTimeOfEvent.get(Calendar.HOUR_OF_DAY));
        calendarForDate.set(Calendar.MINUTE, calendarForTimeOfEvent.get(Calendar.MINUTE));

        Log.d(TAG, "calendarForDate: "
                + "HOUR: " + calendarForDate.get(Calendar.HOUR)
                + "MINUTE: " + calendarForDate.get(Calendar.MINUTE)
                + "YEAR: " + calendarForDate.get(Calendar.YEAR)
                + "DAY OF MONTH: " + calendarForDate.get(Calendar.DAY_OF_MONTH));

        SimpleDateFormat rfc330Format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        rfc330Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateTime temp = new DateTime(rfc330Format.format(calendarForDate.getTime()));

        Log.d(TAG, "rfc339 Format: " + temp.toString());

        return temp;
    }

    public static String getTimeRange(String rfc339DateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(rfc339DateTime));
            String startAmOrPm = ((calendar.get(Calendar.AM_PM)) == Calendar.AM) ? "am" : "pm";

            String hour = ((calendar.get(Calendar.HOUR)) == 0) ? "12" : calendar.get(Calendar.HOUR) + "";
            return "" + hour + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)) + " " + startAmOrPm;
        } catch (ParseException exception) {
            Log.d(TAG, exception.toString());
        }
        return "bloop";
    }
}
