package amhamogus.com.daysoff.ui;

import android.app.Application;
import android.content.Context;

import android.widget.TextView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.util.Date;


/**
 * Created by amhamogus on 8/28/16.
 */
public class DayDecorator implements CalendarCellDecorator {

    @Override
    public void decorate(CalendarCellView cellView, Date date){
       // Toast.makeText(this, "Testing", Toast.LENGTH_SHORT).show();

    }
}