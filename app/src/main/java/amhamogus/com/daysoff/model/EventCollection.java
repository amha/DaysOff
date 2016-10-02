package amhamogus.com.daysoff.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by amhamogus on 9/30/16.
 */

public class EventCollection implements Parcelable {

    private List<DaysOffEvent> events;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(events);
    }

    public static final Parcelable.Creator<EventCollection> CREATOR =
            new Parcelable.Creator<EventCollection>() {
                public EventCollection createFromParcel(Parcel in) {
                    return new EventCollection(in);
                }

                public EventCollection[] newArray(int size) {
                    return new EventCollection[size];
                }
            };
    private EventCollection(Parcel in){
        events = in.readArrayList(DaysOffEvent.class.getClassLoader());
    }

    public EventCollection(List<DaysOffEvent> events){
        this.events = events;
    }

    public EventCollection(){
    }


    public List<DaysOffEvent> getEvents(){
        return events;
    }

    public void setEvents(List<DaysOffEvent> events){
        this.events = events;
    }
}
