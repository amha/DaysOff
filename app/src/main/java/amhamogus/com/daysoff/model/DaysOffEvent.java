package amhamogus.com.daysoff.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.calendar.model.Event;

import amhamogus.com.daysoff.utils.DateFormater;


public class DaysOffEvent implements Parcelable {

    private String TAG = "DAYS OFF EVENT";

    private Event eventDetails;
    String eventSummary;
    String startTime;
    String endTime;
    String desc;
    String location;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(eventSummary);
        out.writeString(startTime);
        out.writeString(endTime);
        out.writeString(desc);
        out.writeString(location);
    }

    public static final Parcelable.Creator<DaysOffEvent> CREATOR =
            new Parcelable.Creator<DaysOffEvent>() {
                @Override
                public DaysOffEvent createFromParcel(Parcel parcel) {
                    return new DaysOffEvent(parcel);
                }

                @Override
                public DaysOffEvent[] newArray(int i) {
                    return new DaysOffEvent[0];
                }
            };

    private DaysOffEvent(Parcel in) {
        this.eventSummary = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.desc = in.readString();
        this.location = in.readString();
    }

    public DaysOffEvent(Event event) {
        this.eventSummary = event.getSummary();
        this.startTime = event.getStart().getDateTime().toStringRfc3339();
        this.endTime = event.getEnd().getDateTime().toStringRfc3339();
        this.desc = event.getDescription();
        this.location = event.getLocation();
    }

    public String getEventSummary() {
        return this.eventSummary;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getLocation()

    {
        return this.location;
    }

    public String getTimeRange() {
        return DateFormater.getTimeRange(startTime)
                + " - " + DateFormater.getTimeRange(endTime);
    }
}
