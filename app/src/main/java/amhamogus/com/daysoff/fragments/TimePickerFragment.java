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

    int hour;
    int minute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            Bundle args = getArguments();
            hour = args.getInt("HOUR");
            minute = args.getInt("MINUTE");
        } else {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR);
            minute = c.get(Calendar.MINUTE);
        }
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(
                getActivity(),
                (TimePickerDialog.OnTimeSetListener) getTargetFragment(),
                hour,
                minute,
                false);
    }
}
