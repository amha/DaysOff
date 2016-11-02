package amhamogus.com.daysoff.utils;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import amhamogus.com.daysoff.model.DaysOffEvent;

/**
 * A collection of helper methods that makes it easier to
 * use manipulate data objects.
 */

public class CollectionHelper {

    public static List<DaysOffEvent> convertListToCollection(List<Event> events) {
        if (events != null) {
            List<DaysOffEvent> daysOffEvent = new ArrayList<>();
            for (Event e : events) {
                if (validDateFormat(e)) {
                    daysOffEvent.add(new DaysOffEvent(e));
                }
            }
            return daysOffEvent;
        } else {
            return null;
        }

    }

    // Helper method that determines if an event is longer
    // than a single day. The app currently supports
    // events that are shorter than a single day.
    public static boolean validDateFormat(Event event) {
        if (event.getStart().getDate() == null
                || event.getEnd().getDate() == null) {
            return true;
        } else {
            return false;
        }
    }
}
