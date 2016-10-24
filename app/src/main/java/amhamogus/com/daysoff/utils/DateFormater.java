package amhamogus.com.daysoff.utils;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Collection of static methods that helps converts between
 * different date formats.
 */

public class DateFormater {

    public static DateTime getDateTime(Date date, int offSet) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        DateTime dateTime;

        if (offSet == 0) {
            dateTime = new DateTime(format.format(date));
        } else {
            dateTime = new DateTime(format.format(addMinutesToDate(30, date)));
        }

        return dateTime;
    }

    private static Date addMinutesToDate(int minutes, Date startingPoint) {
        final long ONE_MINUTE_IN_MILLIS = 60000;

        long currentTime = startingPoint.getTime();
        Date afterAddingMins = new Date(currentTime + (minutes * ONE_MINUTE_IN_MILLIS));

        return afterAddingMins;
    }
}
