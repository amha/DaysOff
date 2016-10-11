package amhamogus.com.daysoff.fragments;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.util.Date;

/**
 * Callback used to change the background color of
 * calendar cells.
 */
class DayDecorator implements CalendarCellDecorator {

    @Override
    public void decorate(CalendarCellView cellView, Date date){
       // Toast.makeText(this, "Testing", Toast.LENGTH_SHORT).show();
    }
}