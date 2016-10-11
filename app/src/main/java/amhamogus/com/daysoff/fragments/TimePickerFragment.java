package amhamogus.com.daysoff.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Instance of {@link DialogFragment} that displays
 * a Timepicker widget.
 */

public class TimePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(
                getActivity(),
                (TimePickerDialog.OnTimeSetListener)getTargetFragment(),
                hour,
                minute,
                false);
    }
}
